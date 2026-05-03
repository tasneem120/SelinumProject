import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;

public class ContactTest {

    WebDriver driver;

    @Test(priority = 1)
    public void openHomePage() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://automationexercise.com/");
    }

    @Test(priority = 2)
    public void openContactPage() {
        driver.findElement(By.xpath("//a[text()=' Contact us']")).click();
        Assert.assertTrue(driver.getTitle().contains("Contact"));
    }

    @Test(priority = 3)
    public void verifyContactFormElements() {
        Assert.assertTrue(driver.findElement(By.name("name")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.name("email")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.name("subject")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.id("message")).isDisplayed());
    }

    @Test(priority = 4)
    public void fillContactForm() {

        driver.findElement(By.name("name")).sendKeys("Basmala");
        driver.findElement(By.name("email")).sendKeys("test@email.com");
        driver.findElement(By.name("subject")).sendKeys("Test Subject");
        driver.findElement(By.id("message")).sendKeys("This is a test message");
    }

    @Test(priority = 5)
    public void uploadFile() {

        driver.findElement(By.name("upload_file"))
                .sendKeys("/Users/bsmalaahmed/Desktop/TestScenarios&TestCases.pdf");
    }

    @Test(priority = 6)
    public void submitForm() {

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        driver.findElement(By.name("submit")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        WebElement successMsg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'Success')]")
                )
        );

        Assert.assertTrue(successMsg.isDisplayed());
    }

    @Test(priority = 7)
    public void closeBrowser() {
        driver.quit();
    }
}
