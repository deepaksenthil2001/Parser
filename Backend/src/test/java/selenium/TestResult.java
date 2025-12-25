package selenium;


public class TestResult {
    public String testName;
    public String status;
    public String message;

    public TestResult() {}

    public TestResult(String testName, String status, String message) {
        this.testName = testName;
        this.status = status;
        this.message = message;
    }
}
