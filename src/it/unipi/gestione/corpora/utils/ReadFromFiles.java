package it.unipi.gestione.corpora.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
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
	
	private static class MutableInteger implements Comparable<Integer> {
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

		@Override
		public int compareTo(Integer value) {
			if(value > this.count) return -1;
			else if(value < this.count) return 1;
			else return 0;
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
			int threshold = 1000;
			while (((currentLine = reader.readLine()) != null)) {
				if(currentLine.equals("")) continue;
				
				String[] wordList = st.split(currentLine.toLowerCase());
				//String delims = "[ .,?!]+";
				//String[] wordList = currentLine.split(delims);
				//countWord += wordList.length; 
				for(String s : wordList) {
					map.compute(s, (k, v) -> v == null
							? new MutableInteger(0) : v).increment();
					countWord += 1;
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
				if(countWord >= threshold) threshold = heapsLaw(threshold);
			}
			//System.out.println(countWord);

		}
		reader.close();
		printResults();
	}
	
	private int heapsLaw(int threshold) {
		int k = 50;
		double beta = Math.sqrt(threshold); // 0,5
		System.out.println("beta" + beta);
		
		double value = k * Math.pow((double)threshold, beta);
		
		System.out.println("heaps: " + value);
		
		return threshold += 1000;
		
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
	
	public void sortbykey() throws IOException 
    { 
		// TODO need to write a custom comparator
		BufferedWriter writer2 = new BufferedWriter(new FileWriter("tokenizeResult2.txt"));

		LinkedList<Entry<String, MutableInteger>> list = new LinkedList<>(map.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<String, MutableInteger>>()
		  {
		     public int compare(Map.Entry<String, MutableInteger> o1,
		     Map.Entry<String, MutableInteger> o2) {
		         return (o2.getValue()).compareTo(o1.getValue().getInteger());
		       }

		  });
		for(Entry<String, MutableInteger> entry : list) {
			writer2.write(entry.getKey() + " " + entry.getValue().getInteger());
	        writer2.newLine();
		}
		writer2.close();
		//System.out.println("The most common first name is: " + list.get(0).getKey() + " and it occurs: " + list.get(0).getValue().getInteger() + " times.");
    } 

}
