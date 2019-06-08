package edu.handong.csee;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

public class ZipReader {

	public static void main(String[] args) {
		ZipReader zipReader = new ZipReader();
		zipReader.run(args);
	}

	private void run(String[] args) {
		//String path = args[0];
		//파일 안에 있는 집 목록을 차례로 받는다.
		readFileInZip("0001.zip");
	}

	public void readFileInZip(String path) {
		ZipFile zipFile;
		String target = "output.csv";
		ArrayList<String> middle = new ArrayList<String>();
		ArrayList<String> result = new ArrayList<String>();
		try {
			//zipfile 이라는 친구 생성
			zipFile = new ZipFile(path);
			Enumeration<? extends ZipArchiveEntry> entries = zipFile.getEntries();

		    while(entries.hasMoreElements()){
		    	//집 파일 안에 있는 파일 목록 = entries
		    	//entries에서 entry로 하나 하나 받아온다.
		    	ZipArchiveEntry entry = entries.nextElement();
		        //받아온 엔트리에 대한 정보를 stream으로 만들어준다.
		    	InputStream stream = zipFile.getInputStream(entry);
		        //즉 파일 이름이 stream이다.
		    	ExcelReader myReader = new ExcelReader();
		        
		    	//writeAFile(myReader.getData(stream), target);
		    	middle = myReader.getData(stream);
		    	middle.remove(0);
		        for(String value : middle) {
		        
		        	result.add(value);
		        }
		    }
		    writeAFile(result, target);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


public static void writeAFile(ArrayList<String> lines, String targetFileName) {
	PrintWriter outputStream = null;
	try {
		outputStream = new PrintWriter(targetFileName);
	} catch(FileNotFoundException e) {
		System.out.println("Error opening the file " + targetFileName);
		System.exit(0);
	}
    for (String line : lines) {
        outputStream.println (line);
    }
    outputStream.close();
	}
}

