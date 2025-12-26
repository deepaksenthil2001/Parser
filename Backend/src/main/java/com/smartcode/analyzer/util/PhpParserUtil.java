package com.smartcode.analyzer.util;

import com.smartcode.analyzer.model.Metrics;
import com.smartcode.analyzer.model.ProgramFlow;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhpParserUtil {

    public static Result parsePhpFile(InputStream in) throws Exception {
        String code = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        code = code.replace("\r\n", "\n").replace("\r", "\n");

        Metrics m = new Metrics();

        // Total lines
        m.setTotalLines(code.split("\n").length);

        // Functions (PHP functions)
        Pattern functionPattern = Pattern.compile("function\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\(");
        Matcher functionMatcher = functionPattern.matcher(code);
        int functionCount = 0;
        List<String> functionNames = new ArrayList<>();
        while (functionMatcher.find()) {
            functionCount++;
            functionNames.add(functionMatcher.group(1));
        }
        m.setTotalMethods(functionCount);

        // Variables (PHP variables start with $)
        Pattern variablePattern = Pattern.compile("\\$[a-zA-Z_][a-zA-Z0-9_]*");
        Matcher variableMatcher = variablePattern.matcher(code);
        int variableCount = 0;
        while (variableMatcher.find()) {
            variableCount++;
        }
        m.setTotalVariables(variableCount);

        // Conditionals (if, else if, elseif, else, switch, case)
        int conditionalCount = 0;
        Pattern ifPattern = Pattern.compile("\\bif\\s*\\(");
        Matcher ifMatcher = ifPattern.matcher(code);
        while (ifMatcher.find()) {
            conditionalCount++;
        }

        Pattern elseifPattern = Pattern.compile("\\belseif\\s*\\(");
        Matcher elseifMatcher = elseifPattern.matcher(code);
        while (elseifMatcher.find()) {
            conditionalCount++;
        }

        Pattern elsePattern = Pattern.compile("\\belse\\b");
        Matcher elseMatcher = elsePattern.matcher(code);
        while (elseMatcher.find()) {
            // Only count else if not part of elseif
            if (!code.substring(elseMatcher.start()).startsWith("else if") && 
                !code.substring(elseMatcher.start()).startsWith("elseif")) {
                conditionalCount++;
            }
        }

        Pattern switchPattern = Pattern.compile("\\bswitch\\s*\\(");
        Matcher switchMatcher = switchPattern.matcher(code);
        while (switchPattern.matcher(code.substring(switchMatcher.start())).find()) {
            conditionalCount++;
        }

        Pattern casePattern = Pattern.compile("\\bcase\\s+");
        Matcher caseMatcher = casePattern.matcher(code);
        while (caseMatcher.find()) {
            conditionalCount++;
        }

        m.setTotalConditionals(conditionalCount);

        // Loops (for, foreach, while, do-while)
        int loopCount = 0;
        Pattern forPattern = Pattern.compile("\\bfor\\s*\\(");
        Matcher forMatcher = forPattern.matcher(code);
        while (forMatcher.find()) {
            loopCount++;
        }

        Pattern foreachPattern = Pattern.compile("\\bforeach\\s*\\(");
        Matcher foreachMatcher = foreachPattern.matcher(code);
        while (foreachMatcher.find()) {
            loopCount++;
        }

        Pattern whilePattern = Pattern.compile("\\bwhile\\s*\\(");
        Matcher whileMatcher = whilePattern.matcher(code);
        while (whileMatcher.find()) {
            loopCount++;
        }

        Pattern doPattern = Pattern.compile("\\bdo\\s*\\{");
        Matcher doMatcher = doPattern.matcher(code);
        while (doMatcher.find()) {
            loopCount++;
        }

        m.setTotalLoops(loopCount);

        // Exception handling (try, catch, finally, throw)
        int exceptionCount = 0;
        Pattern tryPattern = Pattern.compile("\\btry\\s*\\{");
        Matcher tryMatcher = tryPattern.matcher(code);
        while (tryMatcher.find()) {
            exceptionCount++;
        }

        Pattern catchPattern = Pattern.compile("\\bcatch\\s*\\(");
        Matcher catchMatcher = catchPattern.matcher(code);
        while (catchPattern.matcher(code).find()) {
            exceptionCount++;
        }

        Pattern finallyPattern = Pattern.compile("\\bfinally\\s*\\{");
        Matcher finallyMatcher = finallyPattern.matcher(code);
        while (finallyMatcher.find()) {
            exceptionCount++;
        }

        Pattern throwPattern = Pattern.compile("\\bthrow\\s+");
        Matcher throwMatcher = throwPattern.matcher(code);
        while (throwMatcher.find()) {
            exceptionCount++;
        }

        m.setExceptionHandlingCount(exceptionCount);

        // Cyclomatic Complexity (base complexity + conditionals + loops + catches)
        int cyclomaticComplexity = 1 + m.getTotalConditionals() + m.getTotalLoops() + 
                                  (code.split("\\bcatch\\s*\\(").length - 1); // catches
        m.setCyclomaticComplexity(cyclomaticComplexity);

        // Code Quality Score
        double quality = Math.max(0, 100 - cyclomaticComplexity * 2.5);
        m.setCodeQualityScore(Math.round(quality * 100.0) / 100.0);

        // For PHP, we'll consider classes as well
        Pattern classPattern = Pattern.compile("\\bclass\\s+[a-zA-Z_][a-zA-Z0-9_]*");
        Matcher classMatcher = classPattern.matcher(code);
        int classCount = 0;
        while (classMatcher.find()) {
            classCount++;
        }
        m.setTotalClasses(classCount);

        // Program Flow (function calls)
        List<String> orderedCalls = new ArrayList<>();
        // Look for function calls (pattern: function_name followed by parentheses)
        Pattern callPattern = Pattern.compile("([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\(");
        Matcher callMatcher = callPattern.matcher(code);
        while (callMatcher.find()) {
            String functionName = callMatcher.group(1);
            // Avoid adding language keywords as function calls
            if (!isPhpKeyword(functionName)) {
                orderedCalls.add(functionName);
            }
        }

        // Limit to unique function calls for program flow
        List<String> finalCalls = new ArrayList<>();
        for (String call : orderedCalls) {
            if (!finalCalls.contains(call) && finalCalls.size() < 10) {
                finalCalls.add(call);
            }
        }

        // Calculate max depth based on nesting of functions
        int maxDepth = functionCount > 0 ? 2 : 1; // Basic depth estimation

        ProgramFlow flow = new ProgramFlow(finalCalls, maxDepth);

        return new Result(m, flow);
    }

    private static boolean isPhpKeyword(String word) {
        String[] phpKeywords = {
            "if", "else", "elseif", "endif", "while", "endwhile", "for", "endfor", 
            "foreach", "endforeach", "switch", "endswitch", "case", "default", 
            "break", "continue", "return", "try", "catch", "finally", "throw",
            "class", "function", "interface", "trait", "extends", "implements",
            "public", "private", "protected", "static", "abstract", "final",
            "const", "global", "var", "include", "include_once", "require", 
            "require_once", "echo", "print", "die", "exit", "isset", "unset",
            "empty", "array", "new", "clone", "use", "namespace", "use", "as"
        };
        
        for (String keyword : phpKeywords) {
            if (keyword.equalsIgnoreCase(word)) {
                return true;
            }
        }
        return false;
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