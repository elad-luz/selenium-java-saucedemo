package saucedemo.testcases;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.chrome.ChromeOptions; // No longer needed - TODO - Delete & Clean all !!!
import static com.github.automatedowl.tools.AllureEnvironmentWriter.allureEnvironmentWriter;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Step;
import saucedemo.utilities.ConfPropertiesUtil;
import saucedemo.utilities.ReportAttachmentsUtil;

public class BaseTest { // Need to RUN as TestNG! -> the class wraps common code for each Test, to be used by test-classes extending it!
	
	WebDriver driver; // declare the driver out-side of classes (global) so it will relate to all Tests (init during first case only) !!
	
	// if you want to use varied browsers in your tests - you can pass them on, as @Parameters -> see "Notes" bellow...
	// @Parameters annotation states which parameter testng.xml passes for RUN:
	@Parameters({"browser"}) // take a dynamic param from testNG.xml (via RUN by MVN) - needs: import org.testng.annotations.Parameters;
	@BeforeClass // will occur at the beginning of class (not before-each test) -> Note we changed driver.get to use DDT |
	@Step("Start Test Run: Choose Browser, Maximize & Browse to the log-in page") // Allure annotation to describe step taken by method!
	public void setUp(@Optional("Chrome") String browser, ITestContext testContext) {
	// in method above - browser = @parameter of RUN if no param passed, use chrome as default !!! 
	// NOTE that, there should be exact correlation of all 3: Params in TestNG file, @Parameters stated & the Arguments (within Method)!
		System.out.println("--- START ---"); // TODO -> REMOVE not needed redundant prints !!!
		
		/* switch case code below, comes to support multi-browsers selection-during-run-options
		   getting a Parameter (passed by TestNG) to determine which Browser you wish to Run on
		   accordingly the relevant browser's driver is declared & initiated !!!
		 */
		switch (browser){ // again, the browser = @parameter of RUN passed into method as a variable
		case "Chrome":
			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver();
			break;
		case "Firefox":
			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver();
			break;
		case "Edge":
			WebDriverManager.edgedriver().setup();
			driver = new EdgeDriver();
			break;
		/** case "explorer":
            WebDriverManager.iedriver().setup();
            InternetExplorerOptions capabilities = new InternetExplorerOptions();
            capabilities.ignoreZoomSettings();
            driver = new InternetExplorerDriver(capabilities);
            break; */
		default: throw new IllegalArgumentException("no such browser " + browser);
		}
		driver.manage().window().maximize(); // set to be working in 'full screen' to open browser at max size
		testContext.setAttribute("WebDriver", this.driver);  //  This Context is needed for taking Print-Screens <-------------- NEW !!!
		String baseUrl = ConfPropertiesUtil.readProperty("url");
		driver.get(baseUrl); // could also be taken from TestNG param - this step could be removed  -and-  set this input at test level!
		// ALLURE Report related - add browserType & baseUrl (can add more- taken dynamically) to Allure report - Overview ENVIRONMENT !
		allureEnvironmentWriter(ImmutableMap.<String, String>builder() //
                        .put("Browser", browser)
                        .put("URL", baseUrl)
                        .build());
	}
	
	@AfterMethod
	@Step("IF Test Runner gets a Failed Test - Failure URL would be attached to Allure & Failure Screenshot will be Collected & Stored")
	/**	I've recently added another different capability of taking screenshots upon Failure (detected by listener) & Attaching to Allure
		This new Util, makes the @AfterMethod redundant (but I kept it as capability show-off)...  TODO  <---------------|  DELETE !  */
	public void failedTest(ITestResult result) { // This method will get the TestNG object holding tests results
	  //check if the test failed, and do (several things) |--> NOTE - also see:  saucedemo.utilities.ListenersUtil -> onTestFailure(...)
		if (result.getStatus() == ITestResult.FAILURE ){
			// use the Util to attach the current URL during failure to the Allure report:
			ReportAttachmentsUtil.attachURL(driver.getCurrentUrl()); // driver.getCurrentUrl() = The Current Failure URL
			// take a Screenshot into memory & save it to a defined location (relative path) -> NOTE  dealt also via: onTestFailure(...)
			TakesScreenshot ts = (TakesScreenshot)driver;
			File srcFile = ts.getScreenshotAs(OutputType.FILE);
			try {
				FileUtils.copyFile(srcFile, new File("./test-screenshots/" + result.getName() + ".png")); // changed after Maven
				// result.getName() method will give you current test case name  & ./test-screenshots/ states the directory path
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@AfterClass // will occur after class (not after-each test)
	@Step("After all Tests in class Ended - Test Runner Closes the browser")
	public void tearDown() { // after finish all tests - close the resource!
		driver.quit();
		// System.out.println("\n--- END ---");
	}
}
