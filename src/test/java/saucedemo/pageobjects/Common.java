package saucedemo.pageobjects;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Common extends BasePage { // Extends BasePage, and acts as 'Middle-Tier' in Hierarchy, for any other Page which extends it!
// this Page is relevant after Successful Login, because only then, you're able to see the common elements of all pages (as found here)…
	
	//	pageFactory ► logo
	@FindBy(css=".app_logo") // upper logo - note: it's not the same locator as that of the Logo in LoginPage, prior to user logging-in…
	private WebElement appLogoSwagLabs;

	//	pageFactory ► menu component
	@FindBy(css="#react-burger-menu-btn") // menu icon ≡ to Open!
	private WebElement menuOpenBtn;
	@FindBy(css="#react-burger-cross-btn") // menu x close button
	private WebElement menuCloseBtn;
	@FindBy(css = "#inventory_sidebar_link") // menu to All Items
	private WebElement menuAllLnk;
	@FindBy(css = "#about_sidebar_link") // menu to About
	private WebElement menuAboutLnk;
	@FindBy(css = "#logout_sidebar_link") // menu to Logout
	private WebElement menuLogoutLnk;
	@FindBy(css = "#reset_sidebar_link") // menu to Reset (do: clears the cart from all added items)
	private WebElement menuResetLnk;
	@FindBy(css = ".bm-item-list > a") // parent object, containing All Menu-Links (working on Menu as a list of objects to choose from)
	private List<WebElement> listOfMenuLnks; // note: Menu Located as a whole (array) -> each button clicked on, would be found in code…

	//	pageFactory ► cart-icon component
	@FindBy(css="#shopping_cart_container") // shopping cart btn (to cart)
	private WebElement cartBtn;
	@FindBy(css=".shopping_cart_badge") // cart indication (items in cart)
	private WebElement itemsNumber;

	//	constructor: pass the 'driver' to BasePage (initialize at the Test-class, then passed to Page-class and in to Common & on to BP)
	public Common(WebDriver driver) {
		super(driver);
	}

	/** ★★ methods below ▼ encapsulates UI functionality interactions ‹-› and used in Test-Cases, needing those operational steps ★★ */
	/*	methods represent actions that can be performed on page elements during test (that mimic basic users' behavior & interaction) */

	/** ✿ menu component: operations ▼ */

	//	open menu (for link possibilities…), by click on  ≡  button (top left).  Note: click() itself, also checks element availability!
	public void openMenu() {
		click(menuOpenBtn);
	}

	//	close menu by clicking the X-button
	public void closeMenu() {
		click(menuCloseBtn);
	}

	//	click on a specific menu link: 'About'…  Note: Menu must be open, otherwise you'll get "element don't exist" exception!
	public void clickAboutMenuLnk() {
		click(menuAboutLnk);
	}

	//	implement a dynamic click on any given menu link, Upon using 'Link-Text' input (from from TestCase), to find relevant one…
	public void clickOnMenuLnk(String LnkTxt) {
		waitForElementVisibility(menuCloseBtn); // start after menu open (x btn visible)
		for (WebElement el : listOfMenuLnks) { // go over all links list, find relevant one & click it…
			if (el.getText().toLowerCase().contains((LnkTxt).toLowerCase())) {
				click(el);
				break;
			}
			else { // print upon not matching any of the list-elements checked in loop (for debugging! do not use regularly)
				  // System.out.println("requested link-txt: " + LnkTxt + ", don't match list-element-txt: " + getText(el));
			}
		}
	}

	/** ✿ cart component: operations ▼ */

	//	open cart, by clicking it - top right (to open YourCartPage, and see items added).
	public void openCart() {
		click(cartBtn);
	}

	/** ★★ methods below ▼ wraps various Validating operations ‹-› to be used in Test-Cases, in relevant part of tests' scenarios ★★ */
	/*	methods retrieve info (needed during tests) & compare it to expected result, to verify application-aspects function correctly */

	/** ✿ logo: validations ▼ */

	//	validate the Logo Component is displayed properly
	public boolean isLogoDisplayed() {
		if (isDisplayed(appLogoSwagLabs)) {
			return true;
		} else {
			return false;
		}
	}

	/** ✿ menu component: validations ▼ */

	//	menu 'debugging' method, which assist development, by Printing the MENU links 'Element Display State'…
	public void printElDisplayState() {
		System.out.println("menuOpenBtn displayed: " + isDisplayed(menuOpenBtn) +  " | absent: " + isAbsent(menuOpenBtn));
		System.out.println("menuCloseBtn displayed: " + isDisplayed(menuCloseBtn) +  " | absent: " + isAbsent(menuCloseBtn));
		System.out.println("menuAllLnk displayed: " + isDisplayed(menuAllLnk) +  " | absent: " + isAbsent(menuAllLnk));
		System.out.println("menuAboutLnk displayed: " + isDisplayed(menuAboutLnk) +  " | absent: " + isAbsent(menuAboutLnk));
		System.out.println("menuLogoutLnk displayed: " + isDisplayed(menuLogoutLnk) +  " | absent: " + isAbsent(menuLogoutLnk));
		System.out.println("menuResetLnk displayed: " + isDisplayed(menuResetLnk) +  " | absent: " + isAbsent(menuResetLnk));
	}

	//	validate the Menu-Open Button is displayed properly…  Note: this is NOT Indicating that Menu State is: Close
	public boolean isMenuOpenBtnDisplayed() {
		if (isDisplayed(menuOpenBtn)) {
			return true;
		} else {
			return false;
		}
	}

	//	validate the Close-Menu Button is displayed properly…  Note: this actually Indicates the Menu State is: Open
	public boolean isMenuCloseBtnDisplayed() {
		if (isDisplayed(menuCloseBtn)) {
			return true;
		} else {
			return false;
		}
	}

	//	validate in Close-Menu State, the Component has all related elements displayed properly
	public boolean isProperClosedMenuUI() {
		sleep(500); // a quick pause after action done…, to allow state be ready for validation 
		if (isMenuOpenBtnDisplayed() &&
				isAbsent(menuCloseBtn) &&
				isAbsent(menuAllLnk) &&
				isAbsent(menuAboutLnk) &&
				isAbsent(menuLogoutLnk) &&
				isAbsent(menuResetLnk)) {
			return true;
		} else {
			return false;
		}
	}

	//	validate in Open-Menu State, the Component has all related elements displayed properly -&- also proper Links Titles…
	public boolean isProperOpenedMenuUI() {
		sleep(500);
		if (isMenuCloseBtnDisplayed() &&
				// note the Menu btn is still there beneath the opened-menu
				isDisplayed(menuAllLnk) &&  getText(menuAllLnk).toLowerCase().contains("all items") &&
				isDisplayed(menuAboutLnk) &&  getText(menuAboutLnk).toLowerCase().contains("about") &&
				isDisplayed(menuLogoutLnk) &&  getText(menuLogoutLnk).toLowerCase().contains("logout") &&
				isDisplayed(menuResetLnk) &&  getText(menuResetLnk).toLowerCase().contains("reset app state")) {
			return true;
		} else {
			return false;
		}
	}

	/** ✿ cart component: validations ▼ */

	//	validate the Cart Button is displayed properly (without Indication Full or Empty)…
	public boolean isCartBtnDisplayed() {
		if (isDisplayed(cartBtn)) {
			return true;
		} else {
			return false;
		}
	}

	//	validate in Empty-Cart State, the Component has all related elements displayed properly (icon + no items amount indicated)
	public boolean isProperEmptyCartUI() { // should be Empty by Default (to begin with), unless items were already added to it… !
		sleep(500);
		if (isCartBtnDisplayed() &&
				isAbsent(itemsNumber)) {
			return true;
		} else {
			return false;
		}
	}

	//	validate in Full-Cart State, the Component has all related elements displayed properly (icon + added items amount indication)
	public boolean isProperFullCartUI() {
		sleep(500);
		if (isCartBtnDisplayed() &&
				isDisplayed(itemsNumber)) { // indication of items been added to cart
			return true;
		} else {
			return false;
		}
	}

	//	get number of cart-items (when indication displayed) - used for validations.   Note: this will Not work for No-Items in cart !
	public int getNumCartItems() {
		if (isProperFullCartUI()) {
			String sNum = getText(itemsNumber);
			int num=Integer.parseInt(sNum); // converts a string to int…
			return num;
		} else {
			return 0; // indication No itemsNumber is displayed in cart!
		}
	}
}
