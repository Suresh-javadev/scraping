package org.ans.scraping;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FileCreationForFolioUpload {

	private FileCreationForFolioUpload(){}
	
	public static String createFileFromFolio(List<String> folios) throws IOException {
		String filename = "folio_"+System.currentTimeMillis()+"_list.txt";
		
		File file =new File(filename);	
		
	    FileOutputStream outputStream = new FileOutputStream(file);
	    StringBuilder sb=new StringBuilder();
	    
	    for(String folio:folios) {
	      sb.append(folio);
	      sb.append("\n");
	    }
	    
	    byte[] strToBytes = sb.toString().getBytes();
	    outputStream.write(strToBytes);

	    outputStream.close();
	    
	    return filename;
	}
	public static void deleteFileAfterUpload(String fileName) {
		File file =new File(fileName);
		
		if(file.exists())
			file.delete();
	}
}
