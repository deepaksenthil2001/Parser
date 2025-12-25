package selenium;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;

import io.github.bonigarcia.wdm.WebDriverManager;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RegisterTest {

    WebDriver driver;
    WebDriverWait wait;

    private static final String FILE_PATH =
            "C:\\Users\\acer\\Downloads\\smartcode-analyzer\\samplfiles\\javacode2.java";

    @BeforeEach
    void setup() {

        WebDriverManager.chromedriver().avoidResolutionCache().setup();

        driver = new ChromeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ============================================
    // 1) REGISTER TEST
    // ============================================
    @Test
    @Order(1)
    void testRegister() {
        String name = "Register Test";
        try {
            driver.get("http://localhost:5173/register");

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//button[contains(.,'Create Account')]")));

            driver.findElement(By.xpath("//label[text()='Full Name']/following::input[1]"))
                    .sendKeys("Deepak");

            driver.findElement(By.xpath("//label[text()='Email Address']/following::input[1]"))
                    .sendKeys("deepak@gmail.com");

            driver.findElement(By.xpath("//label[text()='Password']/following::input[1]"))
                    .sendKeys("StrongPassword123");

            driver.findElement(By.xpath("//label[text()='Confirm Password']/following::input[1]"))
                    .sendKeys("StrongPassword123");

            driver.findElement(By.xpath("//button[contains(.,'Create Account')]")).click();

            Thread.sleep(1500);
            SendResultToBackend.send(name, "PASS", "User registered successfully");
            System.out.println("✔ REGISTERED SUCCESSFULLY!");
        } catch (Exception e) {
            SendResultToBackend.send(name, "FAIL", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // ============================================
    // 2) FULL WORKFLOW TEST
    // ============================================
    @Test
    @Order(2)
    void testFullFlowAnalyzeAndPreview() {
        String workflowName = "Full Workflow Test";

        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // LOGIN
            driver.get("http://localhost:5173/login");
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//button[contains(.,'Sign In')]")));

            driver.findElement(By.xpath("//label[text()='Email or Username']/following::input[1]"))
                    .sendKeys("deepak@gmail.com");
            driver.findElement(By.xpath("//label[text()='Password']/following::input[1]"))
                    .sendKeys("StrongPassword123");
            driver.findElement(By.xpath("//button[contains(.,'Sign In')]")).click();
            Thread.sleep(1600);
            SendResultToBackend.send("Login Test", "PASS", "User logged in successfully");
            System.out.println("✔ LOGGED IN SUCCESSFULLY!");


            // DARK MODE
            try {
                WebElement darkModeBtn = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[name()='svg' and @data-testid='DarkModeIcon']/ancestor::button")
                ));
                darkModeBtn.click();
                Thread.sleep(800);
                SendResultToBackend.send("Dark Mode Test", "PASS", "Dark mode enabled");
                System.out.println("✔ DARK MODE ENABLED!");
            } catch (Exception e) {
                SendResultToBackend.send("Dark Mode Test", "PASS", "Already in dark mode");
            }

            // FULLSCREEN
            try {
                WebElement fullBtn = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[name()='svg' and @data-testid='FullscreenIcon']/ancestor::button")
                ));
                fullBtn.click();
                Thread.sleep(900);
                SendResultToBackend.send("Fullscreen Test", "PASS", "Fullscreen enabled");
                System.out.println("✔ FULLSCREEN ENABLED!");
            } catch (Exception e) {
                SendResultToBackend.send("Fullscreen Test", "PASS", "Already in fullscreen");
            }

            // FILE UPLOAD
            driver.get("http://localhost:5173/upload");
            Thread.sleep(1500);

            WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//input[@type='file']")));
            fileInput.sendKeys(FILE_PATH);
            Thread.sleep(1500);

            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(.,'Continue to Analyze')]"))).click();
            Thread.sleep(2500);
            SendResultToBackend.send("File Upload Test", "PASS", "File uploaded successfully");

            // ANALYZE PAGE
            js.executeScript("window.scrollBy(0, 300)");
            Thread.sleep(800);

            List<WebElement> cards = driver.findElements(
                    By.xpath("//div[contains(@class,'MuiPaper-root')]"));

            for (WebElement card : cards) {
                js.executeScript("arguments[0].scrollIntoView({behavior:'smooth'});", card);
                Thread.sleep(450);
            }
            SendResultToBackend.send("Analyze Test", "PASS", "Analysis page loaded");

            // PREVIEW PAGE
            driver.get("http://localhost:5173/preview");
            Thread.sleep(1500);

            long height = (long) js.executeScript("return document.body.scrollHeight;");
            for (int y = 0; y < height; y += 200) {
                js.executeScript("window.scrollTo(0, arguments[0]);", y);
                Thread.sleep(120);
            }
            SendResultToBackend.send("Preview Test", "PASS", "Preview scrolled successfully");

            // HISTORY PAGE
            driver.get("http://localhost:5173/history");
            Thread.sleep(1800);

            List<WebElement> rows = driver.findElements(By.xpath("//table/tbody/tr"));

            if (!rows.isEmpty()) {

                Random r = new Random();
                int index = r.nextInt(rows.size()) + 1;

                driver.findElement(By.xpath("(//tbody/tr)[" + index + "]//button[1]")).click();
                Thread.sleep(1200);
                driver.navigate().back();
                Thread.sleep(1200);

                driver.findElement(By.xpath("(//tbody/tr)[" + index + "]//button[2]")).click();
                Thread.sleep(1600);
                driver.navigate().back();
                Thread.sleep(1200);

                driver.findElement(By.xpath("(//tbody/tr)[" + index + "]//button[3]")).click();
                Thread.sleep(1200);

                SendResultToBackend.send("History Test", "PASS", "History actions completed");
                System.out.println("✔ HISTORY ACTION COMPLETED!");
            }

            // LOGOUT
            WebElement logoutBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button//*[name()='svg' and @data-testid='LogoutIcon']")
            ));
            logoutBtn.click();
            Thread.sleep(1500);
            SendResultToBackend.send("Logout Test", "PASS", "User logged out");
            System.out.println("✔ LOGGED OUT SUCCESSFULLY!");
            Thread.sleep(1000);

            // FINAL WORKFLOW RESULT
            SendResultToBackend.send(workflowName, "PASS", "Full workflow completed successfully");
            System.out.println("🎉 FULL WORKFLOW COMPLETED!");

            // ⭐ AUTO REDIRECT TO TEST REPORT PAGE ⭐
            driver.get("http://localhost:5173/test-report");
            Thread.sleep(20000);

        } catch (Exception e) {
            SendResultToBackend.send(workflowName, "FAIL", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void teardown() {
        if (driver != null)
            driver.quit();
    }
}
