import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class Cart {

    // ── Driver & Wait ──────────────────────────────────────────
    WebDriver driver;
    WebDriverWait wait;

    // ── Site constants ─────────────────────────────────────────
    static final String BASE_URL     = "https://automationexercise.com";
    static final String CART_URL     = BASE_URL + "/view_cart";
    static final String PRODUCTS_URL = BASE_URL + "/products";
    static final String LOGIN_URL    = BASE_URL + "/login";
    static final String[] PRODUCT_URLS = {
            BASE_URL + "/product_details/11",
            BASE_URL + "/product_details/12",
            BASE_URL + "/product_details/3",   // Sleeveless Dress  – Rs. 1000
            BASE_URL + "/product_details/4",   // Stylish Dress     – Rs. 2150
            BASE_URL + "/product_details/5",   // Winter Top        – Rs. 600
            BASE_URL + "/product_details/6",   // Summer White Top  – Rs. 400
            BASE_URL + "/product_details/7",   // Madame Top        – Rs. 1000
            BASE_URL + "/product_details/8",   // Master            – Rs. 2200
            BASE_URL + "/product_details/13",   // Cotton Mull       – Rs. 3500
            BASE_URL + "/product_details/14"   // Fancy Green Top   – Rs. 700
    };

    static final String VALID_EMAIL    = "eaya5317@gmail.com";
    static final String VALID_PASSWORD = "PK7iyS2cTKd3fX@";

    static final String PRODUCT_NAME_1 = "Blue Top";
    static final String PRODUCT_URL_1  = BASE_URL + "/product_details/1";
    static final String PRODUCT_NAME_2 = "Men Tshirt";
    static final String PRODUCT_URL_2  = BASE_URL + "/product_details/2";

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) driver.quit();
    }


    // ══════════════════════════════════════════════════════════════
    //  HELPER METHODS
    // ══════════════════════════════════════════════════════════════

    private void goToCart() {
        driver.get(CART_URL);
    }

    private void clearCart() {
        driver.get(CART_URL);

        List<WebElement> deleteButtons = driver.findElements(
                By.cssSelector(".cart_quantity_delete")
        );

        // Keep clicking delete until no rows remain
        while (!deleteButtons.isEmpty()) {
            deleteButtons.get(0).click();

            // Wait for that row to disappear before looking for the next one
            // This is important — if you don't wait, Selenium grabs a stale reference
            // Stale reference = element was found but then the page changed, so it no longer exists
            wait.until(ExpectedConditions.stalenessOf(deleteButtons.get(0)));

            // Re-fetch the list because the page DOM changed after deletion
            deleteButtons = driver.findElements(By.cssSelector(".cart_quantity_delete"));
        }
    }
    private void loginWithValidCredentials() {
        driver.get(LOGIN_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
        driver.findElement(By.name("email")).sendKeys(VALID_EMAIL);
        driver.findElement(By.name("password")).sendKeys(VALID_PASSWORD);
        driver.findElement(By.cssSelector("button[data-qa='login-button']")).click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/"));
    }

    private void addProductToCart(String productDetailUrl, int qty) {
        driver.get(productDetailUrl);
        WebElement qtyInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("quantity"))
        );
        qtyInput.clear();
        qtyInput.sendKeys(String.valueOf(qty));
        driver.findElement(By.cssSelector("button.cart")).click();

        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.modal-content"))
        );
        WebElement continueBtn = modal.findElement(
                By.cssSelector("button.close-modal, button[data-dismiss='modal']")
        );
        continueBtn.click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector("div.modal-content")
        ));
    }
    // ADD THIS — used in TC-048 to check if the page overflows horizontally
    private boolean hasHorizontalOverflow() {
        Long scrollWidth = (Long) ((JavascriptExecutor) driver)
                .executeScript("return document.body.scrollWidth;");
        Long clientWidth = (Long) ((JavascriptExecutor) driver)
                .executeScript("return document.body.clientWidth;");
        return scrollWidth > clientWidth + 5;
    }

    // ADD THIS — used in TC-046, TC-048 to reach the footer
    private void scrollToBottom() {
        ((JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight);");
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    private String getCartQuantityText(int rowIndex) {
        List<WebElement> rows = driver.findElements(By.cssSelector("#cart_info_table tbody tr"));
        if (rows.size() <= rowIndex) return "";
        return rows.get(rowIndex).findElement(By.cssSelector(".cart_quantity button")).getText().trim();
    }

    private String getCartTotalText(int rowIndex) {
        List<WebElement> rows = driver.findElements(By.cssSelector("#cart_info_table tbody tr"));
        if (rows.size() <= rowIndex) return "";
        return rows.get(rowIndex).findElement(By.cssSelector(".cart_total_price")).getText().trim();
    }

    private boolean isCartEmptyMessageVisible() {
        List<WebElement> emptyMsg = driver.findElements(By.cssSelector("#empty_cart"));
        return !emptyMsg.isEmpty() && emptyMsg.get(0).isDisplayed();
    }

    private int getCartRowCount() {
        return driver.findElements(By.cssSelector("#cart_info_table tbody tr")).size();
    }

    private void deleteCartRow(int rowIndex) {
        List<WebElement> rows = driver.findElements(By.cssSelector("#cart_info_table tbody tr"));
        rows.get(rowIndex).findElement(By.cssSelector(".cart_quantity_delete")).click();
    }


    // ══════════════════════════════════════════════════════════════
    //  TC-001  |  POSITIVE
    //  Empty cart message shown to a GUEST (not logged in)
    // ══════════════════════════════════════════════════════════════
    @Test(priority = 1, description = "TC-001: Empty cart message visible to guest user")
    public void TC001_emptyCartGuestUser() {
        goToCart();

        Assert.assertTrue(isCartEmptyMessageVisible(),
                "Expected the empty cart message to be visible for a guest with no items.");
    }


    // ══════════════════════════════════════════════════════════════
    //  TC-002  |  POSITIVE
    //  Empty cart message shown to a LOGGED-IN user with no items
    // ══════════════════════════════════════════════════════════════
    @Test(priority = 2, description = "TC-002: Empty cart message visible to logged-in user with no items")
    public void TC002_emptyCartLoggedInUser() {
        clearCart();
        loginWithValidCredentials();
        goToCart();

        Assert.assertTrue(isCartEmptyMessageVisible(),
                "Expected the empty cart message for a logged-in user who has no items.");
    }


    // ══════════════════════════════════════════════════════════════
    //  TC-004  |  POSITIVE
    //  Single product displays correct details in cart
    // ══════════════════════════════════════════════════════════════
    @Test(priority = 3, description = "TC-004: Single product shows correct name, price, qty, total in cart")
    public void TC004_singleProductDisplay() {
        addProductToCart(PRODUCT_URL_1, 1);
        driver.get(CART_URL);

        WebElement productNameCell = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart_description h4 a"))
        );
        Assert.assertTrue(productNameCell.getText().contains(PRODUCT_NAME_1),
                "Product name in cart does not match expected: " + PRODUCT_NAME_1);

        Assert.assertEquals(getCartQuantityText(0), "1",
                "Default quantity should be 1 for a freshly added item.");
    }


    // ══════════════════════════════════════════════════════════════
    //  TC-005  |  POSITIVE
    //  Multiple products all appear in cart
    // ══════════════════════════════════════════════════════════════
    @Test(priority = 4, description = "TC-005: Multiple products all display in cart correctly")
    public void TC005_multipleProductsDisplay() {
        addProductToCart(PRODUCT_URL_1, 1);
        addProductToCart(PRODUCT_URL_2, 1);
        driver.get(CART_URL);

        int rowCount = getCartRowCount();
        Assert.assertEquals(rowCount, 2,
                "Expected 2 product rows in cart, found: " + rowCount);
    }


    // ══════════════════════════════════════════════════════════════
    //  TC-006  |  POSITIVE
    //  Product thumbnail image renders (not broken) in cart
    // ══════════════════════════════════════════════════════════════
    @Test(priority = 5, description = "TC-006: Product thumbnail image is displayed and not broken in cart")
    public void TC006_productThumbnailDisplay() {
        addProductToCart(PRODUCT_URL_1, 1);
        driver.get(CART_URL);

        WebElement img = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart_product img"))
        );

        Long naturalWidth = (Long) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].naturalWidth;", img);

        Assert.assertTrue(naturalWidth > 0,
                "Product thumbnail image appears broken (naturalWidth = 0).");
    }


    // ══════════════════════════════════════════════════════════════
    //  TC-008  |  POSITIVE
    //  Default quantity = 1 for a freshly added item
    // ══════════════════════════════════════════════════════════════
    @Test(priority = 6, description = "TC-008: Newly added item has default quantity of 1 in cart")
    public void TC008_defaultQuantityIsOne() {
        addProductToCart(PRODUCT_URL_1, 1);
        driver.get(CART_URL);

        Assert.assertEquals(getCartQuantityText(0), "1",
                "Expected default quantity to be 1 after a fresh add.");
    }


    // ══════════════════════════════════════════════════════════════
    //  TC-009  |  POSITIVE
    //  Increase quantity: add 3 more → cart should show 4 total
    // ══════════════════════════════════════════════════════════════
    @Test(priority = 7, description = "TC-009: Increasing quantity via product detail page updates cart total")
    public void TC009_increaseQuantity() {
        addProductToCart(PRODUCT_URL_1, 1);
        addProductToCart(PRODUCT_URL_1, 3);
        driver.get(CART_URL);

        Assert.assertEquals(getCartQuantityText(0), "4",
                "Expected cart quantity to be 4 after adding 1 + 3.");
    }


    // ══════════════════════════════════════════════════════════════
    //  TC-016  |  POSITIVE
    //  Total = Price × Quantity for a single item (Rs.500 × 1 = Rs.500)
    // ══════════════════════════════════════════════════════════════
    @Test(priority = 8, description = "TC-016: Total correctly equals Price × Qty (Rs.500 × 1 = Rs.500)")
    public void TC016_totalEqualsPrice() {
        addProductToCart(PRODUCT_URL_1, 1);
        driver.get(CART_URL);

        String total = getCartTotalText(0);
        Assert.assertTrue(total.contains("500"),
                "Expected total to be Rs. 500 for Blue Top qty=1, but got: " + total);
    }


    // ══════════════════════════════════════════════════════════════
    //  TC-017  |  POSITIVE
    //  Total updates correctly when qty is 4 (Rs.500 × 4 = Rs.2000)
    // ══════════════════════════════════════════════════════════════
    @Test(priority = 9, description = "TC-017: Total correctly equals Price × Qty (Rs.500 × 4 = Rs.2000)")
    public void TC017_totalEqualsPriceTimesQty() {
        addProductToCart(PRODUCT_URL_1, 1);
        addProductToCart(PRODUCT_URL_1, 3);
        driver.get(CART_URL);

        String total = getCartTotalText(0);
        Assert.assertTrue(total.contains("2000"),
                "Expected total Rs.2000 for Blue Top qty=4, but got: " + total);
    }


    // ══════════════════════════════════════════════════════════════
    //  TC-019  |  POSITIVE
    //  Deleting one item removes ONLY that item; other stays
    // ══════════════════════════════════════════════════════════════
    @Test(priority = 10, description = "TC-019: Removing one item from a 2-item cart leaves the other intact")
    public void TC019_removeOneItemFromTwoItemCart() {
        addProductToCart(PRODUCT_URL_1, 1);
        addProductToCart(PRODUCT_URL_2, 1);
        driver.get(CART_URL);

        Assert.assertEquals(getCartRowCount(), 2, "Expected 2 items before deletion.");

        deleteCartRow(0);

        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("#cart_info_table tbody tr"), 1
        ));

        Assert.assertEquals(getCartRowCount(), 1,
                "Expected 1 item remaining after deleting one of two.");
    }


    // ══════════════════════════════════════════════════════════════
    //  TC-020  |  POSITIVE
    //  Removing the LAST item shows empty cart message
    // ══════════════════════════════════════════════════════════════
    @Test(priority = 11, description = "TC-020: Removing the last item shows empty cart message")
    public void TC020_removeLastItemShowsEmptyCart() {
        addProductToCart(PRODUCT_URL_1, 1);
        driver.get(CART_URL);

        deleteCartRow(0);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#empty_cart")));

        Assert.assertTrue(isCartEmptyMessageVisible(),
                "Expected empty cart message after removing the last item.");
    }


    // ══════════════════════════════════════════════════════════════
    //  TC-024  |  POSITIVE
    //  "Register/Login" link inside modal goes to /login page
    // ══════════════════════════════════════════════════════════════
    @Test(priority = 12, description = "TC-024: Register/Login link inside checkout modal navigates to /login")
    public void TC024_modalRegisterLoginLink() {
        addProductToCart(PRODUCT_URL_1, 1);
        driver.get(CART_URL);

        driver.findElement(By.cssSelector(".btn.btn-default.check_out")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#checkoutModal")));

        driver.findElement(By.cssSelector("#checkoutModal a[href='/login']")).click();

        wait.until(ExpectedConditions.urlContains("/login"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"),
                "Expected to be redirected to /login after clicking the link in the modal.");
    }


    // ══════════════════════════════════════════════════════════════
    //  TC-025  |  POSITIVE
    //  "Continue On Cart" button closes modal; cart unchanged
    // ══════════════════════════════════════════════════════════════
    @Test(priority = 13, description = "TC-025: 'Continue On Cart' closes modal without losing cart items")
    public void TC025_continueOnCartClosesModal() {
        addProductToCart(PRODUCT_URL_1, 1);
        driver.get(CART_URL);

        driver.findElement(By.cssSelector(".btn.btn-default.check_out")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#checkoutModal")));

        driver.findElement(By.cssSelector("#checkoutModal .btn-success")).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("#checkoutModal")));

        Assert.assertEquals(getCartRowCount(), 1,
                "Expected cart to still have 1 item after closing modal with 'Continue On Cart'.");
    }


    // ══════════════════════════════════════════════════════════════
    //  TC-027  |  POSITIVE
    //  Logged-in user goes directly to checkout (no modal)
    // ══════════════════════════════════════════════════════════════
    @Test(priority = 14, description = "TC-027: Logged-in user proceeds to checkout without seeing a modal")
    public void TC027_checkoutWithLogin() {
        loginWithValidCredentials();
        addProductToCart(PRODUCT_URL_1, 1);
        driver.get(CART_URL);

        driver.findElement(By.cssSelector(".btn.btn-default.check_out")).click();

        wait.until(ExpectedConditions.urlContains("/checkout"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/checkout"),
                "Expected direct navigation to /checkout for logged-in user.");

        List<WebElement> modal = driver.findElements(By.cssSelector("#checkoutModal.show"));
        Assert.assertTrue(modal.isEmpty(),
                "Checkout modal should not appear for a logged-in user.");
    }


    // ══════════════════════════════════════════════════════════════
    //  TC-030  |  POSITIVE
    //  After logout and re-login, cart items are still there
    // ══════════════════════════════════════════════════════════════
    @Test(priority = 15, description = "TC-030: Cart items persist in account after logout and re-login")
    public void TC030_cartPersistsAfterLogoutAndReLogin() {
        loginWithValidCredentials();
        addProductToCart(PRODUCT_URL_1, 1);

        driver.findElement(By.cssSelector("a[href='/logout']")).click();
        wait.until(ExpectedConditions.urlContains("/login"));

        loginWithValidCredentials();
        driver.get(CART_URL);

        Assert.assertEquals(getCartRowCount(), 1,
                "Expected cart to still have 1 item after logout and re-login.");
    }


    // ══════════════════════════════════════════════════════════════
    //  TC-034  |  POSITIVE
    //  'Home' breadcrumb button navigates to homepage
    // ══════════════════════════════════════════════════════════════
    @Test(priority = 16, description = "TC-034: 'Home' breadcrumb on cart page navigates to homepage")
    public void TC034_breadcrumbHomeLink() {
        addProductToCart(PRODUCT_URL_1, 1);
        driver.get(CART_URL);

        driver.findElement(By.cssSelector(".breadcrumb li a[href='/']")).click();

        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/"));
        Assert.assertEquals(driver.getCurrentUrl(), BASE_URL + "/",
                "Expected to be redirected to homepage after clicking 'Home' breadcrumb.");
    }


    // ══════════════════════════════════════════════════════════════
    //  TC-035  |  POSITIVE
    //  'here' link in empty cart message goes to /products
    // ══════════════════════════════════════════════════════════════
    @Test(priority = 17, description = "TC-035: 'here' link in empty cart message navigates to /products")
    public void TC035_emptyCartHereLinkGoesToProducts() {
        // Guarantee cart is empty before testing the empty state message
        clearCart();

        WebElement hereLink = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#empty_cart a[href='/products']")
                )
        );
        hereLink.click();

        wait.until(ExpectedConditions.urlContains("/products"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/products"),
                "Expected to be on /products page after clicking 'here' link.");
    }

    // ══════════════════════════════════════════════════════════════
    //  TC-037  |  POSITIVE
    //  Valid email subscription shows success confirmation
    // ══════════════════════════════════════════════════════════════
    @Test(priority = 18, description = "TC-037: Valid email in footer subscription shows success message")
    public void TC037_subscriptionValidEmail() {
        goToCart();

        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");

        WebElement emailInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("susbscribe_email"))
        );
        emailInput.sendKeys("autotest_" + System.currentTimeMillis() + "@mail.com");
        driver.findElement(By.id("subscribe")).click();

        WebElement alert = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#success-subscribe"))
        );
        Assert.assertTrue(alert.isDisplayed(),
                "Expected success message after subscribing with a valid email.");
    }


    // ══════════════════════════════════════════════════════════════
    //  TC-044  |  POSITIVE
    //  Footer copyright text is correct
    // ══════════════════════════════════════════════════════════════
    @Test(priority = 20, description = "TC-044: Footer shows correct copyright text")
    public void TC044_footerCopyrightText() {
        goToCart();
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");

        WebElement footer = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#footer .footer-bottom p"))
        );

        Assert.assertTrue(footer.getText().contains("Copyright"),
                "Expected footer to contain copyright text, but got: " + footer.getText());
        Assert.assertTrue(footer.getText().contains("2021"),
                "Expected copyright year 2021 in footer, but got: " + footer.getText());
    }


    @Test(priority = 39,
            description = "TC-047: 5 distinct products each appear as a separate row with valid data")
    public void TC047_fiveProductsAllAppearInCart() {
        for (int i = 0; i < 5; i++) {
            addProductToCart(PRODUCT_URLS[i], 1);
        }
        driver.get(CART_URL);

        int rowCount = getCartRowCount();
        Assert.assertEquals(rowCount, 5,
                "Expected 5 rows for 5 distinct products. Found: " + rowCount);

        List<WebElement> names = driver.findElements(
                By.cssSelector(".cart_description h4 a")
        );
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i).getText().trim();
            Assert.assertFalse(name.isEmpty(),
                    "Row " + (i + 1) + " has an empty product name.");
        }

        for (int i = 0; i < 5; i++) {
            String total = getCartTotalText(i);
            Assert.assertFalse(total.isEmpty(),
                    "Row " + (i + 1) + " total cell is empty.");
            Assert.assertFalse(total.toUpperCase().contains("NAN"),
                    "Row " + (i + 1) + " shows NaN in total: " + total);
        }
    }


    @Test(priority = 40,
            description = "TC-048: Cart page has no overflow and footer/checkout remain accessible with 10 items")
    public void TC048_cartPageLayoutWithTenItems() {
        for (String url : PRODUCT_URLS) {
            addProductToCart(url, 1);
        }
        driver.get(CART_URL);

        Assert.assertFalse(hasHorizontalOverflow(),
                "Page has horizontal overflow with 10 items — content is cut off.");

        int rowCount = getCartRowCount();
        Assert.assertEquals(rowCount, 10,
                "Expected 10 rows for 10 products. Found: " + rowCount);

        WebElement checkoutBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".btn.btn-default.check_out")
                )
        );
        Assert.assertTrue(checkoutBtn.isDisplayed(),
                "'Proceed to Checkout' not visible with 10 items.");
        Assert.assertTrue(checkoutBtn.isEnabled(),
                "'Proceed to Checkout' is disabled with 10 items.");

        scrollToBottom();
        WebElement footer = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("footer"))
        );
        Assert.assertTrue(footer.isDisplayed(),
                "Footer not visible after scrolling with 10 items in cart.");
    }
}
