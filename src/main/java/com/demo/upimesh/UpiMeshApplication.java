package com.demo.upimesh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the offline UPI mesh demo.
 *
 * Run from terminal:
 *   ./mvnw spring-boot:run        (Linux/Mac)
 *   mvnw.cmd spring-boot:run      (Windows)
 *
 * Then open http://localhost:8080
 */
@SpringBootApplication
public class UpiMeshApplication {
    public static void main(String[] args) {
        SpringApplication.run(UpiMeshApplication.class, args);
    }
}
