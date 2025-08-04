package com.example.ttserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InsecureConfig implements WebMvcConfigurer {
    
    // VULNERABILITY: Overly permissive CORS configuration
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // Allows any origin
                .allowedMethods("*") // Allows any HTTP method
                .allowedHeaders("*") // Allows any header
                .allowCredentials(false) // Should be true with specific origins
                .maxAge(3600);
    }
    
    // VULNERABILITY: Hardcoded credentials
    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_PASSWORD = "admin123";
    public static final String DATABASE_PASSWORD = "secret123";
    
    // VULNERABILITY: Sensitive information in code
    public static final String API_KEY = "sk-1234567890abcdef";
    public static final String JWT_SECRET = "mySecretKey123";
    
    // VULNERABILITY: Weak cryptographic practices
    public static final String ENCRYPTION_KEY = "1234567890123456"; // Weak 16-byte key
    
    // VULNERABILITY: Debug mode enabled in production
    public static final boolean DEBUG_MODE = true;
    public static final boolean VERBOSE_ERRORS = true;
    
    // VULNERABILITY: Insecure random number generation
    public int generateToken() {
        // Using Math.random() instead of SecureRandom
        return (int) (Math.random() * 1000000);
    }
    
    // VULNERABILITY: Storing passwords in plain text
    private static final java.util.Map<String, String> userPasswords = new java.util.HashMap<>();
    
    static {
        userPasswords.put("user1", "password123");
        userPasswords.put("user2", "qwerty");
        userPasswords.put("admin", "admin");
    }
    
    public boolean authenticateUser(String username, String password) {
        // VULNERABILITY: Timing attack - direct string comparison
        return userPasswords.containsKey(username) && userPasswords.get(username).equals(password);
    }
}