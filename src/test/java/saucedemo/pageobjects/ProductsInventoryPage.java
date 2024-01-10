package saucedemo.pageobjects; // Please NOTE, that the explanation-comments are filling this page, but its the last page to have them !

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

public class ProductsInventoryPage extends Common { // Inventory (products) page + 'Common' as middle-tier in Hierarchy above 'BasePage'
// this Page onwards, relevant only after Logging-in. Because Common extends BasePage, you can also reach all methods from here as well…

	//	pageFactory ► page title (top-left, below menu) = 'Products'…
	@FindBy(css=".title")
	private WebElement pgTitle;

	//	pageFactory ► inventory section - working on 'Inventory Items' indirectly, as List of objects - to choose the specific item from
	@FindBy(css = ".inventory_list")
	private WebElement inventory; // this is the 'parent-object' of All 'Items' on page (as whole)…
	@FindBy(css = ".inventory_list .inventory_item")
	private List<WebElement> inventoryList; // that's a wrapper locating all elements of type 'item' (as a LIST of 6 product-elements) !
	/** There are 6 inventory elements in List ▲ (each one = Item). Each element consist of sub-elements having the same HTML structure:
	Pic []; Name (Sauce Labs…); Description (Get your…); Price ($9.99); Button (Add to cart \ Remove) - Functions, done on one of them -
	every sub-element (below) is located within the HTML-region of each Product & found by its-own list, OR could be located directly */
	@FindBy(css = "img.inventory_item_img")
    private List<WebElement> iPic; // locate 6 sub-elements product-Pic, as a List (to pin exact pic per item, use the 'for-each' loop)!
    @FindBy(css = ".inventory_item_name")
    private List<WebElement> iName; // 6 product-Names…
	@FindBy(css = ".inventory_item_desc")
	private List<WebElement> iDesc; // 6 product-Descriptions…
	@FindBy(css = ".inventory_item_price")
	private List<WebElement> iPrice; // 6 product-Prices…
	@FindBy(css = ".btn.btn_small.btn_inventory") // Add css: ".btn_primary.btn_inventory" -> Remove css: ".btn_secondary.btn_inventory"
	private List<WebElement> iButton; // 6 product-Buttons (per each item, locating both the states which could be: 'Add' -or- 'Remove')

	//	pageFactory ► sorting items by… -&- Display-order selection component (dropdown below Cart)
	@FindBy(css=".select_container")
	private WebElement sortOptionsIcon; // top-right container of selection sort-options dropdown - also shows currently active, as text
	@FindBy(css=".active_option")
	private WebElement activeSortOption; // the active sort-option is shown as highlighted (upon expanding sorting component drop-down)!
	@FindBy(css=".product_sort_container")
	private WebElement objectSelect; // the Select container object, with all Option tags underneath it…, to be used Directly or as List
	@FindBy(css = ".product_sort_container option")
	private List<WebElement> sortOptionsList; // all sort options as a List. each option clicked (e.g. click & select), is done in code!

	//	constructor: pass the 'driver' to BasePage (initialize at the Test-class, then passed to Page-class and in to Common & on to BP)
	public ProductsInventoryPage(WebDriver driver) {
		super(driver);
	}

	/** ★★ methods below ▼ encapsulates UI functionality interactions ‹-› and used in Test-Cases, needing those operational steps ★★ */

	/** ✿ inventory-items section: working on the items' list of products… ▼ */
	/* 	several 'combined' functions (exercises robust & flexibility), to click-on… & get-data etc. (see validation section, bellow…) */

	//	first, I add the predefined 'Items' to a List, using default sort by A to Z (to be used as items-list, in all testing methods) !
	public List<String> listItems() { // This could have been set as a separated data-source  ->  There is No Need to Use this in Tests…
		List<String> item = new ArrayList<>();  // Create a List of Products' Name - each represent an Item  ->  Add values to the list:
		item.add("Sauce Labs Backpack");
		item.add("Sauce Labs Bike Light");
		item.add("Sauce Labs Bolt T-Shirt");
		item.add("Sauce Labs Fleece Jacket");
		item.add("Sauce Labs Onesie");
		item.add("Test.allTheThings() T-Shirt (Red)");
		return item;
	}

/**	then, I wish to find & target the relevant sub-element of each product-region (per 'item') --> the method needs the input arguments:
 *	@param subElement	(String arg, pointing the sub-element. valid values are: "name", "pic", "desc", "price", "button").
 *	@param productName	(String arg, pin-pointing a Product by its itemName. Valid value is any from listItems e.g. "Sauce Labs Onesie")
 */	//	locating the needed sub-element (from its specified list) relevant to operation we want to perform on the item (from list above)
	public WebElement findSubElementOfProduct(String subElement, String productName) { // this method would be used by other, see below!
		List<WebElement> targetList;
		if (productName.equalsIgnoreCase("Sauce Labs Backpack") ||
			productName.equalsIgnoreCase("Sauce Labs Bike Light") ||
			productName.equalsIgnoreCase("Sauce Labs Bolt T-Shirt") ||
			productName.equalsIgnoreCase("Sauce Labs Fleece Jacket") ||
			productName.equalsIgnoreCase("Sauce Labs Onesie") ||
			productName.equalsIgnoreCase("Test.allTheThings() T-Shirt (Red)")) {
			if (subElement.equalsIgnoreCase("name")) {
				targetList = iName;
			} else if (subElement.equalsIgnoreCase("pic")) {
				targetList = iPic;
			} else if (subElement.equalsIgnoreCase("desc")) {
				targetList = iDesc;
			} else if (subElement.equalsIgnoreCase("price")) {
				targetList = iPrice;
			} else if (subElement.equalsIgnoreCase("button")) {
				targetList = iButton; // note: 'button' subElement considers both state-possibilities (Add & Remove) at the same time !!
			} else {
				throw new IllegalArgumentException("Invalid sub-Element: \"" + subElement +
		    		"\" input used, NOT-Fitting expected: name, pic, desc, price, button");
			}
		} else {
	    	throw new IllegalArgumentException("Invalid Product Name: " + productName);
	    }
		WebElement targetItemSubElement = null;
		for (int i = 0; i < iName.size(); i++) {
			if (getText(iName.get(i)).equalsIgnoreCase(productName)) {
				try {
					targetItemSubElement = targetList.get(i);
				} catch (Exception e) {
			        System.out.println("Got an Exception while trying to locate the Sub Element: '" + subElement + "'\n" + e);
				} break;
			}
		}
		return targetItemSubElement;
	}

/**	action method: click on any relevant product-region sub-element (identified using findSubElementOfProduct method above) - arguments:
 *	@param subElement	(String arg, pointing the clickable sub-element. valid values are: "name", "pic", "button" = both: add \ remove)
 *	@param productName	(String arg, pin-pointing a Product by its itemName. Valid value is any from listItems e.g. "Sauce Labs Onesie")
 */	//	clicking relevant sub-element (locating the proper, is done using the above method for targeting the proper SubElementOfProduct)
	public void clickSubElementOfProduct(String subElement, String productName) { /* valid inputs bellow:
				productName: should fit product's name || subElement: name, pic, desc, price, button (for ) */
		WebElement element = findSubElementOfProduct(subElement, productName);
		if (subElement.equalsIgnoreCase("name")) {
			sleep(200); // note: the short sleep is for better visual appearance!
			click(element);
			System.out.println("just clicked: name (of: " + productName +")…");
		} else if (subElement.equalsIgnoreCase("pic")) {
			sleep(200);
			click(element);
			System.out.println("just clicked: pic (of: " + productName +")…");
		} else if (subElement.equalsIgnoreCase("button")) { // regarding Add \ Remove - consider them both, as same !
			sleep(200);
			click(element);
			System.out.println("just clicked: add-remove button (of: " + productName +")…");
		} else {
	    	throw new IllegalArgumentException("input might not be: name, pic, button  -or-  various other issues…");
	    }
	}

	//	adding a specific element to cart (using the clickSubElementOfProduct for a case-specific condition…)
	// 
	public void addProductToCart(String productName) {
		if (isProductSubElementDataExpected("button", productName, "add")) {
			clickSubElementOfProduct("button", productName);
		} else {
			System.out.println("Note: that this item is already added");
		}
	}
	
	//	removing a specific element from cart (using the clickSubElementOfProduct for a case-specific condition…)
	public void removeProductFromCart(String productName) {
		if (isProductSubElementDataExpected("button", productName, "remove")) {
			clickSubElementOfProduct("button", productName);
		} else {
			System.out.println("Note: that this item is already removeed");
			
		}
	}

	/** ✿ sorting-options section: working on items' list sort-by… functions, for the dropdown selection options ▼ */

	//	implement open Sort DropDown container - lick on Sort icon (for sorting selection options)
	public void clickSortOptions() {
		click(sortOptionsIcon); // click the sort-button to open the various options (second click closes)
		// driver.findElement(By.cssSelector(".select_container")).click(); // the pre-testNG way to code!
	}

/**	action method: select the relevant sortBy option - by Text (to sort the Products' List display by given order) -- needing arguments:
 *	@param sort	(String arg, sort… By. valid values are: "Name (A to Z)", "Name (Z to A)", "Price (low to high)", "Price (high to low)")
 */	//	clicking relevant sub-element (locating the proper, is done using the above method for targeting the proper SubElementOfProduct)
		public void selectSortOptionByText(String sort) { // method use input to find relevant in list (could also be by Attribute) !
			getSelect().selectByVisibleText(sort);
			System.out.println("just sorted by: " + sort);
		}

	//	implement mouse-over & click function, to select given sort, of all available sorting-options (VisibleText input from TestCase)!
		public void selectMouseOverSortOptionByAttributet(String sortBy) { // method uses input - to find option using the getAttribute!
		clickSortOptions();
		sleep(500);
	}	

	/** ★★ methods below ▼ wraps various Validating operations ‹-› to be used in Test-Cases, in relevant part of tests' scenarios ★★ */

	/** ✿ general section: inventory page validations ▼ */ // TODO -> implement common page methods as well !!!

	//	validate the current webpage is: inventory page (only by URL)  ->  to verify, you actually got to the expected destination page…
	public boolean isInventoryPage() { // no need users' input (this method will have the expected & compare)
		if (getCurrentUrl().toLowerCase().contains("inventory.html")) { // checking if URL contains expected string (relevant & unique)…
			return true; // proper page !
		} else {
			return false;
		}
	}

	//	validate the current webpage is: inventory page (only by page-title compared to expected -> as done on get URL see above…):
	public boolean isProductsInventoryPageTitle() { // no need users' input (method already have the expected title, to compare to)
		if (isDisplayed(pgTitle) && getText(pgTitle).equalsIgnoreCase("PRODUCTS")) { // check element is displayed & match expected
			return true; // proper page !
		} else {
			return false;
		}
	}

	//	validate the inventory page is properly displayed (all UI elements etc.) --> only if all operands are true, it will return true!
	//	note: I'm using IF with 'AND' binary operator, so only if all operands are true, the result is true (otherwise, result is false)
	public boolean isProperProductsInventoryPageUI() {
		if (isInventoryPage() && // Verify proper page (using method above)…
			isLogoDisplayed() && // SwagLabs Logo - it is taken from common!
			isMenuOpenBtnDisplayed() && // Menu btn, also taken from common!
			isCartBtnDisplayed() && // Shopping cart (empty) -> from common!
			isProductsInventoryPageTitle() && // Checks that this page Title has proper expected text ("PRODUCTS")… -> see above!
			isSortContainerDisplayedProperly("name (a to z)") && // 'Sorting' is visible, with default selection-by: "NAME (A to Z)"…
			isDefaultItemsListDisplayedProperly() // Check the inventory items displayed properly… (below) -► TODO: Add SORT Display!
			) {
			return true;
		} else {
			return false;
		}
	}

	//	implement boolean function, to validate the ProductsInventoryPage has all needed elements displayed properly, in Default State !
	//	Note: I'm using IF with OR binary operator, so if at least one operands is true, the result is true! (otherwise result is false)
	//  I'm using OR because I'm checking that: NO Negative-Condition are to be found !!!
	public boolean isDefaultItemsListDisplayedProperly() { // checks the inventory items exist with all sun elements…
		if (isDisplayed(inventory)) { // checks the inventory wrapping-object of all items exist  (not the items objects themselves) !!!
			List<String> itemsList = listItems(); // using above method, first method (to get all products)
			for (String item : itemsList) { // checks for all, state of : name, pic, desc, price, button (non added, Remove not visible)
				// note: I'm using IF with OR conditions -> so if one Negative-Condition found to be 'True', the IF would return: false!
				if (getSubElementDataOfProduct("name", item).isEmpty() || // method described just bellow… ▼
					getSubElementDataOfProduct("pic", item).isEmpty() ||
					getSubElementDataOfProduct("desc", item).isEmpty() ||
					getSubElementDataOfProduct("price", item).isEmpty() ||
					getSubElementDataOfProduct("button", item).isEmpty() ||
					getSubElementDataOfProduct("button", item).equalsIgnoreCase("Remove"))
					// TODO: add to method, a relevant condition, that checks the default sorting of List, is displayed by A to Z order…
				{
					return false; // if its returned, isProperProductsInventoryPageUI method will also return false and Fail TestAssert!
				}
			}
			return true; // true is returned ONLY when no Negative-Condition was found -and- All Items exist with All sections per Each!
		}
		return false;
	}

	/** ✿ inventory-items section: items' list of products (and all relevant item-sub-regions), various related validations ▼ */
	
/**	using above methods, of inventory section general operation - 'combined' functions to fetch-data of sub element (according to input)
 * 	get relevant data from a product-region sub-element (identified using findSubElementOfProduct method) - method arguments:
 *	@param subElement	(String arg, pointing the sub-element. valid values are: "name", "pic", "desc", "price", "button").
 *	@param productName	(String arg, pin-pointing the Product by Name. Valid value is: any product  (e.g. "Sauce Labs Backpack").
 */	//	fetching relevant data of sub-element (locating the relevant region using the combined methods. above…)
	public String getSubElementDataOfProduct(String subElement, String productName) {
		WebElement element = findSubElementOfProduct(subElement, productName);
		if (subElement.equalsIgnoreCase("name")) {
			sleep(200);
			return getText(element);
		} else if (subElement.equalsIgnoreCase("desc")) {
			sleep(200);
			return getText(element);
		} else if (subElement.equalsIgnoreCase("pic")) {
			sleep(200);
			return element.getAttribute("src");
		} else if (subElement.equalsIgnoreCase("price")) {
			sleep(200);
			return getText(element);
		} else if (subElement.equalsIgnoreCase("button")) {
			sleep(200);
			String btnState;
			if (getText(element).equalsIgnoreCase("Add to cart")) {
				btnState = "add";
			} else if (getText(element).equalsIgnoreCase("Remove")) {
				btnState = "remove";
			} else {
				btnState = "unknown";
			}
			return btnState;
		} else {
			throw new IllegalArgumentException("Problem!)");
		}
	}

/**	using above method, to fetch-data of sub element (according to input) -&- compare it to expected result (e.g. to be used in asserts)
 * 	method arguments:
 *	@param subElement	(String arg, pointing the sub-element. valid values are: "name", "pic", "desc", "price", "button").
 *	@param productName	(String arg, pin-pointing the Product by Name. Valid value is: any product  (e.g. "Sauce Labs Backpack").
 *	@param dataExpected	(String arg, stating the Expected case. Valid value is: proper Data  (e.g. for "Button": "add" or "remove").
 */	//	comparing fetched data of relevant products' sub-element, to expected result…
	public boolean isProductSubElementDataExpected(String subElement, String productName, String dataExpected) {
		if (getSubElementDataOfProduct(subElement, productName).equalsIgnoreCase(dataExpected)) {
			return true;
		} else {
			return false;
		}
	}

	/** ✿ sorting-options section: proper sort display validations ▼ */

	//	getting the select as an OBJECT to use methods such as: …().selectByVisibleText(), …().getFirstSelectedOption().getText(), etc…
	public Select getSelect() {
		return select(objectSelect);
	}

	//  implement a boolean function, to validate the Sorting-Component is displayed properly, with the expected sort option (as input)!
	public boolean isSortContainerDisplayedProperly(String sortExpected) { // e.g. sortExpected input = "name (a to z)" = Default State!
		if (isDisplayed(sortOptionsIcon) && // Sorting-Container displayed
			getActiveSortOption().toLowerCase().contains(sortExpected.toLowerCase()) // see in that method, described just bellow… ▼
			) {
			return true;
		} else {
			return false;
		}
	}

	//	implement get info, to retrieve the Currently 'Active Sort Option' (to assist with assessing the ActiveSort)
	public String getActiveSortOption() { // to be used in assertEquals - compare: actual.toLowerCase() -&- expected.toLowerCase()
		if (isDisplayed(activeSortOption)) {
			return getText(activeSortOption); // currently active = any given is only relevant, till becomes obsolete - upon any change!
		}
		return "No Active Sort Option was Displayed!";
	}

	//	inventory get-sort item-name
	//	getting the names only, of ALL items 'as is', without any sort function done on List (used as a base-line, for SORT assertions)!
	public List<String> getInventoryListNames() {
		// For Assertion, add a List (to hold all string values of names) -> will be derived from the ITEMs inventoryList, as following:
		List<String> inventoryListNames = new ArrayList<>(); // New List (of orig state, before sort)
		// extract items name-string from orig List -and- insert to the above ^ inventoryListNames
		for (WebElement el : inventoryList) { // Traverse the Orig inventoryList WebElements list (traverse meaning: go over all parts)!
			String elName = el.findElement(By.cssSelector(".inventory_item_name")).getText(); // 'name' is a sub-element of any ITEM !!!
			System.out.println("el-Name: " + elName);
			inventoryListNames.add(elName); // add each found name to the List of inventoryListNames (to be used later on)
		}
		return inventoryListNames;
	}	// For Assertion, add a List to hold all number values of prices --> will be derived from the ITEMs inventoryList, as following:

	// use the above List & sort ascending / descending - to compare with the 2 sort by price cases ascending & descending:
	public List<String> getSortedInventoryListNamesTrueAscFalseDece(boolean AD) { // AD stands for Ascending \ Descending !
		List<String> itemsSortedByNameAD = new ArrayList<>();
		itemsSortedByNameAD = getInventoryListNames();
		if (AD) { // AD = True (Asc)
			Collections.sort(itemsSortedByNameAD); // Sorting orig state List - in Ascending order (A to Z)
			return itemsSortedByNameAD;
		} else { // AD = False (Desc)
			Collections.sort(itemsSortedByNameAD, Collections.reverseOrder()); // Descending order (Z to A)
			return itemsSortedByNameAD;
		}
	}

	//	inventory get-sort item-price
	// implement method getting the string prices of all items - and CONVERTING to Numbers (will be used later on for SORT assertions) !
	public List<Double> getInventoryListPrices() { // making use of: inventoryList (from PageFactory)
		// For Assertion, add a List to hold all number values of prices --> will be derived from the ITEMs inventoryList as following :
		List<Double> inventoryListPrices = new ArrayList<>(); // New List (of orig state, before sort)
		// extract items price string from orig List & convert to number and insert to above ^ inventoryListPrices
		for (WebElement el : inventoryList) { // Traverse the Orig inventoryList WebElements list (traverse meaning: go over all parts)!
			String elPrice = el.findElement(By.cssSelector(".inventory_item_price")).getText(); // price is a sub-element of an ITEM !!!
			// System.out.println("el-Price: " + elPrice); // Print String as is: $15.99
			String onlyPrice = elPrice.replace("$", ""); // replace & REMOVE the $ sign from string, to keep only text of number chars !
			Double priceNmber = Double.parseDouble(onlyPrice); // PARSE & convert the String (textual-chars) to Double (number) type ...
			// System.out.println(priceNmber); // ->  output = 15.99
			inventoryListPrices.add(priceNmber); // add each found price to the List of byPriceNumber (to be used later on)
		}
		return inventoryListPrices;
	}
	
	// use the above List & sort ascending / descending - to compare with the 2 sort by price cases ascending & descending:
	public List<Double> getSortedInventoryListPricesTrueAscFalseDece(boolean AD) { // AD stands for Ascending \ Descending!
		List<Double> itemsSortedByPriceAD = new ArrayList<>();
		itemsSortedByPriceAD = getInventoryListPrices();
		if (AD) { // AD = True (Asc)
			Collections.sort(itemsSortedByPriceAD); // Sorting orig state List - in Ascending order (low to high)
			return itemsSortedByPriceAD;
		} else { // AD = False (Desc)
			Collections.sort(itemsSortedByPriceAD, Collections.reverseOrder()); // Descending order (high to low)
			return itemsSortedByPriceAD;
		}
	}

	// implement a function to validate Current Active Sort Option Selected (by text, not by actual sort)
	public void assertCurrentActiveSort(String expected) { // input needed = expected text
		assertElementText(activeSortOption, expected); // make use of the BasePage function 'assertElementText'
	} // USE in test like this: pp.assertCurrentActiveSort("Name (A to Z)")
	
	// implement a validation that by default, before any change made - the 'Active Sort Selected' is the First Option of List !
	public void assertDefaultSortIsFirstOption() { // NOTE: this method don't use Page Factory !!! <----------------------------
		// to work on Options we need to have the Element + Select Object initialized
		WebElement sortOptions = driver.findElement(By.cssSelector(".product_sort_container")); // Options selection have other locator!
		Select selectSortOptions = new Select(sortOptions); // the Object manages the operations possibilities on: options selections !!
		// System.out.println("First Sort Option is: " + selectSortOptions.getOptions().get(0).getText()); // first option = 0 !!
		String sortFirstOption = selectSortOptions.getFirstSelectedOption().getText(); // thats a capability of 'Select' Object...
		// System.out.println("First Sort Option is: " + sortFirstOption);
		// make Assertion for comparison with active set above ^...
		Assert.assertEquals(getActiveSortOption().toLowerCase(), sortFirstOption.toLowerCase()); // remove 2ed 2Low & you'll get failure
	}

	// implement a function to validate All Sort Option are as Expected
	public void assertProperSortOptions() { //
	ArrayList<String> listExpected = new ArrayList<>(Arrays.asList( "Name (A to Z)", 
																	"Name (Z to A)", 
																	"Price (low to high)", 
																	"Price (high to low)"));
	ArrayList<String> listActual = new ArrayList<>(Arrays.asList("c", "b", "a"));		
	Collections.sort(listExpected);
	Collections.sort(listActual);
	System.out.println(listExpected.equals(listActual)); // true -> after sorting both, they match !!!
	}
}
