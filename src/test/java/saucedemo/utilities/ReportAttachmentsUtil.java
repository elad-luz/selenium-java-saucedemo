package saucedemo.utilities; // Allure Report - Attachments Utils

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import io.qameta.allure.Attachment;

public class ReportAttachmentsUtil { //  for Allure Attachments

	// Add Text attachment
	@Attachment(value = "{0}", type = "text/plain", fileExtension = ".txt")
	public static String attachText(String message) {
		return message;
	}

	// Add a URL link attachment - can also be fetched dynamically driver.getCurrentUrl()
	// implement locally e.g: ReportAttachmentsUtil.attachURL("https://www.google.com/");
    @Attachment(value = "URL attachment", type = "text/uri-list", fileExtension = ".uri")
    public static String attachURL(String url) {
        return url;
    }

	// Add Page-Source attachment (needs import)
	@Attachment(value = "Html source", type = "text/html", fileExtension = ".html")
	public static byte[] getPageSource(WebDriver driver) {
		return driver.getPageSource().getBytes(StandardCharsets.UTF_8);
	}

	// Add Console Log attachment (needs import)
	@Attachment(value = "Console Logs", type = "text/plain", fileExtension = ".txt")
    public static String attachConsoleLogs(WebDriver driver) {
        String consoleLogs = driver
                .manage()
                .logs()
                .get(LogType.BROWSER)
                .getAll()
                .stream()
                .map(LogEntry::toString)
                .collect(Collectors.joining(System.lineSeparator()));
        return StringUtils.isBlank(consoleLogs) ? "No Console Logs Found" : consoleLogs;
    }

	// Capture -and- Add Screenshot attachment Upon Text-Failure that was detected by Listener (needs import)
	// This also refers to: BaseTest & ListenersUtil...
	@Attachment(value = "Page Screenshot", type = "image/png", fileExtension = ".png")
	public static byte[] attachScreenshot(WebDriver driver) {
		return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
	}
	
	// Capture -and- Add a Stand-alone Element-Screenshot attachment, regardless of Failure... (needs import)
	/** Example of how to Use anywhere in code on any element:
		ReportAttachmentsUtil.attachElementScreenshot(driver.findElement(By.cssSelector("#test")));
		See implementation in page: printErrorButton() method -> in: LoginPage class
		and, in test: tc01_LoginFailures(...)  method -> in: S01_LoginTests class !   */
	@Attachment(value = "Element Screenshot", type = "image/png", fileExtension = ".png")
	public static byte[] attachElementScreenshot(WebElement element) {
		return element.getScreenshotAs(OutputType.BYTES);
	}
}

/*
TODO - Delete notes:
--------------------

*/
