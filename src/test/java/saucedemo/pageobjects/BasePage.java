package saucedemo.pageobjects;

import static org.testng.Assert.assertTrue;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import io.qameta.allure.Step;

public abstract class BasePage { // ☑ Page-Parent ► The SUPER for all pages & tests (so its' methods would be accessible, and used by…)

	//	variables
	protected WebDriver driver;
	protected WebDriverWait wait;
	protected Actions actions;
	protected JavascriptExecutor js;

	//	constructor (of the last layer - a must reach for all project-testing classes)
	public BasePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		this.actions = new Actions(driver);
		this.js = (JavascriptExecutor)driver;
	}

	/** ★★ methods below ▼ encapsulates selenium commands & wrap browser UI Functionalities ‹-› to be used in TestCases as needed ★★ */

	//	debug method which gets any string as input & print it -> implementation for 'any', can also-be the value returned by getText()!
	public void print (String any) {
		System.out.println(any);
	}

	//	return WebElement found By given 'locator' -> e.g. 'By.cssSelector("#username")'
	public WebElement find(By locator) {
		return driver.findElement(locator);
	}

	//	click on WebElement - after checking it's clickable & highlighting it beforehand (for demo usage).
	@Step("Click functionality on a WebElement") // @Step annotation - describes in Allure report what was performed by method…
	public void click(WebElement el) {
		waitForElementVisibility(el); // method checking Visibility (bellow…), make-use of Try-Catch mechanism to give feedback if not !
		waitForElementClickability(el);
		highlightElement(el, "Lime"); // only background color!  TODO -► mark-out the highlight & sleep, if Real Testing (not DEMO mode)
		sleep(500); // added sleep -> only to slow the test-steps a bit, so it actually look as if an operation was made on the element!
		el.click();
	}

	/** ✿ FOR DEMO ONLY !!! */
	//	highlight a WebElement With background-color (e.g: red, blue etc.), and afterwards, switch back to orig stile !
	private void highlightElement(WebElement el, String background) { // implementation: highlightElement(el, "Green");
	// private void highlightElement(WebElement el, String background, String border) { // also add border-line-color !
		waitForElementVisibility(el);
		String originalStyle = el.getAttribute("style"); // to keep orig style, for back-switching, after highlighting…
		String newStyle = "background-color: " + background + ";" + originalStyle; // only background -&- dynamic color
		// String newStyle= "background-color:Red;" + originalStyle; // a set background, no input needed & no border ▼
		// String newStyle= "background-color: "+ background + ";"+ "border: 1px solid " + border + ";"+ originalStyle;
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("var tmpArguments = arguments;setTimeout(function () {tmpArguments[0].setAttribute('style', '"
				+ newStyle + "');},0);", el);
		js.executeScript("var tmpArguments = arguments;setTimeout(function () {tmpArguments[0].setAttribute('style', '"
				+ originalStyle + "');},500);", el);
	} // TODO -► Make sure to mark-out, if NOT working in DEMO mode (from other methods), the highlight & related sleep

	//	click (overloading-method = same name, with minor diff) - do as above, BUT gets a locator  (instead of WebElement)…
	public void click(By locator) { // gets input of type 'By'='By.cssSelector("#username")' -> find element, and click it!
		find(locator).click();
	}

	//	submit (mimics 'enter', to submit a form) -> the function is applicable only for elements' type 'form'!
	public void submit(WebElement el) {
		el.submit();
	}

	//	fill in given text, into input-field (given element -- using 'locator' not 'element')
	public void typeIn(String inputText, By locator) {
		find(locator).sendKeys(inputText);
	}

	//	fill in given text, into input-field (given element -- using 'element' not 'locator'), wait till visible beforehand…
	public void sendKeys(WebElement el, String inputText) {
		waitForElementVisibility(el);
		el.sendKeys(inputText);
	}

	//	clear text from field (empty any current textual content, if possible)
	public void clear(WebElement el) {
		el.clear();
	}
	
	//	fill in a given text string, into input-field given element - clear field visible beforehand… combined function - SHORT version!
	public void fillTextQuick(WebElement el, String text) {
		clear(el);
		sendKeys(el, text);
	}

	//	fillText (in use = Complex version): fill text in field - wait visible, highlight, validate 'input', empty content beforehand… !
	public void fillText(WebElement el, String text) {
		waitForElementVisibility(el);
		highlightElement(el, "Yellow"); // only background color! TODO -► mark-out the highlight if Real Testing (when not in DEMO mode)
		if (el.getTagName().contains("input")) {
			if (el.isDisplayed()) {
				if (isTextFieldEmpty(el)) // using isTextFieldEmpty (see bellow, checking the 'getAttribute')…
				{
					el.sendKeys(text);
				}
				else { // if not empty…
					el.clear();
					el.sendKeys(text);
				}
			}
		}
		else { // if by any chance, element isn't 'input' - print & do nothing!
			System.out.println("can't fill text to element: " + el + " because it is NOT of type: input");
		}
		sleep(500); // added, only so it looks as if an operation was made on the element (to slow the test-steps a bit) for DEMO Mode !
	}

	//	return the Select object (element must be of any type relevant for selection - upon using selection is needed - see below…)
	public Select select(WebElement el) {
		Select select = new Select(el);
		return select;
	}

	//	select by value (mimic click on an option form a select dropdown) - gets WebElement -&- do Submit (click 'enter' upon fill form)
	public void selectByValue(WebElement el, String value) {
		Select s = new Select(el);
		s.selectByValue(value);
	}
	/**	TODO -► add more Selects operations... -> By index\text */

	//	check if element is on screen - combined with validation (try-catch) --> see several possible ways to do it, below:
	public boolean isDisplayed(WebElement el) { // the preferred way (using WebElement as input) if its displayed, returns boolean true!
		try {
			return el.isDisplayed(); // if OK -> return true, BUT - upon Element is not found (caught exceptions below) -> return false…
		} catch (org.openqa.selenium.NoSuchElementException exception) {
			return false;
		} catch (org.openqa.selenium.StaleElementReferenceException exception) {
			return false;
		}
	}

	//	check as above (also same name), BUT gets a locator instead of element.
	public boolean isDisplayed(By locator) {
		try {
			return find(locator).isDisplayed();
		} catch (org.openqa.selenium.NoSuchElementException exception) {
			return false;
		} catch (org.openqa.selenium.StaleElementReferenceException exception) {
			return false;
		}
	}

	//	check an Element is NOT Displayed (Absent from page) -> get Element as input, check if it's NOT displayed & return boolean (T\F)
	public boolean isAbsent(WebElement el) {
		try {
			if (el.isDisplayed()) {
				return false;
			} 
		} catch(Exception e) {
			return true;
		}
		return true;
	}

	//	check if element is currently Enabled
	public boolean isEnabled(WebElement el) {
		try {
			return el.isEnabled();
		} catch (org.openqa.selenium.NoSuchElementException exception) {
			return false;
		}
	}

	//	check current selection state of a given Element (one which can be selected - can add a verification 'IF' prior to the decision)
	public boolean isSelected(WebElement el) {
		try {
			return el.isSelected(); // if selected -> return true
		} catch (org.openqa.selenium.NoSuchElementException exception) {
			return false;
		}
	}

	//	check if a text-field element is Empty - validate for given element (type 'input'), the 'value' attribute (must exist) is empty
	public boolean isTextFieldEmpty(WebElement el) {
		if (getAttribute(el, "value").isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	//	get the Text of a WebElement - wait till visible, highlight & retrieve Text…
	public String getText(WebElement el) {
		waitForElementVisibility(el);
		highlightElement(el, "DeepPink"); // only background color!      TODO -► mark-out the highlight if Real Testing (not DEMO mode)…
		return el.getText();
	}

	//	get the TagName of a WebElement (e.g. "input")…
	public String getTagName(WebElement el) {
		return el.getTagName();
	}

	//	retrieve a given element attributes' value (for a given attribute name - attribute must exist)
	public String getAttribute(WebElement el, String attribute) {
		return el.getAttribute(attribute);
	}

	//	get text (if any) inside a given text-field (el type 'input') -> validate & retrieve the textual-value of attributeName="value"!
	public String getTextFieldValue(WebElement el) {
		if (el.isDisplayed() && el.getTagName().contains("input")) {
			if (isTextFieldEmpty(el)) {
				return "" ;
			} else {
				return getAttribute(el,"value");
			}
		}
		else { // if, by any chance, the given element is NOT a type 'input' - print the issue & do nothing
			return "can't fetch relevant data from element: ";
		}
	}

	//	retrieve Current page URL (wrapping of browser-driver related capability, not needing any input)
	public String getCurrentUrl() {
		return driver.getCurrentUrl();
	}

	//	verify page url 'is as expected' = check contains given partial string (a 2ed layer over above method, adding strength to code)…
	public boolean doesUrlInclude(String str) {
		if (getCurrentUrl().toLowerCase().contains(str)) {
			return true;
		} else {
			return false;
		}
	}

	//	retrieve the page Source (wrap driver capability without input)
	public String getPageSource() {
		return driver.getPageSource();
	}

	//	retrieve the page Title (wrap driver capability without input)
	public String getPageTitle() {
		return driver.getTitle();
	}

	//	verify page title 'is as expected' = checks if matching a given string (a 2ed layer over above method, adding strength to code)…
	public boolean verifyPageTitle(String expectedPageTitle) {
		return getPageTitle().equalsIgnoreCase(expectedPageTitle);
	}

	//	browse to a given URL -> go to a given page on the opened browser (or - use the below: navigate to url - which do the same plus)
	public void visit(String url) {
		driver.get(url); // only open a given web page (don't maintain browser history & cookies, and don't wait till page fully loaded)
	}

	//	navigate to url -> this is similar to: driver.get(url); ( url="http://www.google.com" ) -> see stated above & doing the same !!!
	public void navigateTo(String url) {
		driver.navigate().to(url); // open page+ save history+ cookies -so- possible to navigate between pages (back, forward & refresh)
	}

	//	browser navigation back to previous page
	public void navigateBack() {
		driver.navigate().back();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body"))); // wait for page to reload after navigating back!
		// sleep(500); // we can also add this, just to make sure there is time for re-loaded (if the above wait was not enough for it)!
	}

	//	browser navigation forward to following page (possible only if you did backward beforehand!…)
	public void navigateForward() {
		driver.navigate().forward();
	}

	//	browser refresh on current page (a forced fetch webpage from the server instead of load it from the cache)
	public void navigateRefresh() {
		driver.navigate().refresh();
	}

	//	navigate handle (single) = do…
	public void navigateToHandle() {
		driver.getWindowHandle();
	}

	//	get main Window Handle = do…
	public String mainWindow() {
		return driver.getWindowHandle();
	}

	//	navigate handles (plural) = do…
	public void navigateToHandles() {
		driver.getWindowHandles();
	}

	//	get a Set of all Window Handles = do…
	public Set<String> handleWindows() {
		return driver.getWindowHandles();
	}
	/** TODO -► add more operations -> Window & Alert */

	//	do alert accept - wait for alert to be present then click on OK
	public void alertOK() {
		waitForAlert();
		driver.switchTo().alert().accept();
	}

	//	do alert accept after filling text (could have use 'Overloading', and to also call this method alertOK - same as above)
	public void alertFillTextSubmitOK(String text) {
		waitForAlert();
		driver.switchTo().alert().sendKeys(text);
		driver.switchTo().alert().accept();
	}

	//	do alert cancel -  click on dismiss
	public void alertCancel() {
		driver.switchTo().alert().dismiss();
	}

	//	a generic sleep - BUT try Not to Use it (execution is paused the full length of time set)  ->  Better to use Wait-Till-Element !
	public void sleep(long millisec) {
		try {
			Thread.sleep(millisec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/** Note: any 'wait' used below ▼ is declared & set to 5 sec within the constructor above ▲ */
	//	an implicit wait (pause the full time length, or less if possible, trying to find an element not immediately available)…
	public void waitTime(Duration duration) {
		driver.manage().timeouts().implicitlyWait(duration);
	}

	//	wait till a WebElement is Visible on page (using a Try-Catch mechanism, for feedback if not), to use in other methods / asserts…
	public void waitForElementVisibility (WebElement el) {
	    try {
	        wait.until(ExpectedConditions.visibilityOf(el));
	    } catch (Exception e) { // or: TimeoutException e
	        System.out.println("Element not Visible:\n" + e.getMessage());
	    }
	}

	//	wait till Visible (as above. BUT wrapper method using the By not the Element)
	public void waitVisibility (By elementBy) {
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(elementBy));
	}

	//	wait till Visible (wrapper method using the List), to be used in relevant methods… Note: some recommend not to use!
	public void waitForListBeSeen(List<WebElement> list) {
		wait.until(ExpectedConditions.visibilityOfAllElements(list));
	}

	//	wait till a WebElement (el) is NOT Visible (not seen on page) - Negative!
	public void waitForInvisibility(WebElement el) {
		wait.until(ExpectedConditions.invisibilityOf(el));
	}

	//	wait till a WebElement is Clickable (and thus, also Visible), using a Try-Catch mechanism - to give feedback, if not clickable !
	public void waitForElementClickability(WebElement el) {
	    try {
	        wait.until(ExpectedConditions.elementToBeClickable(el));
	    } catch (Exception e) {
	        System.out.println("Element not clickable:\n" + e.getMessage());
	    }
	}

	//	click element after checking Clickability beforehand (using WebElement)
	public void clickClickable (WebElement el) {
		waitForElementClickability(el);
		el.click();
	}

	//	click element after checking Visibility beforehand (using By)
	public void clickVisible (By elementBy) {
		waitVisibility(elementBy);
		driver.findElement(elementBy).click();
	}

	//	enter input text after check Visibility beforehand (using By)
	public void inputToVisible (By elementBy, String text) {
		waitVisibility(elementBy);
		driver.findElement(elementBy).sendKeys(text);
	}

	//	get text after check Visibility beforehand (using By)
	public String readFromVisible (By elementBy) {
		waitVisibility(elementBy);
		return driver.findElement(elementBy).getText();
	}

	//	return the Element (using its CSS Locator) upon condition met -> wait till it is Clickable  !
	public WebElement getElementWaitTillClickable(String css) {
		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(css)));
		return el;
	}

	//	return the Element (using its CSS Locator) upon condition met -> wait till Visible on screen…
	public WebElement getElementWaitTillVisible(String css) {
		WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(css)));
		return el;
	}

	//	return the Element (using its CSS Locator) upon condition met -> wait till Present on the DOM
	public WebElement getElementWaitTillPresent(String css) {
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(css))); // checks also the Hidden Elements
		return el;
	}

	//	wait for alert to be present - this method is being used in Alerts' related methods (see above)…
	public void waitForAlert() {
		wait.until(ExpectedConditions.alertIsPresent());
	}

	//	hover (mouse-over WebElement), to enable seeing stuff which is visible only upon hovering over element
	public void moveTo(WebElement el) {
		waitForElementVisibility(el);
		actions.moveToElement(el).build().perform();
		highlightElement(el, "Cyan"); // only background color !         TODO -► mark-out the highlight if Real Testing (not DEMO mode)…
		// System.out.println("mouse was just moved…");
	}

	//	drag & drop (element on-to element)
	public void dragAndDrop(WebElement el, WebElement dropZone) {
		waitForElementVisibility(el);
		waitForElementVisibility(dropZone);
		actions.dragAndDrop(el, dropZone).perform();
	}
	/** TODO -► add more Mouse Actions -&- Asserts */

	//	assert element text as expected: wait, highlight, get text & compare to expected (result determine's if Test-Case Passed / Not)…
	public void assertElementText (WebElement el, String expectedText) {
		waitForElementVisibility(el);
		highlightElement(el, "OrangeRed"); // only background color      TODO -► mark-out the highlight if Real Testing (not DEMO mode)…
		String foundElementText = getText(el);
		Assert.assertEquals(foundElementText.toLowerCase(), expectedText.toLowerCase());
	}

	//	assert Current Page Url, is as Expected (contains a given sub-String of URL)
	@Step("Asserting Current Url is as Expected")
	public void assertCurrentUrl(String expected) { // input: expected sub-String of URL & check if page URL contains it (can use full)!
		sleep(500); // general sleep of 1/2 sec. (just to make sure it was fully loaded…) 
		assertTrue(doesUrlInclude(expected), "URL does Not include expected: " + expected + " (sub-String) ! ");
	}
}
