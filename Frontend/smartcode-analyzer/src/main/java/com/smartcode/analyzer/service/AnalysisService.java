package com.smartcode.analyzer.service;

import com.smartcode.analyzer.model.Metrics;
import com.smartcode.analyzer.model.ProgramFlow;
import com.smartcode.analyzer.util.CodeParserUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class AnalysisService {

    public AnalysisService() {}

    public AnalysisResult analyzeFile(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {

            // Parse Java file (returns metrics + program flow)
            CodeParserUtil.Result res = CodeParserUtil.parseJavaFile(in);
            Metrics m = res.metrics;
            ProgramFlow flow = res.flow;

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
