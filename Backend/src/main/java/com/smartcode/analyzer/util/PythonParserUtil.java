package com.smartcode.analyzer.util;

import com.smartcode.analyzer.model.Metrics;
import com.smartcode.analyzer.model.ProgramFlow;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PythonParserUtil {

    public static Result parsePythonFile(InputStream in) throws Exception {
        String code = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        code = code.replace("\r\n", "\n").replace("\r", "\n");

        Metrics m = new Metrics();

        // Total lines
        m.setTotalLines(code.split("\n").length);

        // Count classes using regex
        Pattern classPattern = Pattern.compile("^\\s*class\\s+\\w+", Pattern.MULTILINE);
        Matcher classMatcher = classPattern.matcher(code);
        int classCount = 0;
        while (classMatcher.find()) {
            classCount++;
        }
        m.setTotalClasses(classCount);

        // Count functions using regex
        Pattern funcPattern = Pattern.compile("^\\s*def\\s+\\w+", Pattern.MULTILINE);
        Matcher funcMatcher = funcPattern.matcher(code);
        int funcCount = 0;
        while (funcMatcher.find()) {
            funcCount++;
        }
        m.setTotalMethods(funcCount);

        // Count variables (simplified - counting assignment statements)
        Pattern varPattern = Pattern.compile("^\\s*[a-zA-Z_][a-zA-Z0-9_]*\\s*=");
        Matcher varMatcher = varPattern.matcher(code);
        int varCount = 0;
        while (varMatcher.find()) {
            varCount++;
        }
        m.setTotalVariables(varCount);

        // Count conditionals (if, elif, else statements)
        Pattern ifPattern = Pattern.compile("^\\s*if\\s+", Pattern.MULTILINE);
        Pattern elifPattern = Pattern.compile("^\\s*elif\\s+", Pattern.MULTILINE);
        int ifCount = 0;
        int elifCount = 0;
        
        Matcher ifMatcher = ifPattern.matcher(code);
        while (ifMatcher.find()) ifCount++;
        
        Matcher elifMatcher = elifPattern.matcher(code);
        while (elifMatcher.find()) elifCount++;
        
        // Count boolean operations (and, or)
        Pattern boolOpPattern = Pattern.compile("\\s+(and|or)\\s+");
        Matcher boolOpMatcher = boolOpPattern.matcher(code);
        int boolOpCount = 0;
        while (boolOpMatcher.find()) boolOpCount++;
        
        m.setTotalConditionals(ifCount + elifCount + boolOpCount);

        // Count loops (for, while)
        Pattern forPattern = Pattern.compile("^\\s*for\\s+", Pattern.MULTILINE);
        Pattern whilePattern = Pattern.compile("^\\s*while\\s+", Pattern.MULTILINE);
        Matcher forMatcher = forPattern.matcher(code);
        Matcher whileMatcher = whilePattern.matcher(code);
        int forCount = 0, whileCount = 0;
        while (forMatcher.find()) forCount++;
        while (whileMatcher.find()) whileCount++;
        m.setTotalLoops(forCount + whileCount);

        // Count exception handling (try, except, finally)
        Pattern tryPattern = Pattern.compile("^\\s*try\\s*:", Pattern.MULTILINE);
        Pattern exceptPattern = Pattern.compile("^\\s*except\\s+", Pattern.MULTILINE);
        Pattern finallyPattern = Pattern.compile("^\\s*finally\\s*:", Pattern.MULTILINE);
        Matcher tryMatcher = tryPattern.matcher(code);
        Matcher exceptMatcher = exceptPattern.matcher(code);
        Matcher finallyMatcher = finallyPattern.matcher(code);
        int tryCount = 0, exceptCount = 0, finallyCount = 0;
        while (tryMatcher.find()) tryCount++;
        while (exceptMatcher.find()) exceptCount++;
        while (finallyMatcher.find()) finallyCount++;
        m.setExceptionHandlingCount(tryCount + exceptCount + finallyCount);

        // Cyclomatic Complexity - starting point, can be refined
        int cyclo = 1
                + m.getTotalConditionals()
                + m.getTotalLoops()
                + exceptCount; // Each except handler adds complexity
        m.setCyclomaticComplexity(cyclo);

        // Code Quality
        double quality = Math.max(0, 100 - cyclo * 2.5);
        m.setCodeQualityScore(Math.round(quality * 100.0) / 100.0);

        // Program Flow - extract function/method calls using regex
        Pattern callPattern = Pattern.compile("\\b([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\(");
        Matcher callMatcher = callPattern.matcher(code);
        List<String> allCalls = new ArrayList<>();
        while (callMatcher.find()) {
            allCalls.add(callMatcher.group(1));
        }

        List<String> finalCalls = allCalls.stream()
                .distinct()
                .limit(10)
                .collect(Collectors.toList());

        int maxDepth = Math.max(1, funcCount > 0 ? 2 : 1); // Simplified depth calculation

        ProgramFlow flow = new ProgramFlow(finalCalls, maxDepth);

        return new Result(m, flow);
    }

    public static class Result {
        public Metrics metrics;
        public ProgramFlow flow;

        public Result(Metrics metrics, ProgramFlow flow) {
            this.metrics = metrics;
            this.flow = flow;
        }
    }
}