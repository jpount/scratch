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
}