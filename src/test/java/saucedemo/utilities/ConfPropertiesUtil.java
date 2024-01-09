package saucedemo.utilities;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfPropertiesUtil { // can be used (as BasePage) in many Projects !  --> see implementation in BaseTest - setUp()
// Configuration (residing in src/test/resources/saucedemo/configuration.properties) is being consumed by the help of this Util
// Test1_LoginAuthentication.java - tc03_LoginSuccess() also make use of the util, for getting the proper username and password
	
	//	following method is a 'tool' to read a property value from a given config.prop file) -
	/* 	the 'static' states you don't need to create an object - can actually use it directly:
		example of usage from anywhere in the src -->  Utils.readProperty("url");  -->  or as:
		used in BaseTest -->  driver.get(Utils.readProperty("url"));  <-----------------------
		*/
	public static String readProperty(String key) { // method get String called key = property
		// we want to read the .properties file & get the value, correlative to that key given
		String value=""; // declare & init a String variable for getting the given key's value
		// path defines where the file resides: in this case, a relative path under my project
		// NOTE: 'InputStream' works with a File -> so it needs to be wrapped with Try/Catch !
		try (InputStream input = new FileInputStream("./src/test/resources/saucedemo/configuration.properties")) { // use Maven
			// declare & initialize a Properties object (to host the properties file in memory)
			Properties prop = new Properties();

			// load the properties file to memory (into above created ^ prop object)
			prop.load(input);

			// reading the properties from file (get value of a key):
			// get the property value (also print out) of given key - and, set it to variable!
			value = prop.getProperty(key);
			// System.out.println(prop.getProperty(key));

		} catch (Exception e) { // can use specific exceptions as: FileNotFoundException -or- IOException
			e.printStackTrace();
		}
		return value; // method returns the value of the key from related .properties file ...
	}
	
	/*	you can also add a method that write the property value...
		--> https://mkyong.com/java/java-properties-file-examples/
			-----------------------------------------------------
			
		In this case we don't need an object instance of this util - its not like cat class that can hold several cat instances
		we just need to access it & use it -> so we turn it into static...
		-------
		STATIC:
		static method in Java (or static function) is a method that is part of a class rather than an instance of that class ->
		it defined as a member of an object but is accessible directly from an API object's constructor (from anywhere in src),
		rather than from an object instance created via the constructor.
		Static methods have access to class variables (static variables) without using the class's object (instance).
		Only static data may be accessed by a static method !
	 */
}
