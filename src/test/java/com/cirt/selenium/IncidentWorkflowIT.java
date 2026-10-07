package com.cirt.selenium;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Selenium end-to-end tests for critical CIRT user journeys.
 *
 * <p>These tests verify the five most important user workflows
 * through a real browser against the running application.</p>
 *
 * <h3>Journeys tested:</h3>
 * <ol>
 *   <li>Dashboard loads with statistics cards</li>
 *   <li>Create a new incident via the form</li>
 *   <li>View incident list showing created incident</li>
 *   <li>View incident detail and update status</li>
 *   <li>Search incidents by keyword</li>
 * </ol>
 *
 * <h3>Running locally:</h3>
 * <pre>
 *   mvn verify -Pselenium
 * </pre>
 * The Maven 'selenium' profile auto-starts the app with H2 before tests
 * and stops it after.
 *
 * <h3>Screenshots:</h3>
 * On test failure, a PNG screenshot is saved to {@code target/selenium-screenshots/}.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("CIRT Selenium End-to-End Tests")
@Tag("selenium")
public class IncidentWorkflowIT {

    private static WebDriver driver;
    private static final String BASE_URL =
            System.getProperty("app.url", "http://localhost:9090");
    private static final String SCREENSHOT_DIR = "target/selenium-screenshots";

    /** URL of the incident created in Journey 2 — used by later tests. */
    private static String createdIncidentUrl;

    // ---- Automatic failure-screenshot extension ----

    @RegisterExtension
    static TestWatcher screenshotWatcher = new TestWatcher() {
        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            captureScreenshot(context.getDisplayName() + "_FAILED");
        }
    };

    private static void captureScreenshot(String label) {
        if (driver instanceof TakesScreenshot ts) {
            try {
                byte[] png = ts.getScreenshotAs(OutputType.BYTES);
                Path dir = Paths.get(SCREENSHOT_DIR);
                Files.createDirectories(dir);
                String safeName = label.replaceAll("[^a-zA-Z0-9_-]", "_") + ".png";
                Path file = dir.resolve(safeName);
                Files.write(file, png);
                System.out.println("[Selenium] Screenshot saved: " + file.toAbsolutePath());
            } catch (Exception e) {
                System.err.println("[Selenium] Screenshot capture failed: " + e.getMessage());
            }
        }
    }

    // ---- Lifecycle ----

    @BeforeAll
    static void launchBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--headless=new",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--window-size=1920,1080",
                "--remote-allow-origins=*"
        );
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    @AfterAll
    static void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ==================================================================
    // Journey 1 — Dashboard
    // ==================================================================

    @Test
    @Order(1)
    @DisplayName("Journey 1: Dashboard loads with statistics cards")
    void dashboardLoadsWithStatCards() {
        driver.get(BASE_URL + "/");

        assertEquals("CIRT — Dashboard", driver.getTitle(),
                "Page title should be 'CIRT — Dashboard'");

        // Verify all four stat cards are rendered
        assertAll("Dashboard stat cards",
                () -> assertTrue(
                        driver.findElement(By.cssSelector(".stat-card.stat-total")).isDisplayed(),
                        "Total stat card should be visible"),
                () -> assertTrue(
                        driver.findElement(By.cssSelector(".stat-card.stat-open")).isDisplayed(),
                        "Open stat card should be visible"),
                () -> assertTrue(
                        driver.findElement(By.cssSelector(".stat-card.stat-critical")).isDisplayed(),
                        "Critical stat card should be visible"),
                () -> assertTrue(
                        driver.findElement(By.cssSelector(".stat-card.stat-resolved")).isDisplayed(),
                        "Resolved stat card should be visible")
        );

        // Verify navigation bar is present
        assertTrue(driver.findElement(By.cssSelector("nav.navbar")).isDisplayed(),
                "Navigation bar should be visible");
    }

    // ==================================================================
    // Journey 2 — Create Incident
    // ==================================================================

    @Test
    @Order(2)
    @DisplayName("Journey 2: Create a new incident via form")
    void createNewIncident() {
        driver.get(BASE_URL + "/incidents/new");

        assertTrue(driver.getTitle().contains("New Incident"),
                "Page title should contain 'New Incident'");

        // Fill in required fields
        driver.findElement(By.id("title"))
                .sendKeys("Selenium Test — Phishing Email Detected");
        driver.findElement(By.id("description"))
                .sendKeys("Automated Selenium test: suspicious phishing email with "
                        + "malicious attachment reported by the monitoring system.");

        new Select(driver.findElement(By.id("category")))
                .selectByValue("PHISHING");
        new Select(driver.findElement(By.id("severity")))
                .selectByValue("HIGH");

        driver.findElement(By.id("reportedBy"))
                .sendKeys("Selenium Automation");
        driver.findElement(By.id("assignedTo"))
                .sendKeys("Security Team");

        // Submit the form
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Wait for redirect to incident detail page
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/incidents/"));

        // Capture the detail-page URL for later tests
        createdIncidentUrl = driver.getCurrentUrl();

        // Verify the detail page shows the new incident
        String page = driver.getPageSource();
        assertAll("Created incident on detail page",
                () -> assertTrue(page.contains("Phishing Email Detected"),
                        "Title should appear on detail page"),
                () -> assertTrue(page.contains("PHISHING"),
                        "Category should appear on detail page"),
                () -> assertTrue(page.contains("HIGH"),
                        "Severity should appear on detail page"),
                () -> assertTrue(page.contains("OPEN"),
                        "Status should default to OPEN")
        );
    }

    // ==================================================================
    // Journey 3 — Incident List
    // ==================================================================

    @Test
    @Order(3)
    @DisplayName("Journey 3: Incident list shows the created incident")
    void incidentListShowsCreatedIncident() {
        driver.get(BASE_URL + "/incidents");

        assertEquals("CIRT — Incident List", driver.getTitle(),
                "Page title should be 'CIRT — Incident List'");

        // Verify table is visible and contains our incident
        WebElement table = driver.findElement(By.cssSelector("table.table"));
        assertTrue(table.isDisplayed(), "Incidents table should be visible");
        assertTrue(table.getText().contains("Phishing Email Detected"),
                "Table should contain the created incident title");
    }

    // ==================================================================
    // Journey 4 — Detail + Status Update
    // ==================================================================

    @Test
    @Order(4)
    @DisplayName("Journey 4: View incident detail and update status to INVESTIGATING")
    void viewDetailAndUpdateStatus() {
        assertNotNull(createdIncidentUrl,
                "Incident URL should have been captured in Journey 2");
        driver.get(createdIncidentUrl);

        // Verify detail page content
        assertTrue(driver.getPageSource().contains("Phishing Email Detected"),
                "Detail page should show incident title");

        // Change status to INVESTIGATING
        new Select(driver.findElement(By.name("status")))
                .selectByValue("INVESTIGATING");

        // Submit the status-update form
        WebElement statusForm = driver.findElement(
                By.cssSelector("form[action*='/status']"));
        statusForm.findElement(By.cssSelector("button[type='submit']")).click();

        // Wait for page reload
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/incidents/"));

        // Verify status was updated
        assertTrue(driver.getPageSource().contains("INVESTIGATING"),
                "Status should be updated to INVESTIGATING");
    }

    // ==================================================================
    // Journey 5 — Search
    // ==================================================================

    @Test
    @Order(5)
    @DisplayName("Journey 5: Search incidents by keyword")
    void searchIncidentsByKeyword() {
        driver.get(BASE_URL + "/incidents");

        // Type search query
        WebElement searchInput = driver.findElement(By.id("search"));
        searchInput.clear();
        searchInput.sendKeys("Phishing Email");

        // Click Apply / filter-submit button
        driver.findElement(By.cssSelector("#filter-form button[type='submit']"))
                .click();

        // Wait for results to load
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("table.table")));

        // Verify our incident appears in the search results
        assertTrue(driver.getPageSource().contains("Phishing Email Detected"),
                "Search results should contain the incident");
    }
}
