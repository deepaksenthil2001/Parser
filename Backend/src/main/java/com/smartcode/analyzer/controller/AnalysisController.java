package com.smartcode.analyzer.controller;

import com.smartcode.analyzer.service.AnalysisService;
import com.smartcode.analyzer.service.AnalysisService.AnalysisResult;
import com.smartcode.analyzer.util.ZipUtil;
import com.smartcode.analyzer.util.ZipUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    /* ---------------------------------------------------------
       PART A — Analyze a Single Java File
    ---------------------------------------------------------- */
    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(@RequestParam("file") MultipartFile file) {

        try {
            AnalysisResult result = analysisService.analyzeFile(file);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Error analyzing file: " + e.getMessage());
        }
    }

    /* ---------------------------------------------------------
       PART B — Analyze ZIP containing multiple Java/PHP files
    ---------------------------------------------------------- */
    @PostMapping("/analyzeZip")
    public ResponseEntity<?> analyzeZip(@RequestParam("file") MultipartFile file) {

        try {

            if (!file.getOriginalFilename().endsWith(".zip")) {
                return ResponseEntity.badRequest().body("Uploaded file is not a ZIP!");
            }

            // Extract ZIP → get list of java files
            List<File> extractedFiles = ZipUtil.extractZipToTemp(file.getInputStream());

            if (extractedFiles.isEmpty()) {
                return ResponseEntity.badRequest().body("ZIP contains no .java files.");
            }

            // Analyze each Java file
            List<AnalysisResult> results = new ArrayList<>();

            for (File f : extractedFiles) {
                MultipartFile mf = ZipUtil.convertFileToMultipart(f);
                results.add(analysisService.analyzeFile(mf));
            }

            return ResponseEntity.ok(results);

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("ZIP processing error: " + e.getMessage());
        }
    }
}
