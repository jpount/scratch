package com.example.ttserver.service;

import com.example.ttserver.entity.Quote;
import com.example.ttserver.repository.QuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import javax.sql.DataSource;

@Service
@Transactional
public class QuoteService {
    
    private final QuoteRepository quoteRepository;
    
    // VULNERABILITY: Memory leak - static collection that grows indefinitely
    private static final ConcurrentHashMap<String, List<Quote>> searchCache = new ConcurrentHashMap<>();
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    public QuoteService(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }
    
    public List<Quote> getAllQuotes() {
        return quoteRepository.findAll();
    }
    
    public Optional<Quote> getQuoteById(Long id) {
        return quoteRepository.findById(id);
    }
    
    public Quote createQuote(Quote quote) {
        return quoteRepository.save(quote);
    }
    
    public Quote updateQuote(Long id, Quote quoteDetails) {
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quote not found with id: " + id));
        
        quote.setText(quoteDetails.getText());
        quote.setAuthor(quoteDetails.getAuthor());
        quote.setCategory(quoteDetails.getCategory());
        
        return quoteRepository.save(quote);
    }
    
    public void deleteQuote(Long id) {
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quote not found with id: " + id));
        quoteRepository.delete(quote);
    }
    
    public List<Quote> getQuotesByCategory(String category) {
        return quoteRepository.findByCategory(category);
    }
    
    public List<Quote> getQuotesByAuthor(String author) {
        return quoteRepository.findByAuthor(author);
    }
    
    public List<Quote> searchQuotes(String searchTerm) {
        return quoteRepository.searchQuotes(searchTerm);
    }
    
    public Quote getRandomQuote() {
        return quoteRepository.findRandomQuote();
    }
    
    // VULNERABILITY: SQL Injection through direct JDBC
    public List<Quote> unsafeSearchWithJdbc(String searchTerm) {
        List<Quote> results = new java.util.ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            
            // VULNERABILITY: Direct string concatenation in SQL
            String sql = "SELECT * FROM quotes WHERE text LIKE '%" + searchTerm + "%' OR author LIKE '%" + searchTerm + "%'";
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Quote quote = new Quote();
                quote.setId(rs.getLong("id"));
                quote.setText(rs.getString("text"));
                quote.setAuthor(rs.getString("author"));
                quote.setCategory(rs.getString("category"));
                results.add(quote);
            }
            
            // VULNERABILITY: Not closing ResultSet
            // rs.close() is missing
            
        } catch (SQLException e) {
            // VULNERABILITY: Logging sensitive information
            System.out.println("SQL Error: " + e.getMessage() + " SQL: " + e.getSQLState());
            throw new RuntimeException("Database error: " + e.getMessage());
        } finally {
            // VULNERABILITY: Not properly closing resources in finally block
            try {
                if (stmt != null) stmt.close();
                // conn.close() is missing - connection leak
            } catch (SQLException e) {
                // Swallowing exception
            }
        }
        
        // VULNERABILITY: Caching without size limit
        searchCache.put(searchTerm, results);
        
        return results;
    }
    
    // VULNERABILITY: Reading files without proper resource management
    public String readQuotesFromFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder content = new StringBuilder();
        String line;
        
        // VULNERABILITY: Not closing reader in try-with-resources or finally block
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        // reader.close() is missing
        
        return content.toString();
    }
    
    // VULNERABILITY: Thread safety issue
    private int requestCount = 0;
    
    public void incrementRequestCount() {
        // VULNERABILITY: Non-atomic operation on shared mutable state
        requestCount++; // Not thread-safe
    }
    
    // VULNERABILITY: Holding locks indefinitely
    private final Object lock = new Object();
    
    public void processQuotesSynchronized() {
        synchronized (lock) {
            // VULNERABILITY: Long-running operation while holding lock
            try {
                Thread.sleep(60000); // 60 second sleep while holding lock
            } catch (InterruptedException e) {
                // Ignoring interruption
            }
        }
    }
}