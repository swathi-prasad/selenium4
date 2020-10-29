package com.framework.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.Status;
import com.google.common.io.Files;
import com.sandbox.locators.SandboxMapping;

import io.github.bonigarcia.wdm.WebDriverManager;
import reporting.ExtentReport;

public class Driver {
	static Logger logger = Logger.getLogger(Driver.class);
	public static WebDriver driver;
	public static JavascriptExecutor jsDriver;

	public static Logger APPLICATION_LOGS = null;
	public static Properties CONFIG = null;
	public static Properties PROCONFIG=null;
	public static File f;
	public static ExtentReport test;
	public String foldername;


	public String getCurrentDate() {
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DATE);
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		String datevalue=day+"/"+month+"/"+year;
		datevalue=datevalue.replaceAll("/", "-");
		return datevalue;
	}

	/**
	 * Gets the current time.
	 *
	 * @return the current time
	 */
	public String getCurrentTime() {
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		String timevalue=hour+"-"+minute+"-"+second;
		return timevalue;
	}

	@BeforeSuite
	/**
	 * Configure log4j.properties
	 */
	public void configurationRead() {
		PropertyConfigurator.configure(System.getProperty("user.dir")+File.separator+"src"
				+File.separator+"main"+File.separator+"java"+File.separator+"reporting"
					+File.separator+"log4j.properties");

		String date=getCurrentDate();
		String time=getCurrentTime();
		f=new File(System.getProperty("user.dir")+File.separator+"TestResults"+File.separator+date+"_"+time);
		f.mkdir();
		test = new ExtentReport(f.toString()+File.separator+"ExtentReport.html") ;
		PROCONFIG = initProjectVar(SandboxMapping.Sandbox_propertyFilePath);
		
	}


	@BeforeMethod
	/**
	 * Browser initialization and launch URL
	 * @param m - current method name
	 * @throws Exception
	 */
	public void init(Method m) throws Exception {
		ChromeOptions chromeOptions = new ChromeOptions();
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		test.createTest(m.getName());

		//Browser setup
		String browser=PROCONFIG.getProperty("browser");
		String runHeadless=PROCONFIG.getProperty("runHeadless");

		if(browser.equalsIgnoreCase("Chrome")) {
			WebDriverManager.chromedriver().setup();
			if (runHeadless.equalsIgnoreCase("true"))				
			{
				chromeOptions.setHeadless(true);
			}

			driver=new ChromeDriver(chromeOptions);
		}

		else if(browser.equalsIgnoreCase("Firefox")) {
			WebDriverManager.firefoxdriver().setup();
			if (runHeadless.equalsIgnoreCase("true"))				
				firefoxOptions.setHeadless(true);			
			driver=new FirefoxDriver(firefoxOptions);
		}

		else if(browser.equalsIgnoreCase("Edge")) {
			WebDriverManager.edgedriver().setup();
			driver=new EdgeDriver();
		}

		else if(browser.equalsIgnoreCase("IE")) {
			WebDriverManager.iedriver().setup();
			driver=new InternetExplorerDriver();
		}
		else if(browser.equalsIgnoreCase("Safari")) {
			driver=new SafariDriver();
		}
		else
			System.out.println("Unsupported browser!");		

		jsDriver = (JavascriptExecutor)driver;
		driver.manage().window().maximize();
	}	



	/**
	 * Init projectVariables
	 * @param filePath - path to ProjectVariable.properties
	 * @return
	 */
	public Properties initProjectVar(String filePath) {
		APPLICATION_LOGS = Logger.getLogger("devpinoyLogger");
		PROCONFIG = new Properties();
		try {
			FileInputStream fs = new FileInputStream(System.getProperty("user.dir") + File.separator + filePath);
			PROCONFIG.load(fs);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//	}
		return PROCONFIG;
	}

	/**
	 * logout
	 */
	public void logout() {
		
	}

	//WebDriver methods
	/**
	 * 
	 * @param locator - xpath to element
	 * @param desc
	 * @throws IOException
	 */
	public void click(String locator,String desc) throws IOException {
		try {
			driver.findElement(By.xpath(locator)).click();
			test.writeLog(Status.PASS, "Click - "+desc);

		}

		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("Click failed", error);
			test.writeLog(Status.FAIL, e);
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}		
	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param desc
	 * @throws IOException
	 */
	public void clickAndWait(String locator,String desc) throws IOException {
		try {
			driver.findElement(By.xpath(locator)).click();
			Thread.sleep(1000);
			test.writeLog(Status.PASS, desc);
		}

		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("Click failed", error);
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}		
	}

	public void clickHyperlink(String Linkname,String desc) throws IOException {
		try {
			driver.findElement(By.linkText(Linkname)).click();
			test.writeLog(Status.PASS, desc);
		}

		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("Click hyperlink failed", error);
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}


	}

	/**
	 * 
	 * @param locator  - xpath to element
	 * @param textToType
	 * @param desc
	 * @throws IOException
	 */
	public void type(String locator,String textToType, String desc) throws IOException {
		try {
			driver.findElement(By.xpath(locator)).sendKeys(textToType);
			test.writeLog(Status.PASS, desc);
		}

		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("Type failed", error);
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}		
	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param textToType
	 * @param desc
	 * @throws IOException
	 */
	public void typeAndWait(String locator,String textToType, String desc) throws IOException {
		try {
			driver.findElement(By.xpath(locator)).sendKeys(textToType);
			Thread.sleep(1000);
			test.writeLog(Status.PASS, desc);
		}

		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("Type failed", error);
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}		
	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param desc
	 * @throws IOException
	 */
	public void scrollToView(String locator,String desc) throws IOException {
		try {
			((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath(locator)));
			test.writeLog(Status.PASS, desc);

			Thread.sleep(1000);
		} 
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("scrollToView failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"scrollToView failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}

	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param desc
	 * @return
	 * @throws IOException
	 */
	//getText
	public String getText(String locator,String desc) throws IOException{
		try{
			String value= driver.findElement(By.xpath(locator)).getText();
			test.writeLog(Status.PASS, desc);
			return value;
		} 
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("getText failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"getText failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}
	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param attribute
	 * @param desc
	 * @return attribute
	 * @throws IOException
	 */
	//getAttribute
	public String getAttribute(String locator,String attribute, String desc) throws IOException{
		String value=null;
		try{
			value= driver.findElement(By.xpath(locator)).getAttribute(attribute);
			test.writeLog(Status.PASS, desc);

		} 
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("getAttribute failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"getAttribute failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}
		return value;
	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param desc
	 * @return isDisplayed(true/false)
	 * @throws IOException
	 */
	//isDisplayed
	public boolean isDisplayed(String locator, String desc) throws IOException{
		boolean isDisplayed=false;
		try{
			isDisplayed = driver.findElement(By.xpath(locator)).isDisplayed();
			test.writeLog(Status.PASS, desc);

		} 
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("isDisplayed failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"isDisplayed failed");
			test.writeLog(Status.FAIL, e);
			//throw new WebDriverException(e);
		}
		return isDisplayed;
	}

	/**
	 * 
	 * @param expected
	 * @param actual
	 * @param desc
	 * @throws IOException
	 */
	//Assert
	public void assertEquals(Object expected, Object actual, String desc) throws IOException
	{		
		try{
			Assert.assertEquals(expected, actual);
			test.writeLog(Status.PASS,  "<br>"+desc+"</br>"+" - Expected Value : "+expected+" and Actual Value : "+actual);				
		} 
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("assertEquals failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"assertEquals failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}
	}

	/**
	 * 
	 * @param inputCondition
	 * @param desc
	 * @throws IOException
	 */
	public void assertTrue(Boolean inputCondition, String desc) throws IOException
	{		
		try{
			Assert.assertTrue(inputCondition);
			test.writeLog(Status.PASS, "<br>"+desc+"</br>"+" Expected :  true and Actual : "+inputCondition);			
		} 
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("assertTrue failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"assertTrue failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}
	}

	/**
	 * 
	 * @param inputCondition
	 * @param desc
	 * @throws IOException
	 */
	public void assertFalse(Boolean inputCondition, String desc) throws IOException
	{		
		try{
			Assert.assertFalse(inputCondition);
			test.writeLog(Status.PASS, "<br>"+desc+"</br>"+" Expected :  false and Actual : "+inputCondition);			
		} 
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("assertFalse failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"assertFalse failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}
	}

	/**
	 * 
	 * @param fullText
	 * @param expectedText
	 * @param desc
	 * @throws IOException
	 */
	public void assertContains(String fullText, String expectedText, String desc) throws IOException
	{	
		if (fullText.contains(expectedText))
			test.writeLog(Status.PASS, "<br>"+desc+"</br>"+ "The given text contains the expected text. Full Text : "+fullText+" . Expected Containg Text : "+expectedText);		
		else {
			String error = captureScreenshot();
			test.writeInfo("assertContains failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"assertContains failed");
			test.writeLog(Status.PASS, "<br>"+desc+"</br>"+ "The given text doesn not contain the expected text. Full Text : "+fullText+" . Expected Containg Text : "+expectedText);	
			throw new WebDriverException("assertContains - "+desc+" - Full Text : "+fullText+". Expected Contaning Text : "+expectedText);
		}
	}


	/**
	 * 
	 * @param timeinSeconds
	 * @param locator
	 * @param desc
	 * @throws IOException
	 */
	//waitForElement
	public void waitForElement(int timeinSeconds,String locator, String desc) throws IOException{
		try{
			WebDriverWait wait=new WebDriverWait(driver, timeinSeconds);
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(locator)));
			test.writeLog(Status.PASS, desc);
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("waitForElement failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"waitForElement failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}
	}

	/**
	 * Close current instance of browser
	 * @param desc
	 * @throws IOException
	 */
	//close, goBack,refresh
	public void close(String desc) throws IOException {
		try {
			driver.close();
			test.writeLog(Status.PASS, desc);
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("close failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"close failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}		
	}
	/**
	 * 
	 * @param desc
	 * @throws IOException
	 */
	public void goBack(String desc) throws IOException {
		try {
			driver.navigate().back();
			test.writeLog(Status.PASS, desc);
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("goBack failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"goBack failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}		
	}

	/**
	 * 
	 * @param desc
	 * @throws IOException
	 */
	public void refresh(String desc) throws IOException {
		try {
			driver.navigate().refresh();
			test.writeLog(Status.PASS, desc);
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("refresh failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"refresh failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}		
	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param desc
	 * @throws IOException
	 */
	//clear
	public void clear(String locator, String desc) throws IOException {		
		try{
			driver.findElement(By.xpath(locator)).clear();
			test.writeLog(Status.PASS, desc);
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("clear failed", error);
			//	test.log(LogStatus.INFO,test.addScreenCapture(error)+"clear failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}
	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param textToType
	 * @param desc
	 * @throws IOException
	 */
	//clearAndType
	public void clearAndType(String locator, String textToType, String desc) throws IOException {		
		try{
			clear(locator, desc);
			type(locator, textToType, desc);
			test.writeLog(Status.PASS, desc);
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("clearAndType failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"clearAndType failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}
	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param textToType
	 * @param desc
	 * @throws IOException
	 */
	//clearAndTypeAndWait
	public void clearAndTypeAndWait(String locator, String textToType, String desc) throws IOException {		
		try{
			clear(locator, desc);
			type(locator, textToType, desc);
			Thread.sleep(2000);
			test.writeLog(Status.PASS, desc);
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("clearAndTypeAndWait failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"clearAndTypeAndWait failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}
	}

	/**
	 * 
	 * @param frameLocator - xpath to frame
	 * @param stepDescription
	 * @throws IOException
	 */
	//selectFrame
	public void selectFrame(String frameLocator, String stepDescription) throws IOException {	
		try {
			driver.switchTo().frame(frameLocator);
			test.writeLog(Status.PASS, stepDescription+" for "+frameLocator);
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("clearAndTypeAndWait failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"clearAndTypeAndWait failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}
	}

	/**
	 * 
	 * @param desc
	 */
	//switchToParentFrame
	public void switchToParentFrame(String desc) {
		driver.switchTo().defaultContent();
		test.writeLog(Status.PASS, desc);
	}

	/**
	 * 
	 * @param desc
	 * @return ALert text
	 * @throws IOException
	 */
	//getAlert, accept, cancel
	public String getAlert(String desc) throws IOException {
		try {
			String value = driver.switchTo().alert().getText();
			driver.switchTo().alert().accept();	
			test.writeLog(Status.PASS, desc);
			return value;
		}

		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("getAlert failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"getAlert failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}

	}

	/**
	 * 
	 * @param stepDescription
	 * @throws IOException
	 */
	public void acceptAlert(String stepDescription) throws IOException{	
		try{
			driver.switchTo().alert().accept();
			test.writeLog(Status.PASS, stepDescription);
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("acceptAlert failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"acceptAlert failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}
	}

	/**
	 * 
	 * @param stepDescription
	 * @throws IOException
	 */
	public void cancelAlert(String stepDescription) throws IOException{	
		try{
			driver.switchTo().alert().dismiss();
			test.writeLog(Status.PASS, stepDescription);
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("cancelAlert failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"cancelAlert failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}
	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param stepDescription
	 * @throws IOException
	 */
	//JSclick
	public void jsClick(String locator,String stepDescription) throws IOException{
		try{
			JavascriptExecutor jse = (JavascriptExecutor)driver;            
			jse.executeScript("arguments[0].click();", driver.findElement(By.xpath(locator)));
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("JSclick failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"JSclick failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}

	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param stepDescription
	 * @throws IOException
	 */
	//JSclickAndWait
	public void jsClickAndWait(String locator,String stepDescription) throws IOException{
		try{
			JavascriptExecutor jse = (JavascriptExecutor)driver;            
			jse.executeScript("arguments[0].click();", driver.findElement(By.xpath(locator)));
			Thread.sleep(2000);
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("JSclickAndWait failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"JSclickAndWait failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}

	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param desc
	 * @throws IOException
	 */
	//tabout
	public void tabout(String locator,String desc) throws IOException 
	{
		try {
			driver.findElement(By.xpath(locator)).sendKeys(Keys.TAB);
			test.writeLog(Status.PASS, "Successfully tabbed out of the control "+desc);
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("tabout failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"tabout failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}

	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param desc
	 * @throws IOException
	 */
	//taboutAndWait
	public void taboutAndWait(String locator,String desc) throws IOException 
	{
		try {
			driver.findElement(By.xpath(locator)).sendKeys(Keys.TAB);
			test.writeLog(Status.PASS, "Successfully tabbed out of the control "+desc);
			Thread.sleep(2000);
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("taboutAndWait failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"taboutAndWait failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}

	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param keyName - key on keyboard
	 * @param key
	 * @throws IOException
	 */
	//pressKey
	public void pressKey(String locator,Keys keyName,String...key) throws IOException
	{
		try {		
			driver.findElement(By.xpath(locator)).sendKeys(keyName);	
			test.writeLog(Status.PASS, "Successfully pressed key "+key[0]);
		}

		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("pressKey failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"pressKey failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}
	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param keyNames - keys on keyboard
	 * @param keys
	 * @throws IOException
	 */
	//pressKeys
	public void pressKeys(String locator,String keyNames,String...keys) throws IOException
	{
		try {		
			driver.findElement(By.xpath(locator)).sendKeys(keyNames);	
			test.writeLog(Status.PASS, "Successfully pressed keys "+keys);
		}

		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("pressKeys failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"pressKeys failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}
	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param desc
	 * @return number of elements matching the xpath
	 * @throws IOException
	 */
	//getXpathCount
	public java.lang.Number getXpathCount(String locator,String desc) throws IOException{
		try{
			List<WebElement> xpathCount = driver.findElements(By.xpath(locator));
			java.lang.Number value = xpathCount.size();
			test.writeLog(Status.PASS, desc);
			return value;
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("getXpathCount failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"getXpathCount failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}
	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param desc
	 * @throws IOException
	 */
	//check
	public void check(String locator,String desc) throws IOException {	
		try{
			WebElement element = driver.findElement(By.xpath(locator));
			if (!element.isSelected()) {
				element.click();
			}
			test.writeLog(Status.PASS, desc);
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("check failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"check failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}
	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param desc
	 * @throws IOException
	 */
	//uncheck
	public void uncheck(String locator,String desc) throws IOException {	
		try{
			WebElement element = driver.findElement(By.xpath(locator));
			if (element.isSelected()) {
				element.click();
			}
			test.writeLog(Status.PASS, desc);
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("uncheck failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"uncheck failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}
	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param desc
	 * @return isChecked(true/false)
	 * @throws IOException
	 */
	//isChecked
	public boolean isChecked(String locator,String desc) throws IOException {

		boolean value = false;
		try{
			value=driver.findElement(By.xpath(locator)).isSelected();
			test.writeLog(Status.PASS, desc);
			return value;
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("uncheck failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"uncheck failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}
	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param label
	 * @param desc
	 * @throws IOException
	 */
	//selectByValue, selecyByVisibleText, selectByIndex
	public void selecyByVisibleText(String locator, String label, String desc) throws IOException {
		try{

			Select droplist = new Select(driver.findElement(By.xpath(locator)));

			droplist.selectByVisibleText(label);			
			test.writeLog(Status.PASS, desc+locator+label);
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("selecyByVisibleText failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"selecyByVisibleText failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}
	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param label
	 * @param desc
	 * @throws IOException
	 */
	public void selectByValue(String locator, String label, String desc) throws IOException {
		try{

			Select droplist = new Select(driver.findElement(By.xpath(locator)));
			List<WebElement> we = droplist.getOptions();

			for(WebElement e:we) {
				String text = e.getText();
				System.out.println("text"+text);
				System.out.println("label"+label);
				if(text.equals(label)) {
					System.out.println("correct");
				}
				logger.info(e.getText());
			}

			droplist.selectByValue(label);			
			test.writeLog(Status.PASS, desc+locator+label);
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("selectByValue failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"selectByValue failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}
	}

	/**
	 * 
	 * @param locator
	 * @param index
	 * @param desc
	 * @throws IOException
	 */
	public void selectByIndex(String locator, int index, String desc) throws IOException {
		try{

			Select droplist = new Select(driver.findElement(By.xpath(locator)));
			droplist.selectByIndex(index);			
			test.writeLog(Status.PASS, desc+locator+index);
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("selectByIndex failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"selectByIndex failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}
	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param desc
	 * @throws IOException
	 */
	//moveTo, rightClick, doubleClick
	public void moveToElement(String locator,String desc) throws IOException
	{
		try {
			Actions action = new Actions(driver);
			WebElement we = driver.findElement(By.xpath(locator));
			action.moveToElement(we).build().perform();
			test.writeLog(Status.PASS, "Successfully moved to element "+desc);
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("moveToElement failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"moveToElement failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}

	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param desc
	 * @throws IOException
	 */
	public void rightClick(String locator,String desc) throws IOException
	{
		try {
			Actions action = new Actions(driver);
			WebElement we = driver.findElement(By.xpath(locator));
			action.contextClick(we).build().perform();
			test.writeLog(Status.PASS, "rightClick "+desc);
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("rightClick failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"rightClick failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}

	}

	/**
	 * 
	 * @param locator - xpath to element
	 * @param desc
	 * @throws IOException
	 */
	public void doubleClick(String locator,String desc) throws IOException
	{
		try {
			Actions action = new Actions(driver);
			WebElement we = driver.findElement(By.xpath(locator));
			action.doubleClick(we).build().perform();
			test.writeLog(Status.PASS, "doubleClick "+desc);
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("doubleClick failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"doubleClick failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}

	}

	/**
	 * This method is used to wait for element is not present in DOM
	 * @param StringParam
	 * @throws Exception
	 */
	public  void waitForElementInvisibility(String StringParam) throws Exception {

		try {
			String ObjectName = StringParam;

			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)    
					.withTimeout(Duration.ofMinutes(1))   
					.pollingEvery(Duration.ofSeconds(1))   
					.ignoring(NoSuchElementException.class);	

			logger.info("Waiting for the Element Invisibility "+ ObjectName);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(ObjectName)));
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("Wait for element not invisibility", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"doubleClick failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}

	}

	/**
	 * This method is used to wait for element present in DOM
	 * @param ObjectName
	 * @throws IOException
	 */
	public void waitForElementPresent(String ObjectName) throws IOException {

		try {
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)    
					.withTimeout(Duration.ofMinutes(1))   
					.pollingEvery(Duration.ofSeconds(1))   
					.ignoring(NoSuchElementException.class);	

			logger.info("Waiting for the Element Present "+ ObjectName);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(ObjectName)));
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("doubleClick failed", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"doubleClick failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}	
	}


	public void waitForElementVisibility(String locator) throws Exception {

		try {
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)    
					.withTimeout(Duration.ofMinutes(1))   
					.pollingEvery(Duration.ofSeconds(1))   
					.ignoring(NoSuchElementException.class);	

			logger.info("Waiting for the Element visibility "+ locator);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));
		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("waiting for element visibility", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"doubleClick failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}	
	}

	public void waitForTextPresent(String locator, String text) throws Exception {
		try {
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)    
					.withTimeout(Duration.ofMinutes(1))   
					.pollingEvery(Duration.ofSeconds(1))   
					.ignoring(NoSuchElementException.class);	

			logger.info("Waiting for the Text Present "+ locator);
			wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath(locator),text));

		}
		catch(Exception e) {
			String error = captureScreenshot();
			test.writeInfo("waiting for text to be present", error);
			//test.log(LogStatus.INFO,test.addScreenCapture(error)+"doubleClick failed");
			test.writeLog(Status.FAIL, e);
			throw new WebDriverException(e);
		}
	}


	/**
	 * This method is used to read the data in table and click on the particular row
	 * @param ObjectName
	 * @param Texttomatch
	 * @param Columnnumber
	 */
	//table 'mat-table
	public void tableData(String ObjectName,String Texttomatch,String Columnnumber) {
		List<WebElement> tablerow = driver.findElements(By.xpath(ObjectName));

		for(int i = 1;i<=tablerow.size();i++) {
			String row = ObjectName+"["+i+"]"+"/mat-cell["+Columnnumber+"]";
			if(driver.findElement(By.xpath(row)).getText().equalsIgnoreCase(Texttomatch)) {
				driver.findElement(By.xpath(row)).click();
				break;
			}
		}
	}


	/**
	 * 
	 * @param driver
	 * @return
	 * @throws IOException
	 */
	//captureScreenshotScreenshot
	public String captureScreenshot() throws IOException {
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

		f=new File(f.toString()+File.separator+"Screenshots");
		f.mkdir();

		File Dest = new File(f + File.separator + System.currentTimeMillis()+ ".png");
		String errflpath = Dest.getAbsolutePath();
		Files.copy(scrFile, Dest);
		System.out.println(errflpath);
		return errflpath;
	}

	public int generateRandomNumber(int upperBound) {
		Random rand = new Random();
		int int_random = rand.nextInt(upperBound); 

		System.out.println("Random integer value from 0 to" + (upperBound-1) + " : "+ int_random);

		return int_random;
	}

	public String encryptPwd(String text) throws Exception {
		String key="Bar12345Bar12345";
		Key aesKey=new SecretKeySpec(key.getBytes(),"AES");
		Cipher cipher=Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, aesKey);
		byte[] encrypted=cipher.doFinal(text.getBytes());
		Base64.Encoder encoder=Base64.getEncoder();
		String encryptedString=encoder.encodeToString(encrypted);
		System.out.println(encryptedString);
		return encryptedString;

	}

	public String decryptPwd(String encryptedString) throws Exception{
		String key="Bar12345Bar12345";
		Key aesKey=new SecretKeySpec(key.getBytes(),"AES");
		Cipher cipher=Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, aesKey);
		Base64.Decoder decoder=Base64.getDecoder();
		cipher.init(Cipher.DECRYPT_MODE, aesKey);
		String decrypted=new String(cipher.doFinal(decoder.decode(encryptedString)));
		return decrypted;
	}

	@AfterMethod
	public void closeBrowser() {
		//driver.close();
	}

	@AfterSuite
	public void extentFlush() {
		
		test.flushLog();

		if (driver != null) {
			driver.quit();
		}		
	}
}