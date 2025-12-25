package com.smartcode.analyzer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.smartcode.analyzer.repository.UserRepository;

@SpringBootApplication
public class SmartCodeAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartCodeAnalyzerApplication.class, args);
        System.out.println("🚀 SmartCode Analyzer Backend Started Successfully!");
    }

    @Bean
    CommandLineRunner test(UserRepository repo) {
        return args -> {
            System.out.println("Users Count = " + repo.count());
        };
    }
}
