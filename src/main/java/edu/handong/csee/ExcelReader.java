package edu.handong.csee;

import java.io.FileInputStream;
import java.util.Iterator;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelReader {
	
	public ArrayList<String> getData(String path) {
		ArrayList<String> values = new ArrayList<String>();
		
		System.out.println(path);
		
		try (InputStream inp = new FileInputStream(path)) {
		    //InputStream inp = new FileInputStream("workbook.xlsx");
		    
		        Workbook wb = WorkbookFactory.create(inp);
		        Sheet sheet = wb.getSheetAt(0);
		        
		   
		        
		        //sheet의 두번째줄
		        Row row = sheet.getRow(2);
		        //두번째 줄의 
		        Cell cell = row.getCell(1);
		        if (cell == null)
		            cell = row.createCell(3);
		        
		        values.add(cell.getStringCellValue());
		        
		    } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return values;
	}
	
	public ArrayList<String> getData(InputStream is) {
		ArrayList<String> values = new ArrayList<String>();
		String tempString = new String();
		try (InputStream inp = is) {
		    //InputStream inp = new FileInputStream("workbook.xlsx");
		        Workbook wb = WorkbookFactory.create(inp);
		        Sheet sheet = wb.getSheetAt(0);
		  
		        for(int i = 0; i <= sheet.getLastRowNum(); i++) {		        	
		        	Row row = sheet.getRow(i);
		        	tempString = "";
		        	for(int j = 0; j <= row.getLastCellNum(); j++) {
		        		Cell cell = row.getCell(j);
		        		if (cell == null)
		        			cell = row.createCell(10);
		        		tempString = tempString + cell.getStringCellValue();
		        	}
		        	values.add(tempString);		        	
		        }
		  
			
		    } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return values;
	}
}
