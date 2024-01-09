package saucedemo.testcases;

import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;
import saucedemo.pageobjects.Common;
import saucedemo.pageobjects.LoginPage;
import saucedemo.pageobjects.ProductsInventoryPage;
import saucedemo.utilities.ConfPropertiesUtil;

public class Test2_CommonMenu  extends BaseTest{ // TestPlan Section 04 Test-Cases

	@Test (priority = 1, description= "01. Smoke-Test = Menu (Common Component) is Accessible & Contains the expected UI-elements")
	public void tc01_Menu_ElementsExist() throws InterruptedException {
		LoginPage lp = new LoginPage(driver);
		lp.login(ConfPropertiesUtil.readProperty("user"), ConfPropertiesUtil.readProperty("pass")); // Success LI credentials properties
		ProductsInventoryPage pp = new ProductsInventoryPage(driver);
		pp.isProductsInventoryPageTitle(); // validation for redirection to the Products-Inventory page, is indicative for a Successful login
		Common cm = new Common(driver);
		assertTrue(cm.isProperClosedMenuUI(), "Closed Menu UI don't displayed all expected elements properly !");
		cm.openMenu();
		assertTrue(cm.isProperOpenedMenuUI(), "Opened Menu UI don't displayed all expected elements properly !");
	}

	@Test (priority = 2, description= "02. Functional-Test = Close Menu")
	public void tc02_Menu_Close() throws InterruptedException { // note: we are already logged in from prev case...
		LoginPage lp = new LoginPage(driver);
		lp.navigateRefresh(); // to refresh the page & get it default state!
		Common cm = new Common(driver);
		cm.openMenu();
		assertTrue(cm.isProperOpenedMenuUI(), "Opened Menu UI don't displayed all expected elements properly !");
		cm.closeMenu();
		assertTrue(cm.isProperClosedMenuUI(), "Closed Menu UI don't displayed all expected elements properly !");
	}

	@Test (priority = 3, description= "03. Functional-Test = Menu-link About")
	public void tc03_Menu_About() throws InterruptedException { // note: we are already logged in from prev case...
		LoginPage lp = new LoginPage(driver);
		lp.navigateRefresh();
		Common cm = new Common(driver);
		cm.openMenu();
		cm.clickOnMenuLnk("about");
		cm.assertCurrentUrl("saucelabs.com"); // using BasePage asserting method + part of URL -> validating opened proper destination !
		cm.navigateBack(); // also making use of a wrapping method from the BasePage! (backing so we can proceed with testing this page)
		cm.assertCurrentUrl("saucedemo.com/inventory.html"); // validating returned to proper page (continue on to next test from here)!
	}

	@Test (priority = 4, description= "04. Functional-Test = Menu-link All-Items")
	public void tc04_Menu_Inventory() throws InterruptedException { // note: we are already logged in from prev case...
		LoginPage lp = new LoginPage(driver);
		lp.navigateRefresh();
		ProductsInventoryPage pip = new ProductsInventoryPage(driver);
		pip.clickSubElementOfProduct("name", "Sauce Labs Backpack");  // opens product info page [- using  complex page method -]
		pip.assertCurrentUrl("saucedemo.com/inventory-item.html"); // verify you are on product items' info page
		Common cm = new Common(driver);
		cm.openMenu(); // opening menu from the new context page
		cm.clickOnMenuLnk("items"); // getting back to products inventory (list) page from Menu
		cm.assertCurrentUrl("saucedemo.com/inventory.html"); // verify you are back at products inventory (list) page
	}

	@Test (priority = 5, description= "05. Functional-Test = Menu-link Reset App, Clears Carts Items")
	public void tc05_Menu_ResetAppClearsCart() throws InterruptedException { // note: we are already logged in from prev case...
		LoginPage lp = new LoginPage(driver);
		lp.navigateRefresh();
		Common cm = new Common(driver);
		assertTrue(cm.isProperEmptyCartUI(), "Empty Cart UI don't displayed all expected elements properly !");
		assertTrue((cm.getNumCartItems() == 0), "Indication of Cart Items nor as expected = 0 !");
		ProductsInventoryPage pip = new ProductsInventoryPage(driver);
		pip.clickSubElementOfProduct("button", "Sauce Labs Backpack"); // add product to cart -> cart should indicate 1 product added !!
		assertTrue(cm.isProperFullCartUI(), "Full Cart UI don't displayed all expected elements properly !");
		assertTrue((cm.getNumCartItems() == 1), "Indication of Cart Items nor as expected = 1 !");
		cm.openMenu(); // opening menu from the new context page
		cm.clickOnMenuLnk("reset"); // clearing Cart from all added Items
		assertTrue(cm.isProperEmptyCartUI(), "Empty Cart UI don't displayed all expected elements properly !");
		assertTrue((cm.getNumCartItems() == 0), "Indication of Cart Items nor as expected = 0 !");
		pip.clickSubElementOfProduct("button", "Sauce Labs Onesie"); // add a different product to cart
		cm.openCart(); // click on icon to open the cart-page
		cm.assertCurrentUrl("saucedemo.com/cart.html"); // verify you are at the cart page
		assertTrue(cm.isProperFullCartUI(), "Full Cart UI don't displayed all expected elements properly !");
		assertTrue((cm.getNumCartItems() == 1), "Indication of Cart Items nor as expected = 1 !");
		cm.openMenu(); // opening menu from the new context page
		cm.clickOnMenuLnk("reset"); // clearing Cart from all added Items
		assertTrue(cm.isProperEmptyCartUI(), "Empty Cart UI don't displayed all expected elements properly !");
		assertTrue((cm.getNumCartItems() == 0), "Indication of Cart Items nor as expected = 0 !");
	}
	
	@Test (priority = 6, description= "06. Functional-Test = Menu-link Logout")
	public void tc06_Menu_Logout() throws InterruptedException { // note: we are already logged in from prev case...
		LoginPage lp = new LoginPage(driver);
		lp.navigateRefresh();
		Common cm = new Common(driver);
		cm.openMenu();
		cm.clickOnMenuLnk("logout"); // getting back to products inventory (list) page from Menu
		lp.assertCurrentUrl("saucedemo.com"); // verify you are back at login page
		assertTrue(lp.isProperUI(), "UI don't displayed all expected elements properly !"); // isProperUI() checks that your Logged-out!
	}
}
