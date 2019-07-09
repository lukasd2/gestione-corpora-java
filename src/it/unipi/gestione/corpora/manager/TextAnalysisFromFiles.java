package it.unipi.gestione.corpora.manager;

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

import it.unipi.gestione.corpora.utils.SimpleTokenizer;

public class TextAnalysisFromFiles {

	private final File[] listOfFiles;

	Map<String, MutableInteger> map = new HashMap<String, MutableInteger>();

	SimpleTokenizer st = new SimpleTokenizer();

	private int tokenCount;
	
	private int threshold;
	private int thresholdStep = 5000;

	public int getTokenCount() {
		return tokenCount;
	}

	public void incrementTokenCount() {
		this.tokenCount += 1;
	}
	
	public int getThreshold() {
		return this.threshold;
	}
	
	public void incrementThreshold() {
		this.threshold += this.thresholdStep;
	}

	public TextAnalysisFromFiles(String destPath) throws IOException {
		File directoryText = new File(destPath);
		listOfFiles = directoryText.listFiles();
		parseFile();
		System.out.println(this.getTokenCount() + " tokens has been processed");
		printResults();
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
			if (value > this.count)
				return -1;
			else if (value < this.count)
				return 1;
			else
				return 0;
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
			if (currentLine.trim().startsWith("Language:") && !currentLine.contains(language))
				break;
			if (currentLine.contains(bookStart))
				bookContent = true;
			rowIndex++;
		}
		if(!bookContent) {
			reader.close();
			return;
		}
		while ((currentLine = reader.readLine()) != null) {
			if (currentLine.isEmpty())
				continue;
			if (currentLine.contains(bookEnd))
				break;

			String[] wordList = st.split(currentLine.toLowerCase());
			
			for (String s : wordList) {
				map.compute(s, (k, v) -> v == null ? new MutableInteger(0) : v).increment();
				this.incrementTokenCount();
			}
			/*if(this.getTokenCount() >= this.getThreshold()) {
				heapsLaw();
			}*/
		}
		reader.close();
	}
	// TODO restituire il numero di step effettuati
	private void heapsLaw() {
		int k = 50;
		double beta = 0.5; // 0,5
		
		double value = k * Math.pow((double) threshold, beta);
		System.out.println(Math.round(value));
		this.incrementThreshold();
	}

	private void printResults() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("tokenizeResult3.txt"));
		map.entrySet().forEach(entry -> {
			try {
				writer.write(entry.getKey() + "\t" + entry.getValue().getInteger());
				writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		writer.close();

		sortbykey();
	}

	public void sortbykey() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter("tokenizeResult4.txt"));

		LinkedList<Entry<String, MutableInteger>> list = new LinkedList<>(map.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<String, MutableInteger>>() {
			public int compare(Map.Entry<String, MutableInteger> o1, Map.Entry<String, MutableInteger> o2) {
				return (o2.getValue()).compareTo(o1.getValue().getInteger());
			}

		});
		for (Entry<String, MutableInteger> entry : list) {
			bw.write(entry.getKey() + " " + entry.getValue().getInteger());
			bw.newLine();
		}
		bw.close();
	}
}
