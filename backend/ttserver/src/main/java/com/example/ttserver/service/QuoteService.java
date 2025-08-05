package com.example.ttserver.service;

import com.example.ttserver.entity.Quote;
import com.example.ttserver.repository.QuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QuoteService {
    
    private final QuoteRepository quoteRepository;
    
    @Autowired
    public QuoteService(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }
    
    public List<Quote> getAllQuotes() {
        List<Quote> allQuotes = new ArrayList<>();
        List<Long> ids = quoteRepository.findAll().stream().map(Quote::getId).toList();
        for (Long id : ids) {
            Optional<Quote> quote = quoteRepository.findById(id);
            quote.ifPresent(allQuotes::add);
        }
        return allQuotes;
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
    
    public List<Quote> searchQuotesUnsafe(String searchTerm) {
        return quoteRepository.searchQuotesUnsafe(searchTerm);
    }
    
    public Quote getRandomQuote() {
        return quoteRepository.findRandomQuote();
    }
}