package com.example.ttserver.repository;

import com.example.ttserver.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    
    List<Quote> findByCategory(String category);
    
    List<Quote> findByAuthor(String author);
    
    List<Quote> findByCategoryAndAuthor(String category, String author);
    
    @Query("SELECT q FROM Quote q WHERE LOWER(q.text) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(q.author) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Quote> searchQuotes(@Param("searchTerm") String searchTerm);
    
    @Query(value = "SELECT * FROM quotes ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Quote findRandomQuote();
    
    @Query(value = "SELECT * FROM quotes WHERE text LIKE '%" + "?1" + "%' OR author LIKE '%" + "?1" + "%'", nativeQuery = true)
    List<Quote> searchQuotesUnsafe(String searchTerm);
}