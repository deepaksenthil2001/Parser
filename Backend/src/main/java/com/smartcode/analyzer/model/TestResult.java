package com.smartcode.analyzer.model;




public class TestResult {

    private String testName;
    private String status;
    private String message;

    public TestResult() {}

    public TestResult(String testName, String status, String message) {
        this.testName = testName;
        this.status = status;
        this.message = message;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

