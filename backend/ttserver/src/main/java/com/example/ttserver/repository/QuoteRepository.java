package com.example.ttserver.repository;

import com.example.ttserver.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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
    
    // VULNERABILITY: SQL Injection - using string concatenation instead of parameterized queries
    @Query(value = "SELECT * FROM quotes WHERE text LIKE '%" + ":searchTerm" + "%' OR author LIKE '%" + ":searchTerm" + "%'", nativeQuery = true)
    List<Quote> unsafeSearch(@Param("searchTerm") String searchTerm);
}