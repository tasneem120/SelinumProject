import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://automationexercise.com/");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test(priority = 1)
    public void verifyHomePage() {
        Assert.assertTrue(driver.getTitle().contains("Automation"));
    }

    @Test(priority = 2)
    public void goToLogin() {
        driver.findElement(By.linkText("Signup / Login")).click();
        Assert.assertTrue(driver.getCurrentUrl().contains("login"));
        driver.navigate().back();
    }

    @Test(priority = 3)
    public void goToProducts() {
        driver.findElement(By.linkText("Products")).click();
        Assert.assertTrue(driver.getCurrentUrl().contains("products"));
        driver.navigate().back();
    }

    @Test(priority = 4)
    public void goToCart() {
        driver.findElement(By.linkText("Cart")).click();
        Assert.assertTrue(driver.getCurrentUrl().contains("view_cart"));
        driver.navigate().back();
    }

    @Test(priority = 5)
    public void goToContact() {
        driver.findElement(By.linkText("Contact us")).click();
        Assert.assertTrue(driver.getCurrentUrl().contains("contact_us"));
        driver.navigate().back();
    }

    // Add to cart + Continue Shopping
    @Test(priority = 6)
    public void addToCartAndContinueShopping() {
        driver.findElement(By.xpath("(//a[contains(text(),'Add to cart')])[1]")).click();

        WebElement continueBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Continue Shopping']"))
        );
        continueBtn.click();

        Assert.assertTrue(driver.getCurrentUrl().equals("https://automationexercise.com/"));
    }

    // Choose category and return home
    @Test(priority = 7)
    public void selectCategory() {
        // Women category example
        driver.findElement(By.xpath("//a[@href='#Women']")).click();
        driver.findElement(By.xpath("//a[contains(text(),'Dress')]")).click();

        driver.navigate().to("https://automationexercise.com/");
        Assert.assertTrue(driver.getTitle().contains("Automation"));
    }

    // Test Cases from slider
    @Test(priority = 8)
    public void openTestCasesFromSlider() {
        WebElement testCasesBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Test Cases')]"))
        );
        testCasesBtn.click();

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
}