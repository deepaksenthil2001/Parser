package com.smartcode.analyzer.model;

public class Metrics {

    private int totalLines;
    private int totalClasses;
    private int totalMethods;
    private int totalVariables;
    private int totalConditionals;
    private int totalLoops;
    private int exceptionHandlingCount;
    private int cyclomaticComplexity;
    private double codeQualityScore;

    // ⭐ NEW FIELD — Overall Complexity
    private int overallComplexity;

    // -----------------------------
    // Getters & Setters
    // -----------------------------

    public int getTotalLines() { return totalLines; }
    public void setTotalLines(int totalLines) { this.totalLines = totalLines; }

    public int getTotalClasses() { return totalClasses; }
    public void setTotalClasses(int totalClasses) { this.totalClasses = totalClasses; }

    public int getTotalMethods() { return totalMethods; }
    public void setTotalMethods(int totalMethods) { this.totalMethods = totalMethods; }

    public int getTotalVariables() { return totalVariables; }
    public void setTotalVariables(int totalVariables) { this.totalVariables = totalVariables; }

    public int getTotalConditionals() { return totalConditionals; }
    public void setTotalConditionals(int totalConditionals) { this.totalConditionals = totalConditionals; }

    public int getTotalLoops() { return totalLoops; }
    public void setTotalLoops(int totalLoops) { this.totalLoops = totalLoops; }

    public int getExceptionHandlingCount() { return exceptionHandlingCount; }
    public void setExceptionHandlingCount(int exceptionHandlingCount) { this.exceptionHandlingCount = exceptionHandlingCount; }

    public int getCyclomaticComplexity() { return cyclomaticComplexity; }
    public void setCyclomaticComplexity(int cyclomaticComplexity) { this.cyclomaticComplexity = cyclomaticComplexity; }

    public double getCodeQualityScore() { return codeQualityScore; }
    public void setCodeQualityScore(double codeQualityScore) { this.codeQualityScore = codeQualityScore; }

    // ⭐ NEW Getter / Setter
    public int getOverallComplexity() { return overallComplexity; }
    public void setOverallComplexity(int overallComplexity) {
        this.overallComplexity = overallComplexity;
    }
}
