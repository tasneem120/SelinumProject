import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Random;

public class Login_SignUP {
    WebDriver driver ;
    String randomName = "Tasneem" + (int)(Math.random()*10000);
    String randomEmail = "user" + System.currentTimeMillis() + "@mail.com";
    String randomPassword = "Tasneem" + (int)(Math.random()*10000);
    String firstName = "Tasneem" + (int)(Math.random()*10000);
    String lastName = "Khaled" + (int)(Math.random()*10000);
    String address = "Menofia" + (int)(Math.random()*10000);

    Random random = new Random();

    int zip = 10000 + random.nextInt(90000);
    String mobile = "01" + (10 + random.nextInt(90))
            + (10000000 + random.nextInt(90000000));
    @BeforeTest
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://automationexercise.com/login");
    }
    @Test(priority = 1)

    public void signUp() throws InterruptedException {

        driver.findElement(By.xpath("//input[@data-qa='signup-name']")).sendKeys(randomName);
        driver.findElement(By.xpath("//input[@data-qa='signup-email']")).sendKeys(randomEmail);
        Thread.sleep(2000);
        driver.findElement(By.xpath("//button[@data-qa='signup-button']")).click();
        Thread.sleep(2000);
        driver.findElement(By.xpath("//input[@name='title' and @value='Mrs']")).click();
        driver.findElement(By.xpath("//input[@data-qa='password']")).sendKeys(randomPassword);
        driver.findElement(By.id("newsletter")).click();
        Thread.sleep(500);
        driver.findElement(By.xpath("//input[@id='optin']")).click();
        driver.findElement(By.xpath("//input[@data-qa='first_name']")).sendKeys(firstName);
        driver.findElement(By.xpath("//input[@data-qa='last_name']")).sendKeys(lastName);
        driver.findElement(By.xpath("//input[@data-qa='address']")).sendKeys(address);
        Thread.sleep(500);
        WebElement countryElement = driver.findElement(By.id("country"));
        Select select = new Select(countryElement);
        int index = random.nextInt(select.getOptions().size());
        select.selectByIndex(index);
        driver.findElement(By.xpath("//input[@data-qa='state']")).sendKeys("California");
        driver.findElement(By.xpath("//input[@data-qa='city']")).sendKeys("Los Angeles");
        Thread.sleep(500);
        driver.findElement(By.xpath("//input[@data-qa='zipcode']")).sendKeys(zip+"");
        driver.findElement(By.xpath("//input[@data-qa='mobile_number']")).sendKeys(mobile);
        Thread.sleep(2000);
        driver.findElement(By.xpath("//button[@data-qa='create-account']")).click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//a[contains(text(),'Continue')]")).click();
        Thread.sleep(2000);

    }
    @Test(priority = 2 )
    public void logout() {
        driver.findElement(By.xpath("//a[contains(.,'Logout')]")).click();
    }
    @Test(priority = 3 )

    public void login() throws InterruptedException {
        Thread.sleep(1000);
        driver.findElement(By.xpath("//input[@data-qa='login-email']")).sendKeys(randomEmail);
        driver.findElement(By.xpath("//input[@data-qa='login-password']")).sendKeys(randomPassword);
        Thread.sleep(500);
        driver.findElement(By.xpath("//button[@data-qa='login-button']")).click();

    }
    @Test(priority = 4 )

    public void deleteAccount() throws InterruptedException {
        Thread.sleep(1000);

        driver.findElement(By.xpath("//a[contains(text(),'Delete Account')]")).click();
    }
    @AfterTest
    public void tearDown() {
        driver.quit();
    }

    public static void main(String[] args) {

    }
}
