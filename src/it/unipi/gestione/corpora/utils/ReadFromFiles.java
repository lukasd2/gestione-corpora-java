package it.unipi.gestione.corpora.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ReadFromFiles {

	private final File[] listOfFiles;
	
    Map<String, MutableInteger> map = new HashMap<String, MutableInteger>();
    
    SimpleTokenizer st = new SimpleTokenizer();
	
	public ReadFromFiles(String destPath) {
		File directoryText = new File(destPath);
		listOfFiles = directoryText.listFiles();
		parseFile();
	}

	public void parseFile() {
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	try {
					parseText(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		}
	}
	
	private static class MutableInteger {
	    int count = 1;
	    
	    public MutableInteger(int count) {
	    	this.count = count;
	    }
	    
	    public void increment() {
	        this.count++;
	    }
	     
	    public int getInteger() {
	    	return this.count;
	    }
	}


	private void parseText(File file) throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader(file));
		String bookStart = "*** START OF THIS PROJECT GUTENBERG";
		String bookEnd = "*** END OF THIS PROJECT GUTENBERG ";
		
		String language = "English";
		String currentLine;
		int rowIndex = 0;
		boolean bookContent = false;
		
		while (((currentLine = reader.readLine()) != null || rowIndex <= 50) && !bookContent) {
			if(currentLine.trim().startsWith("Language:") && !currentLine.contains(language)) break;
			if(currentLine.contains(bookStart)) bookContent = true;
			rowIndex++;
		}
		
		if(bookContent) {
			for(int i = 0; i < rowIndex; i ++) {
				reader.readLine();
			}
			
			int countWord = 0; 
			while (((currentLine = reader.readLine()) != null)) {
				if(currentLine.equals("")) continue;
				
				String[] wordList = st.split(currentLine.toLowerCase());
				//String delims = "[ .,?!]+";
				//String[] wordList = currentLine.split(delims);
				//countWord += wordList.length; 
				for(String s : wordList) {
					map.compute(s, (k, v) -> v == null
							? new MutableInteger(0) : v).increment();
					/*if (map.containsKey(s)) {
			            // increment occurrence
			            int occurrence = map.get(s);
			            occurrence++;
			            map.put(s, occurrence);
			        } else {
			            // add word and set occurrence to 1
			            map.put(s, 1);
			        }*/
				}
				//System.out.println(currentLine);
			}
			//System.out.println(countWord);

		}
		reader.close();
		printResults();
	}
	
	private void printResults() throws IOException {
			BufferedWriter writer = new BufferedWriter(new FileWriter("tokenizeResult.txt"));
			map.entrySet().forEach(entry->{
			    try {
					writer.write(entry.getKey() + " " + entry.getValue().getInteger());
			        writer.newLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			 });
			 writer.close();
			
			 sortbykey();
	}
	
	public void sortbykey() 
    { 
		// TODO need to write a custom comparator
		
        // TreeMap to store values of HashMap 
        TreeMap<String, MutableInteger> sorted = new TreeMap<>(map); 
  
        // Display the TreeMap which is naturally sorted 
        for (Map.Entry<String, MutableInteger> entry : sorted.entrySet())  
            System.out.println("Key = " + entry.getValue().getInteger() +  
                         ", Value = " + entry.getKey());
    } 

}
