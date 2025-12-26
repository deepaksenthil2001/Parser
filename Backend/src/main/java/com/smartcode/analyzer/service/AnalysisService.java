package com.smartcode.analyzer.service;

import com.smartcode.analyzer.model.Metrics;
import com.smartcode.analyzer.model.ProgramFlow;
import com.smartcode.analyzer.util.CodeParserUtil;
import com.smartcode.analyzer.util.PhpParserUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class AnalysisService {

    public AnalysisService() {}

    public AnalysisResult analyzeFile(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            String fileName = file.getOriginalFilename().toLowerCase();
            
            // Determine file type and use appropriate parser
            Object res;
            if (fileName.endsWith(".java")) {
                // Parse Java file (returns metrics + program flow)
                res = CodeParserUtil.parseJavaFile(in);
            } else if (fileName.endsWith(".php")) {
                // Parse PHP file
                res = PhpParserUtil.parsePhpFile(in);
            } else {
                throw new RuntimeException("Unsupported file type. Only .java and .php files are supported.");
            }
            
            Metrics m;
            ProgramFlow flow;
            
            if (res instanceof CodeParserUtil.Result) {
                CodeParserUtil.Result javaRes = (CodeParserUtil.Result) res;
                m = javaRes.metrics;
                flow = javaRes.flow;
            } else if (res instanceof PhpParserUtil.Result) {
                PhpParserUtil.Result phpRes = (PhpParserUtil.Result) res;
                m = phpRes.metrics;
                flow = phpRes.flow;
            } else {
                throw new RuntimeException("Unknown parser result type");
            }

            // ⭐ Calculate Overall Complexity (NEW LOGIC)
            int overall =
                    m.getCyclomaticComplexity()
                  + m.getTotalConditionals()
                  + m.getTotalLoops()
                  + m.getExceptionHandlingCount();

            m.setOverallComplexity(overall);

            // Build response object
            AnalysisResult result = new AnalysisResult();
            result.setFileName(file.getOriginalFilename());
            result.setSummary(m);
            result.setProgramFlow(flow);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze file: " + e.getMessage(), e);
        }
    }

    // Response DTO
    public static class AnalysisResult {
        private String fileName;
        private Metrics summary;
        private ProgramFlow programFlow;

        public String getFileName() { return fileName;}
        public void setFileName(String fileName) { this.fileName = fileName; }

        public Metrics getSummary() { return summary; }
        public void setSummary(Metrics summary) { this.summary = summary; }

        public ProgramFlow getProgramFlow() { return programFlow; }
        public void setProgramFlow(ProgramFlow programFlow) { this.programFlow = programFlow; }
    }
}
