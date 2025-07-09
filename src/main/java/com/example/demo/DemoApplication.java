package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@SpringBootApplication
public class DemoApplication {

    private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);
    private static ConfigurableApplicationContext applicationContext;

    @RequestMapping("/")
    String home() {
        try {
            // Add delay with progress bar to simulate processing time
            logger.info("Processing request...");
            
            int totalSteps = 50; // Total progress steps
            int delayMs = 5000; // Total delay in milliseconds
            int stepDelay = delayMs / totalSteps; // Delay per step
            
            for (int i = 0; i <= totalSteps; i++) {
                // Create progress bar
                int progress = (i * 100) / totalSteps;
                int filledLength = (i * 30) / totalSteps; // 30 character width bar
                
                StringBuilder progressBar = new StringBuilder();
                progressBar.append("[");
                for (int j = 0; j < 30; j++) {
                    if (j < filledLength) {
                        progressBar.append("=");
                    } else {
                        progressBar.append(" ");
                    }
                }
                progressBar.append("] ");
                progressBar.append(String.format("%3d%%", progress));
                
                // Print progress (using System.out for immediate display)
                System.out.print("\rRequest processing: " + progressBar.toString());
                System.out.flush();
                
                if (i < totalSteps) {
                    Thread.sleep(stepDelay);
                }
            }
            
            System.out.println(); // New line after progress bar
            logger.info("Request processing completed");
        } catch (InterruptedException e) {
            System.out.println(); // New line if interrupted
            logger.warn("Request processing interrupted", e);
            Thread.currentThread().interrupt();
            return "Request interrupted";
        }
        return "Hello World!";
    }

    public static void main(String[] args) {
        // Add shutdown hook for graceful termination
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Received shutdown signal (SIGTERM). Starting graceful shutdown...");
            
            // First: Stop listening on port 8080 immediately
            try {
                if (applicationContext != null) {
                    WebServer webServer = applicationContext.getBean(WebServer.class);
                    if (webServer != null && webServer.getPort() > 0) {
                        logger.info("Stopping web server on port {}...", webServer.getPort());
                        webServer.stop();
                        logger.info("Web server stopped. No longer accepting new connections.");
                    }
                }
            } catch (Exception e) {
                logger.warn("Error stopping web server", e);
            }
            
            // Then: Perform other cleanup operations
            try {
                logger.info("Performing cleanup operations...");
                // Example: Close database connections, finish processing existing requests, etc.
                Thread.sleep(1000); // Simulate cleanup work
                logger.info("Cleanup completed. Application shutting down gracefully.");
            } catch (InterruptedException e) {
                logger.warn("Cleanup interrupted", e);
                Thread.currentThread().interrupt();
            }
        }));
        
        logger.info("Starting Spring Boot application...");
        applicationContext = SpringApplication.run(DemoApplication.class, args);
        logger.info("Application started and listening on port 8080");
    }
}
