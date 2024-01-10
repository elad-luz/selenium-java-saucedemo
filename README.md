# selenium-java-saucedemo

This Project is using Selenium with Java, to perform UI Test Automation on SauceDemo.com website:

1. Project demonstrates My ability to code UI Automation Test-Scenarios on web-pages, using Selenium with Java.
2. I follow the Page Object Model (POM) Architecture - including:
    - Base Page
    - Page Classes
    - Data Source files
    - Test Cases
        - `<ROOT>\all-info-of-project\SaucedemoTestPlan.xlsx` - This is the Test-Plan (written in Excel file - currently, only partly covered).
        

## Overview

The primary goal of this project is to perform a DEMO on Test-Capability, while testing: Login to SauceDemo & Add 2 Products to the Cart scenario - the relevant test is located in: `sd-4-DemoTests.spec.ts`.

## Project Structure

- `<ROOT>\src\test\java` : This directory contains the related test packages:
	- `\saucedemo\pageobjects` : This directory contains the BasePage (for code-efficiency) & Pages-classes representing different pages of website.
	- `\saucedemo\testcases` : This directory contains the Tests-classes, for Selenium UI test-scenarios (including a BaseTest for code-efficiency).
	- `\saucedemo\utilities` : This directory contains the varied utilities for Running the Tests properly.
- `<ROOT>\src\test\resources\saucedemo` : This directory contains config-params & data-sources, to be used during tests.

##  Running Tests Locally

Installation, Setup and Run Test (after preparing it properly):

1. Pre-Condition - having Visual-Studio-Code (or any other alternative equivalent), Java -&- Maven.
2. Install relevant dependencies: java etc.
3. Clone this GIT repository to your local machine.
4. To run the tests locally (e.g. using VCS):
    - Select any Test_Name.java (under: `<ROOT>\src\test\java\saucedemo\testcases`) -&- RightClick the Run Option (next to Tests methods) -> See results in Terminal...
5. Running the Selenium tests-cases locally & Generating Allure-Report on the flight, using the 'Command-Line' (CMD):
    - Open CMD & open the project-root-folder.
    - issue maven command  →  `mvn clean test -DsuiteXmlFile=testng.xml` (till finish).
    - For generating the Allure-Test-Results html report, run via same CMD the following command  →  `mvn allure:serve` (will open).

Enjoy...
Elad Luz

