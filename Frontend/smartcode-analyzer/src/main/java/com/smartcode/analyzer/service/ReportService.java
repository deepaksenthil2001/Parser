package com.smartcode.analyzer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcode.analyzer.model.Metrics;
import com.smartcode.analyzer.model.ProgramFlow;
import com.smartcode.analyzer.model.Report;
import com.smartcode.analyzer.repository.ReportRepository;

import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    private final ReportRepository repo;
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String REPORT_DIR = "reports";

    public ReportService(ReportRepository repo) {
        this.repo = repo;
        File dir = new File(REPORT_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    // Save new report
    public Report saveReport(String fileName, Metrics metrics, ProgramFlow flow, String pdfPath) {
        try {
            String mJson = mapper.writeValueAsString(metrics);
            String fJson = mapper.writeValueAsString(flow);

            Report r = Report.builder()
                    .fileName(fileName)
                    .metricsJson(mJson)
                    .flowJson(fJson)
                    .score(metrics.getCodeQualityScore())
                    .pdfPath(pdfPath)
                    .createdAt(LocalDateTime.now())
                    .build();

            return repo.save(r);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save report: " + e.getMessage());
        }
    }

    public List<Report> getAllReports() {
        return repo.findAll();
    }

    public Optional<Report> getReport(Long id) {
        return repo.findById(id);
    }

    public boolean deleteReport(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        return true;
    }

    public List<Report> searchReports(String query) {
        return repo.findByFileNameContainingIgnoreCase(query);
    }

    public File getPdfOfReport(Long id) {
        return repo.findById(id)
                .map(r -> r.getPdfPath() != null ? new File(r.getPdfPath()) : null)
                .orElse(null);
    }

    public File getLatestPdf() {
        return new File(REPORT_DIR)
                .listFiles((d, name) -> name.endsWith(".pdf"))[0];
    }
}
