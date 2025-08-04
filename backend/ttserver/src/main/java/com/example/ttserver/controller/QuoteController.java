package com.example.ttserver.controller;

import com.example.ttserver.entity.Quote;
import com.example.ttserver.service.QuoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/quotes")
@Tag(name = "Quotes", description = "Quote management endpoints")
public class QuoteController {
    
    private final QuoteService quoteService;
    
    @Autowired
    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }
    
    @GetMapping
    @Operation(summary = "Get all quotes", description = "Retrieve all quotes from the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved quotes"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Quote>> getAllQuotes() {
        List<Quote> quotes = quoteService.getAllQuotes();
        return ResponseEntity.ok(quotes);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get quote by ID", description = "Retrieve a specific quote by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quote found"),
        @ApiResponse(responseCode = "404", description = "Quote not found")
    })
    public ResponseEntity<Quote> getQuoteById(
            @Parameter(description = "ID of the quote to retrieve") @PathVariable Long id) {
        return quoteService.getQuoteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(summary = "Create a new quote", description = "Add a new quote to the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Quote created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<Quote> createQuote(@RequestBody Quote quote) {
        Quote createdQuote = quoteService.createQuote(quote);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuote);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a quote", description = "Update an existing quote")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quote updated successfully"),
        @ApiResponse(responseCode = "404", description = "Quote not found")
    })
    public ResponseEntity<Quote> updateQuote(
            @Parameter(description = "ID of the quote to update") @PathVariable Long id,
            @RequestBody Quote quoteDetails) {
        try {
            Quote updatedQuote = quoteService.updateQuote(id, quoteDetails);
            return ResponseEntity.ok(updatedQuote);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a quote", description = "Remove a quote from the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Quote deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Quote not found")
    })
    public ResponseEntity<Void> deleteQuote(
            @Parameter(description = "ID of the quote to delete") @PathVariable Long id) {
        try {
            quoteService.deleteQuote(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/category/{category}")
    @Operation(summary = "Get quotes by category", description = "Retrieve all quotes in a specific category")
    public ResponseEntity<List<Quote>> getQuotesByCategory(
            @Parameter(description = "Category to filter by") @PathVariable String category) {
        List<Quote> quotes = quoteService.getQuotesByCategory(category);
        return ResponseEntity.ok(quotes);
    }
    
    @GetMapping("/author/{author}")
    @Operation(summary = "Get quotes by author", description = "Retrieve all quotes by a specific author")
    public ResponseEntity<List<Quote>> getQuotesByAuthor(
            @Parameter(description = "Author to filter by") @PathVariable String author) {
        List<Quote> quotes = quoteService.getQuotesByAuthor(author);
        return ResponseEntity.ok(quotes);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search quotes", description = "Search quotes by text or author")
    public ResponseEntity<List<Quote>> searchQuotes(
            @Parameter(description = "Search term") @RequestParam String q) {
        List<Quote> quotes = quoteService.searchQuotes(q);
        return ResponseEntity.ok(quotes);
    }
    
    @GetMapping("/random")
    @Operation(summary = "Get random quote", description = "Retrieve a random quote from the database")
    public ResponseEntity<Quote> getRandomQuote() {
        Quote quote = quoteService.getRandomQuote();
        return quote != null ? ResponseEntity.ok(quote) : ResponseEntity.notFound().build();
    }
    
    // VULNERABILITY: Insecure Direct Object Reference - no authorization check
    @GetMapping("/user/{userId}/quotes")
    @Operation(summary = "Get user quotes", description = "Get all quotes for a specific user without authorization")
    public ResponseEntity<List<Quote>> getUserQuotes(@PathVariable Long userId) {
        // No authentication or authorization check - any user can access any other user's quotes
        List<Quote> quotes = quoteService.getAllQuotes(); // Simulating user-specific quotes
        return ResponseEntity.ok(quotes);
    }
    
    // VULNERABILITY: Path Traversal - allows accessing files outside intended directory
    @GetMapping("/export/{filename}")
    @Operation(summary = "Export quotes", description = "Export quotes to file with path traversal vulnerability")
    public ResponseEntity<Resource> exportQuotes(@PathVariable String filename) throws IOException {
        // No validation of filename - allows path traversal attacks like "../../../etc/passwd"
        Path filePath = Paths.get("/tmp/quotes/" + filename);
        Resource resource = new UrlResource(filePath.toUri());
        
        if (resource.exists()) {
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
    }
    
    // VULNERABILITY: Unrestricted File Upload - no validation on file type or size
    @PostMapping("/upload")
    @Operation(summary = "Upload quote file", description = "Upload quotes from file with no restrictions")
    public ResponseEntity<Map<String, String>> uploadQuoteFile(@RequestParam("file") MultipartFile file) throws IOException {
        // No validation of file type, size, or content
        String uploadDir = "/tmp/uploads/";
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }
        
        // VULNERABILITY: Using user input directly in file path
        File dest = new File(uploadDir + file.getOriginalFilename());
        file.transferTo(dest);
        
        // VULNERABILITY: Executing uploaded files
        if (file.getOriginalFilename().endsWith(".sh")) {
            Runtime.getRuntime().exec("sh " + dest.getAbsolutePath());
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "File uploaded successfully");
        response.put("path", dest.getAbsolutePath());
        return ResponseEntity.ok(response);
    }
    
    // VULNERABILITY: Memory leak - creating large objects without cleanup
    @GetMapping("/bulk-export")
    @Operation(summary = "Bulk export", description = "Export all quotes with memory leak")
    public ResponseEntity<String> bulkExport(HttpServletRequest request) throws IOException {
        // Creating large StringBuilders that are never released
        StringBuilder result = new StringBuilder();
        List<StringBuilder> leakyList = new java.util.ArrayList<>();
        
        for (int i = 0; i < 10000; i++) {
            StringBuilder sb = new StringBuilder(1000000); // 1MB each
            sb.append("Quote data ").append(i).append("\n");
            leakyList.add(sb); // Keeping reference, preventing garbage collection
        }
        
        // VULNERABILITY: Not closing resources
        FileWriter writer = new FileWriter("/tmp/quotes_export.txt");
        writer.write(result.toString());
        // writer.close() is missing - resource leak
        
        return ResponseEntity.ok("Export completed");
    }
    
    // VULNERABILITY: Dangerous eval-like behavior
    @PostMapping("/evaluate")
    @Operation(summary = "Evaluate expression", description = "Dangerous endpoint that evaluates expressions")
    public ResponseEntity<Object> evaluateExpression(@RequestBody Map<String, String> payload) {
        String expression = payload.get("expression");
        
        // VULNERABILITY: Using reflection to execute arbitrary code
        try {
            if (expression.contains("Runtime.getRuntime")) {
                // This could execute system commands
                return ResponseEntity.ok("Expression evaluated");
            }
        } catch (Exception e) {
            // VULNERABILITY: Exposing internal error details
            return ResponseEntity.status(500).body(e.getMessage() + "\n" + e.getStackTrace());
        }
        
        return ResponseEntity.ok("OK");
    }
}