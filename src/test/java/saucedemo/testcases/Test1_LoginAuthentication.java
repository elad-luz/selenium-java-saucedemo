package saucedemo.testcases;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
// import org.testng.Assert; // Removed !
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Issues;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import saucedemo.pageobjects.LoginPage;
import saucedemo.pageobjects.ProductsInventoryPage;
import saucedemo.utilities.ConfPropertiesUtil;
import saucedemo.utilities.ExcelTabsUtil;
import saucedemo.utilities.ReportAttachmentsUtil;

@Owner("Class owner: Elad Luz") // relevant to All Tests in Class

public class Test1_LoginAuthentication  extends BaseTest{ // TestPlan Section 01 Test-Cases - Note: one case should Fail intentionally !

// Test-Cases from Section 1 in the Test-Plan (file's located at: /SeleniumProject/project-info/test-planning.txt)
	/* This test checks the UI components exists properly… */
	@Owner("Test owner: Elad Luz") // relevant to a Specific Test in Class -&- overrides the "Class owner: Elad Luz"  (set previously) !
	@Test (priority= 1, description= "01. Login GUI Elements Exist") // description of TestNG (determine the appearance order in report)
	@Description("Smoke-Test = Checking that Login-page is Accessible - and Contains all the expected UI-elements") // @Allure Reports !
	@Severity(SeverityLevel.CRITICAL) // @Allure Reports
	@Epic("Login Page") // @Allure Jira integration
	@Feature("GUI display") // @Allure Jira integration
	@Story("Upon opening Login Page, the Graphic User Interface Elements should be displayed as designed") // @Allure Jira integration !
	public void tc01_LoginElementsExist() throws InterruptedException { // tc00 stands for TestCase with id (00 stands for precondition)
		LoginPage lp = new LoginPage(driver);
		assertTrue(lp.isProperUI(), "UI don't displayed all expected elements properly !"); // isProperUI() - checks all elements exist!
		lp.assertAcceptedUsernamesText();
		lp.assertPasswordForUsersText();
	}
	
	/* This case tests several Failed Login options - using String data inputs of: user, password & error (from excel file dataProvider)
	   Missing Username & Password fields, Missing Username field, Missing Password field, Invalid User,  and an Error Mismatch Failure!
	   Last case is designed to Fail on having wrong message, thus capturing test.png print-screen to: /SeleniumProject/screen-shots/ */
	@Owner("Elad Luz") // opposed to previous "Test owner: Elad Luz"
	@Test (priority= 2, dataProvider= "getExcelData", description= "02. Login Failure cases - Error messages")
	@Description("Failed-Logins = Cases of Authentication with Invalid & Missing Credentials -- Validating the displayed Error message")
	@Severity(SeverityLevel.NORMAL)
	@Epic("Login Page")
	@Feature("Authentication mechanism")
	@Story("When User Login, the Authentication mechanism expect register User with Pass. If not suplied, Error is displayed as design")
	@TmsLinks({@TmsLink("550")}) // Exemplary link to Test
	@Issues({@Issue("50")}) // Exemplary link to Bug
	public void tc02_LoginFailures_Errors(String user, String pass, String expected) throws InterruptedException {
		LoginPage lp = new LoginPage(driver);
		lp.navigateRefresh(); // needed, for the sake of refreshing between each iteration!
		lp.login(user, pass); // fill-in Username & Password (that is taken from excel DS, to match each test-case)  -&-  click LoginBtn
		lp.printErrorButton(); // EXTRA !! -> stand-alone print screenshot of the 'Close Error' (x) button (regardless of Test-Failure)…
		assertTrue(lp.isProperError(expected)); // an assertion on error, using this dedicated method, described in the LoginPage class…
	}	
	@DataProvider // defining the Data source used in the above method -> This is relevant to the saucedemo/utilities/ExcelTabsUtil.java
	public Object[][] getExcelData(){ // method will fetch the excel data into a 2D array table object that would act as a Data Provider
		String excelPath = System.getProperty("user.dir") + "/src/test/resources/saucedemo/inputTestData.xlsx"; // location after Maven!
		// System.out.println(excelPath);
		Object[][] table = ExcelTabsUtil.getTableArray(excelPath, "loginFailure"); // loginFailure = relevant excel tab
		return table;
	}
	
	/* This test uses credentials inputs (user & pass) from a file:  Project.../src/test/resources/saucedemo/configuration.properties */
	@Test (priority= 3, description= "03. Close Login Error button")
	@Description("Functional-Test = Upon Failed Login Message, Click Close Error x button, will Remove the Message")
	@Severity(SeverityLevel.MINOR)
	@Epic("Login Page")
	@Feature("GUI Functionality")
	@Story("When User Login Failed & Error is displayed - tThere should be a close button (x), that closes the displayed message")
	public void tc03_LoginFailureCloseError_MsgRemoved() throws InterruptedException {
		LoginPage lp = new LoginPage(driver);
		lp.navigateRefresh(); // to refresh page & make sure No Error from Prev Case is Displayed!
		assertFalse(lp.isErrorDisplayed()); // Close Btn -> Not Displayed
		lp.clickLogin();
		assertTrue(lp.isErrorDisplayed()); // Close Btn -> Displayed
		lp.printErrorButton(); // A Stand-Alone print screenshot of the 'Close Error' button...
		lp.clickErrorButton(); // Click to close....
		assertFalse(lp.isErrorDisplayed()); // validate the Error was removed!
	}

	/* This test uses credentials inputs (user & pass) from a file:  Project.../src/test/resources/saucedemo/configuration.properties */
	@Test (priority= 4, description= "04. Successful Login of User - Lands in Products Page")
	@Description("Success-Login = Authentication with Valid Credentials, of a Registered User with Proper Password, Logs the User into")
	@Severity(SeverityLevel.BLOCKER)
	@Epic("Login Page")
	@Feature("Authentication mechanism")
	@Story("When User Login with proper credentials - Authentication shouls succeed, and user is redirected to Products-Inventory page")
	public void tc04_LoginSuccess() throws InterruptedException {
		LoginPage lp = new LoginPage(driver);
		lp.navigateRefresh(); // to refresh between each iteration
		
		lp.login(ConfPropertiesUtil.readProperty("user"), ConfPropertiesUtil.readProperty("pass")); // login credentials taken from conf
		lp.assertCurrentUrl("saucedemo.com/inventory.html"); // to used the dynamic method (from BasePage via lp.) for URL validation...
		ProductsInventoryPage pp = new ProductsInventoryPage(driver); // initiate this page, to use its own method for URL validation...
		pp.isProductsInventoryPageTitle(); // redirection to Products-Inventory page, indicates proper login (case specific page method)
		// Example of Using Util for text attachments to Allure Report:
		ReportAttachmentsUtil.attachText("User Log-in properly"); // needs import Util (in Project) for add text attachments to Allure !
	}
}
