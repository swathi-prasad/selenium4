package reporting;

import java.io.File;
import java.io.IOException;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReport{

	public ExtentSparkReporter htmlreporter;	
	public ExtentReports extent;
	public ExtentTest extenttest;

	public ExtentReport(String filelocation)  {

		this.htmlreporter = new ExtentSparkReporter(filelocation);
		try {
			this.htmlreporter.loadXMLConfig(System.getProperty("user.dir")+File.separator+"src"
					+File.separator+"main"+File.separator+"java"+File.separator+"reporting"
						+File.separator+"extentreportproperties.xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.extent = new ExtentReports();
		this.extent.setSystemInfo("Organization", "Ellucian");
		this.extent.setSystemInfo("Operation System ", System.getProperty("os.name"));
		this.extent.setSystemInfo("OS Version number", System.getProperty("os.version"));
		this.extent.attachReporter(this.htmlreporter);

	}

	public void createTest(String TestName) {
		this.extenttest = this.extent.createTest(TestName);	
	}

	public void createTest(String TestName, String Description) {
		this.extenttest = this.extent.createTest(TestName, Description);
	}

	public void writeLog(Status status, String details) {
		this.extenttest.log(status, details);
	}

	public void flushLog() {
		this.extent.flush();
	}

	public void attachScreenshot(String imagePath) throws IOException {
		this.extenttest.addScreenCaptureFromPath(imagePath);
	}

	public void writeInfo(String details) {
		this.extenttest.info(details);		
	}

	public void writeInfo(String details, String ImageFileLocation) throws IOException {
		this.extenttest.info(details, MediaEntityBuilder.createScreenCaptureFromPath(ImageFileLocation).build());		
	}

	public void writeLog(Status status,String details, String ImageFilelocation) throws IOException {
		this.extenttest.log(status, details, MediaEntityBuilder.createScreenCaptureFromPath(ImageFilelocation).build());
	}

	public void writeLog(Status status, Throwable t) {
		this.extenttest.log(status,t);		
	}

	public void category(String category) {
		this.extenttest.assignCategory(category);
	}

	public void author(String author) {
		this.extenttest.assignAuthor(author);		
	}
}