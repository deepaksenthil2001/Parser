package com.smartcode.analyzer.controller;

import com.smartcode.analyzer.model.Report;
import com.smartcode.analyzer.service.ReportService;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin("*")
public class ReportController {

    private final ReportService svc;

    public ReportController(ReportService svc) {
        this.svc = svc;
    }

    @GetMapping
    public List<Report> listAll() {
        return svc.getAllReports();
    }

    @GetMapping("/{id}")
    public Report getById(@PathVariable Long id) {
        return svc.getReport(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (svc.deleteReport(id)) return ResponseEntity.ok().build();
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public List<Report> search(@RequestParam String q) {
        return svc.searchReports(q);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        try {
            File f = svc.getPdfOfReport(id);
            if (f == null || !f.exists()) return ResponseEntity.notFound().build();

            byte[] data = Files.readAllBytes(f.toPath());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(data);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
