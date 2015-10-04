package excelIO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {
	private static Workbook ExcelWBook;
	private static Sheet ExcelWSheet;
	private static org.apache.poi.ss.usermodel.Cell Cell;
	
	public static Workbook fileExtension(String filePath) throws IOException{
	    //Create a object of File class to open xlsx file
	    File file =  new File(filePath);
	 
	    //Create an object of FileInputStream class to read excel file
	    FileInputStream inputStream = new FileInputStream(file);
	    ExcelWBook = null;
	 
	    //Find the file extension by spliting file name in substring and getting only extension name
	    String fileExtensionName = filePath.substring(filePath.indexOf("."));
	 
	    //Check condition if the file is xlsx file
	    if(fileExtensionName.equals(".xlsx")){
	    //If it is xlsx file then create object of XSSFWorkbook class
	    	ExcelWBook = new XSSFWorkbook(inputStream);
	    }
	    //Check condition if the file is xls file
	    else if(fileExtensionName.equals(".xls")){
	        //If it is xls file then create object of XSSFWorkbook class
	    	ExcelWBook = new HSSFWorkbook(inputStream);
	    }
	    
	    return ExcelWBook;
	}
	    
	/**
	 * This method is to create an Object[][] for DDT using DataProvider
	 * 
	 * @param FilePath
	 * @param SheetName
	 * @return
	 * @throws Exeption
	 */
	public static Object[][] getTableArray(String FilePath, String SheetName) throws Exception {
		String[][] tabArray = null;
		try {
			// Access the required test data sheet
			ExcelWBook = fileExtension(FilePath);
			ExcelWSheet = ExcelWBook.getSheet(SheetName);
			int startRow = 1;
			int startCol = 0; 
			int ci,cj; 
			int totalRows = ExcelWSheet.getLastRowNum();
			int totalCols = ExcelWSheet.getRow(0).getLastCellNum();
//			System.out.println(totalRows + "," + totalCols);
			tabArray=new String[totalRows][totalCols];
			ci=0; 
			for (int i=startRow;i<=totalRows;i++, ci++) {
				cj=0;
				for (int j=startCol;j<totalCols;j++, cj++){
					tabArray[ci][cj]=getCellDataDDT(i,j);  
//					System.out.println(tabArray[ci][cj]);
				}
			}
		}catch (FileNotFoundException e){
			System.err.println("Could not read the Excel sheet: "+e.getMessage());
		}catch (IOException e){
			System.err.println("Could not read the Excel sheet: "+e.getMessage());  
		} 
		return(tabArray);  
	} 
	
	public static String getCellDataDDT(int RowNum, int ColNum) throws Exception { 
		try{ 
			Cell = ExcelWSheet.getRow(RowNum).getCell(ColNum);
			int dataType = Cell.getCellType();
			
			if  (dataType != 1) { 
				return "";  
			}else{  
				String CellData = Cell.getStringCellValue();
				return CellData;  
			}
		}catch (Exception e){
			System.err.println(e.getMessage());  
			throw (e);  
		}
	}
}
