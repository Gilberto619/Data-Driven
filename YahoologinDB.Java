package testCases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class YahooLoginDB {
	static WebDriver driver;
	public static final String db_Path= System.getProperty("user.dir")+"\\src\\testData\\" ;
	public static final String db_Name= "YahooData.accdb";
	// Connection object
	static Connection con = null;
	// Statement object
	private static Statement smt;
	// Constant for Database URL
	public static String dblocation = db_Path+db_Name;

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
	 public void setUp() throws Exception {
		 try{
				/*
				 * This will connect with Database , getConnection taking three
				 * argument first argument Test_Oracle is the dsn which you can
				 * change as per your system,
				 */
				con = DriverManager.getConnection("jdbc:ucanaccess://" + dblocation);
				LogInfo("Connection created");

				// This will create statement
				smt = con.createStatement();
				LogInfo("Statement created");
				
				driver = new FirefoxDriver();
				driver.manage().window().maximize();
				LogInfo("Firefox browser is open");
				// Navigate to Yahoo Login page
				driver.navigate().to(Yahoo_Url);
				LogInfo("Opening Yahoo login page");
		 }
		 catch (Exception e)
		 {
			 LogError("Error is: " + e.getMessage());
		 }
	 }
	 
	//Closing Browser
	@AfterClass//@AfterTest
	 public void tearDown() throws Exception {
		 // Close DB connection
		 if (con != null) {
			 con.close();
			 LogInfo("Connection closed");
			 driver.quit();
			 LogInfo("############################### End Test Case #############################");
		 }
	 }
	
	@Test
	public void invalidLoginDB(){

		try {
			/*
			 *  Now execute the query, here Yahoo_Users is the table which I have
			 *  created in DB
			 */
			ResultSet rs = smt.executeQuery("Select * From Yahoo_Users");
			LogInfo("Query Executed");

			// Iterate the result set now
			while (rs.next()) {

				String uname = rs.getString("UserName");
				String pass = rs.getString("Password");
				String sExpected = rs.getString("Expected");

				LogInfo("################################## Invalid Login ##################################");
				
				WebElement lblInvalid = driver.findElement(By.xpath(".//*[@id='mbr-login-error']"));
				fnHighlightMe(lblInvalid);
				
				WebElement lblPassError = driver.findElement(By.xpath(".//*[@id='mbr-login-error']"));
				fnHighlightMe(lblPassError);

				//Login into Yahoo
				login(uname, pass);
					
				if (uname != null && pass != null) {
					String InvalidCred = lblInvalid.getText();
					if(InvalidCred.equals(sExpected.toString())){
						LogInfo(InvalidCred+" is equals to: "+sExpected);
					}else{
						LogError(InvalidCred+" isn't equals to: "+sExpected);
					}		
				}else if (uname == null && pass != null) {
					String InvalidCred = lblInvalid.getText();
					if(InvalidCred.equals(sExpected.toString())){
						LogInfo(InvalidCred+" is equals to: "+sExpected);
					}else{
						LogError(InvalidCred+" isn't equals to: "+sExpected);
					}
				}else if (uname !=null && pass == null) {
					String InvalidPass = lblPassError.getText();
					if(InvalidPass.equals(sExpected.toString())){
						LogInfo(InvalidPass+" is equals to: "+sExpected);
					}else{
						LogError(InvalidPass+" isn't equals to: "+sExpected);
					}
				}else if (uname == null && pass == null) {
					String InvalidCred = lblInvalid.getText();
					if(InvalidCred.equals(sExpected.toString())){
						LogInfo(InvalidCred+" is equals to: "+sExpected);
					}else{
						LogError(InvalidCred+" isn't equals to: "+sExpected);
					}
				}
			}
		} catch (Exception e) {
			LogError("Error is: " + e.getMessage());
		}
	}
	
}
