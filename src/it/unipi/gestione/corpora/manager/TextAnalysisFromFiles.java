package it.unipi.gestione.corpora.manager;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.output.FileWriterWithEncoding;

import it.unipi.gestione.corpora.utils.SimpleTokenizer;

public class TextAnalysisFromFiles {

	private final File[] listOfFiles;

	Map<String, MutableInteger> map = new HashMap<String, MutableInteger>();
	ArrayList<Double> heapValues = new ArrayList<Double>();

	SimpleTokenizer st = new SimpleTokenizer();

	private boolean detailedStats;
	private int tokenCount;

	private int threshold = 10000;
	private int thresholdStep = 10000;

	// setters and getters
	public boolean getDetailedStats() {
		return this.detailedStats;
	}

	public void setDetailedStats(boolean detailedStats) {
		this.detailedStats = detailedStats;
	}

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

	public TextAnalysisFromFiles(String destPath, boolean detailedStats) throws IOException {
		this.setDetailedStats(detailedStats);

		File directoryText = new File(destPath);
		listOfFiles = directoryText.listFiles();
		
		parseFile();
		System.out.println(this.getTokenCount() + " tokens in total has been processed");
		
		printResultsToFile();
		printOrderedResultsToFile();
		printHeapValues();
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
		if (!bookContent) {
			reader.close();
			return;
		}
		while ((currentLine = reader.readLine()) != null) {
			if (currentLine.isEmpty())
				continue;
			if (currentLine.contains(bookEnd))
				break;

			String[] wordList = st.split(currentLine.toLowerCase().trim());

			for (String s : wordList) {
				// if(!s.equals("\t")) TODO da considerare
				map.compute(s, (k, v) -> v == null ? new MutableInteger(0) : v).increment();
				this.incrementTokenCount();
			}
			if (this.getTokenCount() >= this.getThreshold()) {
				int vocabulary = map.size(); // le singole occorrenze di tutte le parole --> il vocabolario
				calculateHeapsLaw(vocabulary); // TODO spiegare semplificazione numero token
				// calculateTtr(vocabulary);
				this.incrementThreshold();
			}
		}
		reader.close();
	}

	private void calculateHeapsLaw(int vocabulary) {
		// costanti
		final int k = 47;
		final double beta = 0.5;

		// double value = k * Math.pow((double) 603849, beta); TODO fitting for costants
		// (STANFORD)
		// System.out.println("prevision " + k * Math.pow(this.getTokenCount(), beta));

		double heapsValue = k * Math.pow(this.getTokenCount(), beta);
		heapValues.add(heapsValue);
		// System.out.println(vocabulary);
		// System.out.println(getTokenCount());
		//System.out.println(heapsValue);
	}

	private void calculateTtr(int vocabulary) {
		double ttr = (vocabulary * 1.0) / (getTokenCount() * 1.0);
		System.out.println("Type Token Ratio\t" + String.format("%.6f", ttr));
		System.out.println("Type Token Ratio in %\t" + String.format("%.3f", ttr * 100));
	}

	private void printResultsToFile() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriterWithEncoding("tokenizeResult.txt", UTF_8));
		map.entrySet().forEach(entry -> {
			try {
				writer.write(entry.getKey() + "\t" + entry.getValue().getInteger());
				writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		writer.close();
	}

	public void printOrderedResultsToFile() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriterWithEncoding("tokenizeResultOrdered.txt", UTF_8));

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
	
	public void printHeapValues() throws IOException {
		BufferedWriter bw2 = new BufferedWriter(new FileWriter("heapStats.txt"));

		for(Double d : heapValues) {
			bw2.write(String.format("%.6f", d));
			bw2.newLine();
		}
	}
}
