package rpa;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import objects.ExcelOperations;
import objects.SeleniumUtils;
import objects.Utility;
import utilities.ExcelReader;

public class LIMSABCNew {
	Logger logger = LogManager.getLogger(LIMSABCNew.class);

	String projDirPath, status, claimNo ,claimNumAvaility, DOB ,serviceDate ,firstName, lastName,memberID,ecwStatus,DOS, claimStatus,dateofbirth, npivalue, charges,currency, error, originalTab, checkNum;

	SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yy");

	SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
	
	public static ExcelReader excel, excel1; 
	public static String sheetName = "Sheet1";
	int rowNum = 1, commonScreenElements;
	int renameRowNum = 1;
	boolean skipFlag =false, screen;
	;
	WebDriver driver;
String CPT2= ", 80307";
	//JavascriptExecutor js;
	SeleniumUtils sel;
	Utility utility;

	ExcelOperations excelFile;
	String currentHandle;
	static String excelFileName="Toxicology Coding Approval LimsABC.xlsx",excelFileName1= "Toxicology Drug classes. Updated (1).xlsx", accessionNum,renameStatus, CPT="";

	@BeforeTest
	public void preRec() throws InterruptedException, SAXException, IOException, ParserConfigurationException {

		sel = new SeleniumUtils(projDirPath);

		driver = sel.getDriver();
		//excelFileName= "practice file toxy new.xlsx";

		utility = new Utility();


		String url = "https://vhl.limsabc.com/";
			driver.get(url);
		logger.info("Open url: " + url);

			driver.findElement(By.xpath("//div[text()='User name']/preceding-sibling::input")).sendKeys("Soran.Baker");
	    	logger.info("Username Entered as: "+ "Soran.Baker");
	    	driver.findElement(By.xpath("//div[text()='Password']/preceding-sibling::input")).sendKeys("Welcome$$2024");
	    	logger.info("Password Entered");
	    	driver.findElement(By.xpath("//div[text()='Sign in']")).click();
	    	logger.info("Login button clicked");
	    	Thread.sleep(3000);
	    	sel.pauseClick(driver.findElement(By.xpath("//div[text()='Portal']")), 10);
	    	driver.findElement(By.xpath("//div[text()='Portal']")).click();
	    	logger.info("Clicked on Portal");
	    	Thread.sleep(3000);
	    	sel.pauseClick(driver.findElement(By.xpath("//div[text()='Display requisitions for the last']/preceding-sibling::input")), 10);
	    	driver.findElement(By.xpath("//div[text()='Display requisitions for the last']/preceding-sibling::input")).clear();
	    	driver.findElement(By.xpath("//div[text()='Display requisitions for the last']/preceding-sibling::input")).sendKeys("Greater than 60 days"+Keys.ENTER);
	    	logger.info("Greater than 60 days selected");
	    	
	    	((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(By.xpath("//span[text()='All']")));
	    	
	    	currentHandle	= driver.getWindowHandle();
	    	//driver.findElement(By.xpath("//span[text()='All']")).click();
	    	logger.info("Clicked on All");
	    
	    	
		
	}

	@Test(dataProvider= "getData",priority=1)
	public  void pdfDownload(Hashtable<String,String> data) throws IOException, InterruptedException {
		renameRowNum++;
	 	renameStatus = data.get("Rename Status");
	 	accessionNum= data.get("Accession No");
    	if(renameStatus.isBlank()|| renameStatus.isBlank()) {	
    		   		Thread.sleep(3000);
        	
   		driver.findElement(By.xpath("//input[@class='search_field span']")).clear();
    	driver.findElement(By.xpath("//input[@class='search_field span']")).sendKeys(accessionNum+Keys.ENTER);
    	logger.info("Accession number entered "+accessionNum);
    Thread.sleep(5000);
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(By.xpath("//div[@title='View Report']")));
logger.info("View report clicked");
	
    	
    	Thread.sleep(5000);
    	Set<String> handles=driver.getWindowHandles();
    	for(String actual: handles) {
    	if(!actual.equalsIgnoreCase(currentHandle)) {
    	//Switch to the opened tab
    	driver.switchTo().window(actual);
    	driver.close();
    	driver.switchTo().window(currentHandle);
    	}}
    	excel.setCellData(sheetName, "Rename Status", renameRowNum, "Pass");
        
   /*
    	
    	File lastModifiedFile = getLastModified(System.getProperty("user.dir")+"\\DownloadedFiles");
    	if (lastModifiedFile != null) {
            String newFileName = accessionNum+".pdf"; // Provide the new file name
            File renamedFile = new File("C:\\Users\\jmartin\\eclipse-workspace\\TestSequence\\DownloadedFiles", newFileName);

            if (lastModifiedFile.renameTo(renamedFile)) {
                System.out.println("File renamed successfully: " + renamedFile.getName());
                excel.setCellData(sheetName, "Rename Status", renameRowNum, "Success");
            } else {
                System.out.println("Failed to rename file.");
                excel.setCellData(sheetName, "Rename Status", renameRowNum, "Failure");
            }
        } else {
            System.out.println("No files found in the directory.");
            excel.setCellData(sheetName, "Rename Status", renameRowNum, "No Files Found");
        }
    	
    	
    	*/
    	}	
	}
	
	@Test(dataProvider= "getData", priority=2)
	public  void pdfParse(Hashtable<String,String> data) throws IOException {
		screen=false;
		rowNum++;
    	accessionNum = data.get("Accession No");
status= data.get("Status");
boolean startProcessing=false;
int countTest=0;
boolean ScreenPos = false;
boolean medPrescribed = false;
int countPos;
int count=0;
Set matchSet = new HashSet();
List<Integer> mycounts = new ArrayList();
Set myset = new HashSet();
Set myset1 = new HashSet();
Set myset2 = new HashSet();
Set mysetScreen = new HashSet();
List<String> screenArr = new ArrayList<String>();
		if(status.isBlank()|| status.isEmpty()) {
		String line1 = "";

		
		 String directoryPath = System.getProperty("user.dir")+"\\DownloadedFiles";
		 String pathofFile = null;
	        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directoryPath), "*.pdf");
	        for (Path path : directoryStream) {
	            if (path.getFileName().toString().matches(".*"+accessionNum+".*")) {
	                System.out.println("Found PDF: " + path);
	                pathofFile= path.toString();
	                break;
	                // Process the found PDF file
	            }
	            
	        }
		
		
		// Provide the path to your PDF file
		String pdfFilePath = pathofFile;
		excel1 = new ExcelReader(System.getProperty("user.dir")+"\\"+excelFileName1);
		// Load the PDF document
		PDDocument document = PDDocument.load(new File(pdfFilePath));

		// Create PDFTextStripper class
		PDFTextStripper pdfTextStripper = new PDFTextStripper();


		String text = pdfTextStripper.getText(document);
if(text.contains("Full Screen") || text.contains("ETG Screen")|| text.contains("Screen") || text.contains("screen")) {
	screen=true;
}


		
		
		
		
		// Process each line in the extracted text

		String[] lines = text.split("\\r?\\n");

		// Iterate through the lines and print each line and the next line
		for (int i = 0; i < lines.length - 1; i++) {
			String currentLine = lines[i];
			String nextLine = lines[i + 1];

			if(currentLine.contains("Test") && currentLine.contains("Result") && !nextLine.contains("Full Screen")&& !currentLine.contains("Range")) {
				//  System.out.println("Line: " + currentLine);
				//  System.out.println("Next Line: " + nextLine);
				startProcessing = true;
			}
			if(currentLine.contains("Screen")) {
				
				for(int j=2; j<excel1.getRowCount(sheetName); j++) {

					line1= excel1.getCellData(sheetName, 2, j);
					boolean isSubstringPresent = isWordPresent(line1, currentLine.trim());

					// System.out.println(isSubstringPresent);
					if(isSubstringPresent==true) {
						// System.out.println(i);
					//	System.out.println(line1+"----------"+currentLine);
						mysetScreen.add(j);
						
					}
				}
				
				
				
			}
			
if(currentLine.contains("Screen") && currentLine.contains("Positive")) {
	ScreenPos=true;
}

if(currentLine.contains("Prescribed Medication")) {
	String[] medArr = currentLine.trim().split("Prescribed Medication(s)");
	if(!(currentLine.contains("No Medication") ||currentLine.contains("None Provided")||currentLine.contains("None Prescribed"))) {
	if(medArr.length>=1) {
		medPrescribed=true;
	}
	
	 medArr = currentLine.trim().split("Prescribed");
	if(medArr.length>=2) {
		medPrescribed=true;
	}
	}
}


			/*	for (String line : text.split("\\r?\\n")) {
				if (line.contains("Test")) {
					// Found the keyword "Test," start processing from this line onward
					countTest++;
					//startProcessing = true;
				}
				if(countTest==2) {
					startProcessing = true;
				}
			 */
			
			if (startProcessing) {
				int rows= excel1.getRowCount(sheetName);

				// Process or print the line (you can modify this part according to your needs)
				for(int j=2; j<excel1.getRowCount(sheetName); j++) {

					line1= excel1.getCellData(sheetName, 2, j);
					boolean isSubstringPresent = isWordPresent(line1, currentLine.trim());

					// System.out.println(isSubstringPresent);
					if(isSubstringPresent==true) {
						// System.out.println(i);
					//	System.out.println(line1+"----------"+currentLine);
						myset.add(j);
						myset1.add(currentLine);
					}
					
					if(isSubstringPresent==true && !currentLine.contains("Screen") &&(currentLine.contains("Positive")||currentLine.contains("POS"))) {
						// System.out.println(i);
					//	System.out.println(line1+"----------"+currentLine);
						myset2.add(j);
						
					}
					
				/*	if(!currentLine.contains("Screen") && (currentLine.contains("Positive") || currentLine.contains("Negative"))){
						for(int k =0; k<screenArr.size(); k++ ) {
						//	System.out.println(screenArr.get(k));
							if(currentLine.split(" ")[0].equals((screenArr.get(k)))){
								matchSet.add(screenArr.get(k));
								System.out.println(currentLine +"---------"+screenArr.get(k));
							}
						}
						
						
					} */
					
					
				}
			}



		}
		
	System.out.println("Common is "+CollectionUtils.intersection(myset,mysetScreen).size());
	commonScreenElements = CollectionUtils.intersection(myset,mysetScreen).size();
		if(screen==true) {
			System.out.println("CPT is 80307");
			CPT= "80307";
		}
		if(screen==true && (myset2.size()>0 &&myset2.size()<=7)) {
			System.out.println("CPT is 80307, G0480");
			CPT= "80307, G0480";
		}
		if(screen==true && (myset2.size()>7 &&myset2.size()<=14)) {
			System.out.println("CPT is 80307, G0481");
			CPT= "80307, G0481";
		}
		if(screen==true && (myset2.size()>14 &&myset2.size()<=21)) {
			System.out.println("CPT is 80307, G0482");
			CPT= "80307, G0482";
		}
		
		if(screen==true && myset2.size()>21 ) {
			System.out.println("CPT is 80307, G0483");
			CPT= "80307, G0483";
		}
		
		if(screen==false && (myset2.size()>0 &&myset2.size()<=7)) {
			System.out.println("CPT is  G0480");
			CPT= "G0480";
			
		}
		if(screen==false && (myset2.size()>7 &&myset2.size()<=14)) {
			System.out.println("CPT is  G0481");
			CPT= "G0481";
		}
		if(screen==false && (myset2.size()>14 &&myset2.size()<=21)) {
			System.out.println("CPT is  G0482");
			CPT= "G0482";
		}
		
		if(screen==false && myset2.size()>21 ) {
			System.out.println("CPT is  G0483");
			CPT= "G0483";
		}
		
		
		if(screen==true && myset2.size()==0 &&ScreenPos==false &&medPrescribed==true) {
			System.out.println("CPT is 80307, G0480 (Prescribed Med)");
			CPT= "CPT is 80307, G0480 (Prescribed Med)";
		}
		
		if(screen==true && myset2.size()==0 &&ScreenPos==false &&medPrescribed==false) {
			if(commonScreenElements>=4) {
				System.out.println("CPT is 80307, G0480 (Not Prescribed Med)");
				CPT= "CPT is 80307, G0480 (Not Prescribed Med)";	
			}
			
		else {
			System.out.println("CPT is 80307, case 7");
			CPT= "CPT is 80307, case 7";
			System.out.println(commonScreenElements);
			
				System.out.println(commonScreenElements);
			
		}}
		
		if(screen==true && myset2.size()==0 &&ScreenPos==true &&medPrescribed==false) {
		System.out.println("CPT is 80307, G0480 case 8");
		CPT= "CPT is 80307, G0480, case 8";
		}
		
		if(screen==false && (medPrescribed==false &&myset2.size()==0 )) {
		
			if(myset.size()>=1 && myset.size()<=7){
				CPT="G0480";
			}
			else if(myset.size()>=8 && myset.size()<=14) {
				CPT="G0481";
			}
			else if(myset.size()>=15 && myset.size()<=21) {
				CPT="G0482";
			}
			else if(myset.size()>21) {
				CPT="G0483";
			}
		
		}
		if(screen==false && (medPrescribed==true && myset2.size()==0 )) {
			System.out.println("CPT is G0480, last case");
			CPT="G0480";
		}
		
		
		
		
		System.out.println(myset.size());
		System.out.println(myset2.size() + "is positive count");
		System.out.println(myset2);
		System.out.println(myset1);
//		System.out.println(myset1.size());
		System.out.println(mysetScreen.size());
		System.out.println(mysetScreen);
		
		excel.setCellData(sheetName, "Status", rowNum, "Pass");
		excel.setCellData(sheetName, "BOT Coding", rowNum, CPT);
		excel.setCellData(sheetName, "Positive Classes Count", rowNum, String.valueOf((myset2.size())));
		excel.setCellData(sheetName, "Total Classes", rowNum, String.valueOf((myset.size())));
	/*
		if(myset.size()>=1 && myset.size()<=7){
			CPT="G0480";
		}
		else if(myset.size()>=8 && myset.size()<=14) {
			CPT="G0481";
		}
		else if(myset.size()>=15 && myset.size()<=21) {
			CPT="G0482";
		}
		else if(myset.size()>21) {
			CPT="G0483";
		}
		if(screen==true) {
			CPT= CPT.toString()+ CPT2.toString();
		}
		
		
		*/
		}
		//
		
		

		/*

		        for (String line : text.split("\n")) {
		     if(line.contains("Screen")) {
		    	 continue;
		     }
		        	for(int i=1; i<excel.getRowCount(sheetName); i++) {

		        		 line1= excel.getCellData(sheetName, 2, i);
		        		 boolean isSubstringPresent = isSubstringPresent(line1, line);
		        		// System.out.println(isSubstringPresent);
		        		 if(isSubstringPresent==true) {
		        			// System.out.println(i);
		        			 myset.add(i);
		        		 }
		        	}


		 */   
		}
	
	//    System.out.println(myset.size());

	//System.out.println(count);

	public static boolean containsIgnoreCase(String[] array, String target) {
		for (String element : array) {
			if (element.equalsIgnoreCase(target)) {
				return true;
			}
		}
		return false;
	}
	public static boolean isWordPresent(String str1, String str2) {
		String[] words1 = str1.split(",");  // Split str1 into words
		String[] words2 = str2.split("\\s+");;  // Split str2 into words
		String[] words3 = str2.split("(?<=\\D)(?=\\d)");
		String result = getSubstringUpToSecondWhitespace(str2);
		for (String word : words1) {
			if (containsIgnoreCase(words2, word.replace(",", "").trim())) {
				return true;
			}
			else if(result.toLowerCase().trim().equalsIgnoreCase(word.toLowerCase().trim())) {
				return true;
			}
			for (String newword : words3) {
			 if(newword.replaceAll("[0-9]", "").equalsIgnoreCase(word.replace(",", "").trim()) ) {
				return true;
			}
			 }


		}
		return false;
	}


	// Method to check if any substring of str1 is present in str2
	public static boolean isSubstringPresent(String str1, String str2) {
		for (int i = 0; i < str1.length(); i++) {
			for (int j = i + 1; j <= str1.length(); j++) {
				String substring = str1.substring(i, j);
				if (str2.contains(substring)) {
					return true;
				}
			}
		}
		return false;
	}

	public static String getSubstringUpToSecondWhitespace(String input) {
		// Find the position of the second whitespace
		int firstWhitespaceIndex = input.indexOf(' ');
		int secondWhitespaceIndex = input.indexOf(' ', firstWhitespaceIndex + 1);

		// If there is a second whitespace, get the substring up to that position
		if (secondWhitespaceIndex != -1) {
			return input.substring(0, secondWhitespaceIndex);
		}

		// If there is no second whitespace, return the original string
		return input;
	}
	public static File getLastModified(String directoryFilePath)
	{
		File directory = new File(directoryFilePath);
		File[] files = directory.listFiles(File::isFile);
		long lastModifiedTime = Long.MIN_VALUE;
		File chosenFile = null;

		if (files != null)
		{
			for (File file : files)
			{
				if (file.lastModified() > lastModifiedTime)
				{
					chosenFile = file;
					lastModifiedTime = file.lastModified();
				}
			}
		}

		return chosenFile;
	}
	
	@DataProvider
	public static Object[][] getData(){


		if(excel == null){


			excel = new ExcelReader(System.getProperty("user.dir")+"\\"+excelFileName);


		}


		int rows = excel.getRowCount(sheetName);
		int cols = excel.getColumnCount(sheetName);

		Object[][] data = new Object[rows-1][1];

		Hashtable<String,String> table = null;

		for(int rowNum=2; rowNum<=rows; rowNum++){

			table = new Hashtable<String,String>();

			for(int colNum=0; colNum<cols; colNum++){

				//	data[rowNum-2][colNum]=	excel.getCellData(sheetName, colNum, rowNum);

				table.put(excel.getCellData(sheetName, colNum, 1), excel.getCellData(sheetName, colNum, rowNum));	
				data[rowNum-2][0]=table;	

			}
		}
		return data;
	
}
	}