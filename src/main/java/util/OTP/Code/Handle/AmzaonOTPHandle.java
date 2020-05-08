package util.OTP.Code.Handle;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Message;

public class AmzaonOTPHandle {

	public static final String ACCOUNT_SID = "AC3ce1c5f16dcd1e0563774a7c065ff7ca";
	public static final String AUTH_TOKEN = "c811245cb5112673e8bd898648317412";

	public static void main(String[] args) {

		WebDriver driver = new ChromeDriver();

		driver.get("https://www.amazon.in");
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		driver.findElement(By.cssSelector("a#nav-link-accountList>span>span")).click();
		driver.findElement(By.linkText("Start here.")).click();

		driver.findElement(By.id("ap_customer_name")).sendKeys("JebrilTestOTP");
		driver.findElement(By.id("auth-country-picker-container")).click();

		driver.findElement(By.xpath("//ul[@role='application']//li/a[contains(text(),'United States +1')]")).click();
		driver.findElement(By.id("ap_phone_number")).sendKeys("2057495862");
		driver.findElement(By.id("ap_password")).sendKeys("TestAutomation@123");
		driver.findElement(By.id("continue")).click();

		//GET THE OTB USING TWILLIOS APIS
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		String smsBody = getMessage();
		System.out.println(smsBody);
		String OTPNumber = 	smsBody.replaceAll("[^-?0-9]+", "");
		System.out.println(OTPNumber);
		driver.findElement(By.id("auth-pv-enter-code")).sendKeys(OTPNumber);
		
	}

	public static String getMessage() {
		return getMessages().filter(m -> m.getDirection().compareTo(Message.Direction.INBOUND) == 0)
				.filter(m -> m.getTo().equals("+12057495862")).map(Message::getBody).findFirst()
				.orElseThrow(IllegalStateException::new);
	}

	private static Stream<Message> getMessages() {
		ResourceSet<Message> messages = Message.reader(ACCOUNT_SID).read();
		return StreamSupport.stream(messages.spliterator(), false);
	}

}
