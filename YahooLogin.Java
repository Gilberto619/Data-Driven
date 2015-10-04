package testCases;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import excelIO.ExcelUtils;

public class YahooLogin {
	private static WebDriver driver;
	public static final String excel_Path= System.getProperty("user.dir")+"\\src\\testData\\" ;
	public static final String excel_Workbook= "YahooData.xlsx";
	public static final String excel_Sheet= "Hoja1" ;
	public static final String Yahoo_Url= "https://login.yahoo.com" ;
	
	//Method used to create log info
	public static void LogInfo(String message){
		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS").format(Calendar.getInstance().getTime()).toString() + " INFO [lOG] " + message);
	}
	
	//Method used to create log error
	public static void LogError(String message){
		System.err.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS").format(Calendar.getInstance().getTime()).toString() + " INFO [Error] " + message);
	}
	
	//Method to identify and Highlight elements
	public static void fnHighlightMe(WebElement element) throws InterruptedException{
		//Creating JavaScriptExecuter Interface
		JavascriptExecutor js = (JavascriptExecutor)driver;
		for (int iCnt = 0; iCnt < 3; iCnt++) {
			//Execute javascript
			js.executeScript("arguments[0].setAttribute('style','border: groove 4px yellow')", element);
			Thread.sleep(1000);
			js.executeScript("arguments[0].setAttribute('style','border:')", element);
		}
	}
	
	//Move mouse over an specified element
	public static void moveToElement(WebElement object){
		try{
			LogInfo("Moving mouse to Element");
			Actions builder2 = new Actions(driver);
			Action mouseOver = builder2
					.moveToElement(object)
					.build();
			mouseOver.perform();  
		}catch(Exception e){
			LogError("Not able to Move to Element --- " + e.getMessage());
		}
	}
		
	//Method used for login credentials
	public void login(String sUserName, String sPassword) throws Exception{
		WebElement userName = driver.findElement(By.name("username"));
		WebElement password = driver.findElement(By.name("passwd"));
		WebElement btnLogin = driver.findElement(By.name("signin"));
		
		// Enter the Username in the UserName field	
		userName.clear();
		fnHighlightMe(userName);
		userName.sendKeys(sUserName);
		LogInfo("typing the user: "+sUserName);
					
		// Enter the Password in the Password field
		password.clear();
		fnHighlightMe(password);
		password.sendKeys(sPassword);
		LogInfo("typing the password: "+sPassword);
					
		// Click on Login button
		fnHighlightMe(btnLogin);
		btnLogin.click();
		LogInfo("Clicking on Login button");
					
		// Wait for Some time
		Thread.sleep(3000);
	}
	
	//Opening Firefox Browser and maximize the window
	@BeforeClass
	public void setUp(){
		try{
			driver = new FirefoxDriver();
			driver.manage().window().maximize();
			LogInfo("Firefox browser is open");
			// Navigate to Yahoo Login page
			driver.navigate().to(Yahoo_Url);
			LogInfo("Opening Yahoo login page");
		}catch(Exception e){
			LogError("Unable to open the browser and navigate: "+e.getMessage());
		}
		
	}
	
	//Closing Browser
	@AfterClass
	public void tearDown(){
		try{
			driver.quit();
			LogInfo("############################### End Test Case #############################");
		}catch(Exception e){
			LogError("Unable to close the browser: "+e.getMessage());
		}
	}
	
	//Method to read Excel file and use data into Test
	@DataProvider(name="TestData")
	public Object[][] Authentication() throws Exception {
		Object[][] testObjArray = ExcelUtils.getTableArray(excel_Path+excel_Workbook,excel_Sheet);
		return (testObjArray);
	}
	
	//Test using invalid credentials
	@Test(dataProvider="TestData", priority = 0)
	public void invalidLogin(String sUserName, String sPassword, String sExpected)
			throws Exception {
		LogInfo("################################## Invalid Login ##################################");
		
		WebElement lblInvalid = driver.findElement(By.xpath(".//*[@id='mbr-login-error']"));
		fnHighlightMe(lblInvalid);
		
		WebElement lblPassError = driver.findElement(By.xpath(".//*[@id='mbr-login-error']"));
		fnHighlightMe(lblPassError);
		
		try{
			//Login into Yahoo
			login(sUserName, sPassword);
			
			if (!(sUserName.isEmpty() && sPassword.isEmpty())) {
				String InvalidCred = lblInvalid.getText();
				if(InvalidCred.equals(sExpected)){
					LogInfo(InvalidCred+" is equals to: "+sExpected);
				}else{
					LogError(InvalidCred+" isn't equals to: "+sExpected);
				}		
			}else if (sUserName.isEmpty() && !sPassword.isEmpty()) {
				String InvalidCred = lblInvalid.getText();
				if(InvalidCred.equals(sExpected)){
					LogInfo(InvalidCred+" is equals to: "+sExpected);
				}else{
					LogError(InvalidCred+" isn't equals to: "+sExpected);
				}
			}else if (!sUserName.isEmpty() && sPassword.isEmpty()) {
				String InvalidPass = lblPassError.getText();
				if(InvalidPass.equals(sExpected)){
					LogInfo(InvalidPass+" is equals to: "+sExpected);
				}else{
					LogError(InvalidPass+" isn't equals to: "+sExpected);
				}
			}else if (sUserName.isEmpty() && sPassword.isEmpty()) {
				String InvalidCred = lblInvalid.getText();
				if(InvalidCred.equals(sExpected)){
					LogInfo(InvalidCred+" is equals to: "+sExpected);
				}else{
					LogError(InvalidCred+" isn't equals to: "+sExpected);
				}
			}
		}catch(Exception e){
			LogError(e.getMessage());
		 }
	}

	//Test using valid credentials
	@Test(priority = 1)
	public void validLoginOn()throws Exception {
		LogInfo("################################## Valid Login ##################################");
		try{
			String sUserName = "selenuimautomation@yahoo.com";
			String sPassword = "Open@123";
			
			login(sUserName, sPassword);

			WebElement lnkUser = driver.findElement(By.xpath(".//*[@id='yui_3_10_3_1_1375219693637_127']/b"));
			fnHighlightMe(lnkUser);
			moveToElement(lnkUser);
							
			//Move and click on Sign out button
			WebElement btnLogOut = driver.findElement(By.xpath(".//*[@id='yucs-signout']"));
			WebDriverWait waitVar = new WebDriverWait(driver,10);
			
			waitVar.until(ExpectedConditions.presenceOfElementLocated(By.xpath(".//*[@id='yucs-signout']")));
			fnHighlightMe(btnLogOut);
			moveToElement(btnLogOut);
							
			// Wait for Some time
			Thread.sleep(3000);
			
		}catch(Exception e){
			LogError(e.getMessage());
		}
	}
}
