package saucedemo.utilities;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class ListenersUtil extends TestListenerAdapter {
	
/**
	@Override
	public void onTestStart(ITestResult result) {
		Object webDriverAttribute = result.getTestContext().getAttribute("WebDriver");
		if (webDriverAttribute instanceof CanRecordScreen) {
			((CanRecordScreen) webDriverAttribute)
				.StartRecordingScreen(new AndroidStartScreenRecordingOptions().enableBugReporter());
		}
	}
*/

	// using “ITestResult” interface for taking & attaching Screenshots to Report on Test-Failure
	// webDriverAttribute comes from ITestContext that is being initiated within BaseTest as part of Driver...
	@Override
	public void onTestFailure(ITestResult result) {
		// Take from TestContext the Attribute you get from WebDriver and Assign it to this Object for using Screenshots
		Object webDriverAttribute = result.getTestContext().getAttribute("WebDriver");
		if (webDriverAttribute instanceof WebDriver) {
			ReportAttachmentsUtil.attachScreenshot((WebDriver) webDriverAttribute); // Print Screenshot
			ReportAttachmentsUtil.getPageSource((WebDriver) webDriverAttribute); // get the Page Source
			ReportAttachmentsUtil.attachConsoleLogs((WebDriver) webDriverAttribute); // get Console Log
			/*
			if (webDriverAttribute instanceof CanRecordScreen) {
				attachVideo((WebDriver) webDriverAttribute);
			} */
		}
	}
}

/*
TODO - Delete notes:
--------------------

*/
