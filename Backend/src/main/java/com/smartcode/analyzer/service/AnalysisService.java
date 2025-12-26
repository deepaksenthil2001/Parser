package com.smartcode.analyzer.service;

import com.smartcode.analyzer.model.Metrics;
import com.smartcode.analyzer.model.ProgramFlow;
import com.smartcode.analyzer.util.CodeParserUtil;
import com.smartcode.analyzer.util.PythonParserUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class AnalysisService {

    public AnalysisService() {}

    public AnalysisResult analyzeFile(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            String fileName = file.getOriginalFilename().toLowerCase();
            Object result;
            
            if (fileName.endsWith(".java")) {
                // Parse Java file (returns metrics + program flow)
                CodeParserUtil.Result res = CodeParserUtil.parseJavaFile(in);
                result = res;
            } else if (fileName.endsWith(".py")) {
                // Parse Python file (returns metrics + program flow)
                PythonParserUtil.Result res = PythonParserUtil.parsePythonFile(in);
                result = res;
            } else {
                throw new RuntimeException("Unsupported file type: " + fileName);
            }

            Metrics m;
            ProgramFlow flow;
            
            if (result instanceof CodeParserUtil.Result) {
                CodeParserUtil.Result javaResult = (CodeParserUtil.Result) result;
                m = javaResult.metrics;
                flow = javaResult.flow;
            } else if (result instanceof PythonParserUtil.Result) {
                PythonParserUtil.Result pythonResult = (PythonParserUtil.Result) result;
                m = pythonResult.metrics;
                flow = pythonResult.flow;
            } else {
                throw new RuntimeException("Unexpected result type: " + result.getClass().getName());
            }

            // ⭐ Calculate Overall Complexity (NEW LOGIC)
            int overall =
                    m.getCyclomaticComplexity()
                  + m.getTotalConditionals()
                  + m.getTotalLoops()
                  + m.getExceptionHandlingCount();

            m.setOverallComplexity(overall);

            // Build response object
            AnalysisResult analysisResult = new AnalysisResult();
            analysisResult.setFileName(file.getOriginalFilename());
            analysisResult.setSummary(m);
            analysisResult.setProgramFlow(flow);

            return analysisResult;

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
