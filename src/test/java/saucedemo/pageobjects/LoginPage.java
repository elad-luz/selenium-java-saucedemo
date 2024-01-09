package saucedemo.pageobjects;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import io.qameta.allure.Step;
import saucedemo.utilities.ConfPropertiesUtil;
import saucedemo.utilities.ReportAttachmentsUtil;

public class LoginPage extends BasePage { // Entrance page (login) is not extending 'Common', which only have relevance after lodged-in!
// this Page is relevant prior to Successful Login - it is the page from which Login is made -&- either succeeds or fails with an error…
	
	//	pageFactory ► login
	@FindBy(css=".login_logo") // upper logo (note: it is not the same locator, as in all other pages - that is being located in Common)
	private WebElement liLogoSwagLabs;
	@FindBy(css="#user-name")
	private WebElement usernameField;
	@FindBy(css="#password")
	private WebElement passwordField;
	@FindBy(css=".submit-button.btn_action")
	private WebElement loginBtn;
	@FindBy(css=".login_wrapper")
	private WebElement loginWrapper;
	@FindBy(css=".error-message-container")
	private WebElement errorContainer;
	@FindBy(css="[data-test='error']")
	private WebElement errorMessage;
	@FindBy(css=".error-button")
	private WebElement errorCloseButton; // the 'x' Button (note: act as 'demo' for Stand-alone Element-Screenshot attachment to Report)
	@FindBy(css="#login_credentials > h4")
	private WebElement acceptedUsernames; // only headline of the 'accepted usernames' (not the content itself)
	@FindBy(css=".login_password > h4")
	private WebElement passwordForUsers; // only headline of: 'Password for all users' (not the content itself)

	//	constructor: pass the 'driver' to BasePage (initialize at the Test-class, then passed to Page-class, and then directly on to BP)
	public LoginPage(WebDriver driver) {
		super(driver);
	}

	/** ★★ methods below ▼ encapsulates UI functionality interactions ‹-› and used in Test-Cases, needing those operational steps ★★ */
	/*	methods represent actions that can be performed on page elements during test (that mimic basic users' behavior & interaction) */

	//	login function implementation  ->  only after a successful login is performed, the user can access in-to any page in the system…
	@Step("Login-attempt with Credentials in Text-fields: User & Pass -and- Click to Commit them") // Allure annotation describing step!
	public void login(String username, String password) {
		fillText(usernameField, username); // fillText = wait visible, highlight, validate 'input', clear all + fill text (see BasePage)
		fillText(passwordField, password);
		waitTime(Duration.ofSeconds(1)); // sleep(1000); TODO -> mark it out if Testing performed (upon not used for DEMO mode showcase)
		click(loginBtn); // submits login…
	}

	//	perform a successful login (implementing the above, using inputs of proper credentials (from 'properties' file)!
	public void loginUsingValidCredentials() {
		login(ConfPropertiesUtil.readProperty("user"), ConfPropertiesUtil.readProperty("pass"));
	}

	//	implement a click on login function -> NOTE: this only clicks (without filling in any user & pass in fields)
	public void clickLogin() {
		click(loginBtn);
	}

	//	implement a click on Error Button function -> NOTE: it becomes visible, only after a failed login…
	public void clickErrorButton() {
		if (isDisplayed(errorCloseButton)) {
			click(errorCloseButton);			
		}
	}

	//	taking a screenshot implementation-example of the 'Close Error' button WebElement -> NOTE it's captured only after failed login!
	//	implemented on:  tc01_LoginFailures_Errors  & tc02_LoginFailureCloseError_MsgRemoved  methods -> in:  Test1_LoginAuthentication!
	public void printErrorButton() {
		if (isDisplayed(errorCloseButton)) {
			ReportAttachmentsUtil.attachElementScreenshot(errorCloseButton);
		}
	}

	/** ★★ methods below ▼ wraps various Validating operations ‹-› to be used in Test-Cases, in relevant part of tests' scenarios ★★ */
	/*	methods retrieve info (needed during tests) & compare it to expected result, to verify application-aspects function correctly */

	//	validate the current webpage is: login -> to verify, you actually got the expected Destination page (e.g. after browsing to it)…
	public boolean isLoginPage() { // no need users' input (this method will have the expected & compare)
		if (isDisplayed(loginWrapper)) { // check if certain element displayed (using wait) to determine!
			return true;
		} else {
			return false;
		}
	}

	//	validate login page is properly displayed (UI elements, empty text fields) -> only if all operands are true, it will return true
	@Step("GUI Test: Checking the Web-Page contain ALL the expected UI-elements") // Allure annotation, to describe step taken by method
	public boolean isProperUI() {
		if (isDisplayed(liLogoSwagLabs) &&
				isDisplayed(usernameField) &&  isTextFieldEmpty(usernameField) &&
				isDisplayed(passwordField) &&  isTextFieldEmpty(passwordField) &&
				isDisplayed(errorContainer)&& // empty spot for Error to be shown
				isAbsent(errorMessage) && isAbsent(errorCloseButton) && // error would exist, only after failed login…
				// isAbsent(loginBtn) && // uncomment and test (to get Test Failure), causes method to return false !!
				isDisplayed(loginBtn) &&
				isDisplayed(acceptedUsernames) &&
				isDisplayed(passwordForUsers)) {
			return true;
		} else {
			return false;
		}
	}

	//	validate login page Error field, is showing "Message" and 'x' Btn
	public boolean isErrorDisplayed() {
		if (isDisplayed(errorMessage) && isDisplayed(errorCloseButton)) {
			return true;
		} else {
			return false;
		}
	}

	//	get the text of errorMessage (if displayed), to validate later-on, you actually got the proper Message!  (for Failure TestCases)
	@Step("Geting the displayed Error Message from page") // Allure annotation…
	public String getErrorMsg() { // This can be used in assertEquals to compare this: actual.toLowerCase() with: expected.toLowerCase()
		if (isErrorDisplayed()) {
			if (getText(errorMessage).isEmpty()) {
				return "Error Displayed Without a Message";
			}
			return getText(errorMessage);
		}
		return "No Error was Displayed!";
	}

	//	validate the errorMessage is as expected (per Failure-Case tested) -> making use of the method above, to get current error text…
	public boolean isProperError(String expectedError) { // via test, input will be set to the method (expected error text), upon usage!
		String actual = getErrorMsg().toLowerCase();
		String expected = expectedError.toLowerCase();
		if (actual.equals(expected)) { // result of the comparison, determines if Test-Assertion Pass (true = proper-error) -or- Fail !!
			return true;
		} else {
			return false;
		}
	}

	//	possible implementation of a Case-Specific errorMessage validation, compared to a given predefined error for: "Missing Username"
	public boolean isMissingUsernameError() { // not needing any input
		String actualError = getErrorMsg();
		String expectedError = "Epic sadface: Username is required"; // predefined test-case related error message
		if ((actualError.toLowerCase()).equalsIgnoreCase(expectedError)) {
			return true;
		} else {
			return false;
		}
	}

	//	validate expected Text of WebElement: Usernames title
	@Step("Checking the Usernames Title text")
	public void assertAcceptedUsernamesText() {
		assertElementText(acceptedUsernames, "Accepted usernames are:"); // BasePage validation - Element Text equals to Expected Text !
	}

	//	validate expected Text of WebElement: Password title
	@Step("Checking the Password Title text")
	public void assertPasswordForUsersText() {
		assertElementText(passwordForUsers, "Password for all users:"); // Same for Password as of User...
	}

	//	validate Current Url is as Expected (using local dedicated method -> which could be replaced by the more general implementation)
	public void assertCurrentPageUrl() {
		String Expected = "https://www.saucedemo.com"; // Login page URL
		Assert.assertEquals(driver.getCurrentUrl().toLowerCase(), Expected.toLowerCase()); // Assert NOT using an intermediate method !!
	}
	/** NOTE: the above way ▲  is directly using the Assert upon need (with a specific url to compare, not dynamic & flexible = below) !
		I've also added a wrapping method in BasePage, that do something alike (validate Current Url is as Expected) -> USE it in Test :
	 	lp.assertCurrentUrl("expected"); // expected: full URL or any Partial sub-String of it, as input during method implementation */
}
