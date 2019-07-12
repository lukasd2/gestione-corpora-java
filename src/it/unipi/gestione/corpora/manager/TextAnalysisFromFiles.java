package it.unipi.gestione.corpora.manager;

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

import it.unipi.gestione.corpora.utils.SimpleTokenizer;

public class TextAnalysisFromFiles {

	private final File[] listOfFiles;

	private Map<String, MutableInteger> frequencyMap = new HashMap<String, MutableInteger>();
	private ArrayList<Double> heapValuesList = new ArrayList<Double>();

	public final SimpleTokenizer simpleTokenizer = new SimpleTokenizer();
	private final String bookStart = "*** START OF THIS PROJECT GUTENBERG";
	private final String bookEnd = "*** END OF THIS PROJECT GUTENBERG";

	private int tokenCount;
	private int documentCount; // conto i documenti che soddisfano i requisti quindi effettivamente analizzati

	private int threshold = 100000; 
	private int thresholdStep = 100000;

	// setters and getters
	public int getDocumentCount() {
		return documentCount;
	}

	public void incrementDocumentCount() {
		this.documentCount += 1;
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

	// constructor
	public TextAnalysisFromFiles(String destPath) throws IOException {
		File directoryText = new File(destPath);
		listOfFiles = directoryText.listFiles();

		parseFiles();
		System.out.printf("\tTotal tokens processed: %d within %d separate text files!%n", this.getTokenCount(),
				this.getDocumentCount());
		// output
		printResultsToFile();
		printOrderedResultsToFile();
		printHeapLawValues();
	}

	public void parseFiles() {
		for (File file : listOfFiles) {
			if (file.isFile()) {
				try {
					parseTextFromFile(file);
				} catch (IOException e) {
					System.out.println("Fallimento lettura del file errore:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	// wrapper per contare le occorrenze come valori interi <Integer>
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

	private void parseTextFromFile(File file) throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader(file));
		String currentLine;
		int rowIndex = 0;
		boolean bookContent = false;

		while (((currentLine = reader.readLine()) != null || rowIndex <= 40) && !bookContent) { // questi dati sono presenti nell'intestazione tipicamente tra le prime righe
			if (currentLine.trim().toLowerCase().startsWith("language")
					&& !currentLine.toLowerCase().contains("english"))
				break;
			if (currentLine.contains(bookStart))
				bookContent = true;
			rowIndex++;
		}
		if (!bookContent) {
			reader.close();
			return;
		}

		this.incrementDocumentCount();

		while ((currentLine = reader.readLine()) != null) {
			if (currentLine.isEmpty())
				continue;
			if (currentLine.contains(bookEnd))
				break;

			String[] wordList = simpleTokenizer.split(currentLine.toLowerCase().trim());

			for (String s : wordList) {
				frequencyMap.compute(s, (k, v) -> v == null ? new MutableInteger(0) : v).increment();
				this.incrementTokenCount();
			}
			if (this.getTokenCount() >= this.getThreshold()) {
				int vocabulary = frequencyMap.size(); // le singole occorrenze di tutte le parole --> il vocabolario
				calculateHeapsLaw(vocabulary); // semplificazione numero token per valori di threshold elevati
				// calculateTtr(vocabulary);
				this.incrementThreshold();
			}
		}
		reader.close();
	}

	private void calculateHeapsLaw(int vocabulary) {
		// costanti
		final int k = 65;
		final double beta = 0.55;
		// Esempio di fine tuning eseguito per raffinare le previsioni
		// double value = k * Math.pow((double) 1189929244, beta); // fitting for costants (sapendo che in output abbiamo 6384579 parole distinte)
		double value = k * Math.pow((double) getTokenCount(), beta);
		heapValuesList.add(value);
	}

	private void calculateTtr(int vocabulary) { // poco rilevante su grandi quantità di dati
		double ttr = (vocabulary * 1.0) / (getTokenCount() * 1.0);
		System.out.println("Type Token Ratio\t" + String.format("%.6f", ttr));
		System.out.println("Type Token Ratio in %\t" + String.format("%.3f", ttr * 100));
	}

	private void printResultsToFile() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("tokenizeResult.txt"));
		frequencyMap.entrySet().forEach(entry -> {
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
		BufferedWriter bw = new BufferedWriter(new FileWriter("tokenizeResultOrdered.txt"));

		LinkedList<Entry<String, MutableInteger>> list = new LinkedList<>(frequencyMap.entrySet());

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

	public void printHeapLawValues() throws IOException {
		BufferedWriter bw2 = new BufferedWriter(new FileWriter("heapStats.txt"));

		for (Double d : heapValuesList) {
			bw2.write(String.format("%.6f", d));
			bw2.newLine();
		}
		bw2.close();
	}
}