import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;

public class home {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeTest
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        driver.manage().window().maximize();
        driver.get("https://automationexercise.com/");

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
    }

    @Test(priority = 1)
    public void verifyHomePage() {
        Assert.assertTrue(driver.getTitle().contains("Automation"));
    }

    @Test(priority = 2)
    public void goToLogin() {
        clickElement(By.xpath("//a[contains(text(),'Signup / Login')]"));

        wait.until(ExpectedConditions.urlContains("login"));
        Assert.assertTrue(driver.getCurrentUrl().contains("login"));

        driver.navigate().back();
    }

    @Test(priority = 3)
    public void goToProducts() {
        clickElement(By.xpath("//a[contains(text(),'Products')]"));

        wait.until(ExpectedConditions.urlContains("products"));
        Assert.assertTrue(driver.getCurrentUrl().contains("products"));

        driver.navigate().back();
    }

    @Test(priority = 4)
    public void goToCart() {
        clickElement(By.xpath("//a[contains(text(),'Cart')]"));

        wait.until(ExpectedConditions.urlContains("view_cart"));
        Assert.assertTrue(driver.getCurrentUrl().contains("view_cart"));

        driver.navigate().back();
    }

    @Test(priority = 5)
    public void goToContact() {
        clickElement(By.xpath("//a[contains(text(),'Contact')]"));

        wait.until(ExpectedConditions.urlContains("contact_us"));
        Assert.assertTrue(driver.getCurrentUrl().contains("contact_us"));

        driver.navigate().back();
    }

    @Test(priority = 6)
    public void addToCartAndContinueShopping() {
        driver.get("https://automationexercise.com/");

        WebElement addBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("(//a[contains(text(),'Add to cart')])[1]")
                )
        );
        addBtn.click();

        WebElement continueBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[text()='Continue Shopping']")
                )
        );
        continueBtn.click();

        Assert.assertEquals(driver.getCurrentUrl(), "https://automationexercise.com/");
    }

    @Test(priority = 7)
    public void selectCategory() {
        driver.get("https://automationexercise.com/");

        WebElement womenCategory = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[@href='#Women']")
                )
        );
        womenCategory.click();

        WebElement dress = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[contains(text(),'Dress')]")
                )
        );
        dress.click();

        Assert.assertTrue(driver.getCurrentUrl().contains("category"));
    }

    @Test(priority = 8)
    public void openTestCases() {
        clickElement(By.xpath("//a[contains(text(),'Test Cases')]"));

        wait.until(ExpectedConditions.urlContains("test_cases"));
        Assert.assertTrue(driver.getCurrentUrl().contains("test_cases"));

        driver.navigate().back();
    }

    @Test(priority = 9)
    public void invalidSubscription() {
        driver.findElement(By.id("susbscribe_email")).sendKeys("test@@gmail");
        driver.findElement(By.id("subscribe")).click();
    }

    @AfterTest
    public void tearDown() {
        driver.quit();
    }

    // =========================
    // REUSABLE CLICK METHOD
    // =========================
    public void clickElement(By locator) {
        WebElement element = wait.until(
                ExpectedConditions.elementToBeClickable(locator)
        );

        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].click();", element);
        }
    }
}
