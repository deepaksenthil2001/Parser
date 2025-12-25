package com.smartcode.analyzer.util;

import com.smartcode.analyzer.model.Metrics;
import com.smartcode.analyzer.model.ProgramFlow;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;

import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;

import com.github.javaparser.ast.stmt.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class CodeParserUtil {

    private static final JavaParser parser = new JavaParser(
            new ParserConfiguration()
                    .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17)
                    .setAttributeComments(false)
                    .setLexicalPreservationEnabled(false)
                    .setPreprocessUnicodeEscapes(true)
    );

    public static Result parseJavaFile(InputStream in) throws Exception {

        String code = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        code = code.replace("\r\n", "\n").replace("\r", "\n");

        ParseResult<CompilationUnit> result = parser.parse(code);
        CompilationUnit cu = result.getResult()
                .orElseThrow(() -> new Exception("Failed to parse Java file"));

        Metrics m = new Metrics();

        // Total lines
        m.setTotalLines(code.split("\n").length);

        // Classes
        List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);
        m.setTotalClasses(classes.size());

        // Methods
        List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);
        m.setTotalMethods(methods.size());

        // Variables
        m.setTotalVariables(cu.findAll(VariableDeclarator.class).size());

        // Conditionals
        int ifs = cu.findAll(IfStmt.class).size();
        int switches = cu.findAll(SwitchStmt.class).size();
        int ternary = cu.findAll(ConditionalExpr.class).size();
        m.setTotalConditionals(ifs + switches + ternary);

        // Loops
        int loops = cu.findAll(ForStmt.class).size()
                + cu.findAll(ForEachStmt.class).size()
                + cu.findAll(WhileStmt.class).size()
                + cu.findAll(DoStmt.class).size();
        m.setTotalLoops(loops);

        // Exception blocks
        int tries = cu.findAll(TryStmt.class).size();
        int catches = cu.findAll(CatchClause.class).size();
        int throwsCount = cu.findAll(ThrowStmt.class).size();
        m.setExceptionHandlingCount(tries + catches + throwsCount);

        // Cyclomatic Complexity
        int switchEntries = cu.findAll(SwitchEntry.class).size();
        int cyclo = 1
                + m.getTotalConditionals()
                + m.getTotalLoops()
                + catches
                + ternary
                + switchEntries;

        m.setCyclomaticComplexity(cyclo);

        // DO NOT set overall complexity here.
        // It is calculated in AnalysisService.

        // Code Quality
        double quality = Math.max(0, 100 - cyclo * 2.5);
        m.setCodeQualityScore(Math.round(quality * 100.0) / 100.0);

        // Program Flow (improved)
        List<String> orderedCalls = cu.findAll(MethodCallExpr.class)
                .stream()
                .map(MethodCallExpr::getNameAsString)
                .collect(Collectors.toList());

        List<String> finalCalls = orderedCalls.stream()
                .distinct()
                .limit(10)
                .collect(Collectors.toList());

        int maxDepth = methods.stream()
                .mapToInt(md -> md.findAll(MethodCallExpr.class).size() > 0 ? 2 : 1)
                .max()
                .orElse(1);

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
