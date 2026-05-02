import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class ProductsTest extends BaseTest {

    @Test
    public void fullFlowProductTest() {

        driver.get("https://automationexercise.com/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // ====== 1. Open Products Page ======
        WebElement productsBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='/products']"))
        );
        productsBtn.click();

        WebElement title = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[text()='All Products']"))
        );
        Assert.assertTrue(title.isDisplayed());

        // ====== 2. View First Product ======
        WebElement viewProduct = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("(//a[text()='View Product'])[1]"))
        );
        viewProduct.click();

        // ====== 3. Verify Product Details ======
        WebElement productInfo = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='product-information']"))
        );
        Assert.assertTrue(productInfo.isDisplayed());

        // ====== 4. Add to Cart ======
        WebElement addToCart = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='button']"))
        );
        addToCart.click();

        WebElement continueBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Continue Shopping']"))
        );
        continueBtn.click();

        // ====== 5. Scroll to Review Section ======
        WebElement reviewTitle = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//a[text()='Write Your Review']"))
        );
        js.executeScript("arguments[0].scrollIntoView(true);", reviewTitle);

        // ====== 6. Fill Review ======

        // Name
        WebElement nameField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("name"))
        );
        js.executeScript("arguments[0].scrollIntoView(true);", nameField);
        nameField.click();
        nameField.clear();
        nameField.sendKeys("Khadija");

        // Email
        WebElement emailField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("email"))
        );
        js.executeScript("arguments[0].scrollIntoView(true);", emailField);
        emailField.click();
        emailField.clear();
        emailField.sendKeys("khadija@test.com");

        // Review
        WebElement reviewField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("review"))
        );
        js.executeScript("arguments[0].scrollIntoView(true);", reviewField);
        reviewField.click();
        reviewField.clear();
        reviewField.sendKeys("Very good product!");

        // ====== 7. Submit Review ======
        WebElement submitBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("button-review"))
        );
        js.executeScript("arguments[0].scrollIntoView(true);", submitBtn);
        submitBtn.click();

        // ====== 8. Verify Success Message ======
        WebElement successMsg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//span[contains(text(),'Thank you for your review')]")
                )
        );

        Assert.assertTrue(successMsg.isDisplayed());
    }
}
