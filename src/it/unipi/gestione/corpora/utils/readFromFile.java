package it.unipi.gestione.corpora.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class readFromFile {
	
	public FileOutputStream whenReadWithBufferedReader_thenCorrect(File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file.getName(), true);

		BufferedReader reader = new BufferedReader(new FileReader(file));
		String currentLine;
		String bookStart = "*** START OF THIS PROJECT GUTENBERG";
		String bookEnd = "*** END OF THIS PROJECT GUTENBERG ";
		
		String language = "English";
		
		
		int rowIndex = 0;
		boolean bookContent = false;
		boolean bookContentEnd = false;
		while (((currentLine = reader.readLine()) != null || rowIndex <= 50) && !bookContent) {
			if(currentLine.trim().startsWith("Language") && !currentLine.contains("English")) return null;
			if(currentLine.contains(bookStart)) bookContent = true;
			rowIndex++;
		}
		int startRow = 0;
		while ((currentLine = reader.readLine()) != null && !bookContentEnd) {
			if(startRow < rowIndex) reader.readLine();
			fos.write(currentLine.getBytes());
			if(currentLine.contains(bookStart)) bookContentEnd = true;
		}
		reader.close();
	   // fos.close();
		return fos;
	}
}
