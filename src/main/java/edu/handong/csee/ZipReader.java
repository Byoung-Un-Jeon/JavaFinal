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
		//���� �ȿ� �ִ� �� ����� ���ʷ� �޴´�.
		readFileInZip("0001.zip");
	}

	public void readFileInZip(String path) {
		ZipFile zipFile;
		String target = "output.csv";
		ArrayList<String> middle = new ArrayList<String>();
		ArrayList<String> result = new ArrayList<String>();
		try {
			//zipfile �̶�� ģ�� ����
			zipFile = new ZipFile(path);
			Enumeration<? extends ZipArchiveEntry> entries = zipFile.getEntries();

		    while(entries.hasMoreElements()){
		    	//�� ���� �ȿ� �ִ� ���� ��� = entries
		    	//entries���� entry�� �ϳ� �ϳ� �޾ƿ´�.
		    	ZipArchiveEntry entry = entries.nextElement();
		        //�޾ƿ� ��Ʈ���� ���� ������ stream���� ������ش�.
		    	InputStream stream = zipFile.getInputStream(entry);
		        //�� ���� �̸��� stream�̴�.
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

