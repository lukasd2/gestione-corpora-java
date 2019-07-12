package it.unipi.gestione.corpora.controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import it.unipi.gestione.corpora.manager.TextAnalysisFromFiles;
import it.unipi.gestione.corpora.utils.UnzipFiles;

public class GestioneCorpora {

	public static void main(String args[]) throws IOException {
		// Unix and Windows paths examples
		final String UNIXinput = "/home/lukasz/gestione-corpora-java/";
		final String WSinput = "F:\\Java Thingies\\test1g\\";

		final String WSdestPath = "F:\\Java Thingies\\1gout\\";
		final String UnixdestPath = "/home/lukasz/gestione-corpora-java/output/";

		// get path with independent OS format
		String sourcePath = Paths.get(WSinput).toString();
		String destPath = Paths.get(WSdestPath).toString();

		UnzipFiles uf = new UnzipFiles();
		long measureUnzip = startTimeMeasure("*** START UNZIPPING FILES ***");
		//uf.unzipToDirectory(sourcePath, destPath);
		stopTimeMeasure(measureUnzip, "*** STOP UNZIPPING FILES ***");

		long measureText = startTimeMeasure("*** START ANALYZING FILES ***");
		TextAnalysisFromFiles rff = new TextAnalysisFromFiles(destPath);
		stopTimeMeasure(measureText, "*** STOP ANALYZING FILES ***");
	}

	// metodi di utilita per il calcolo dei tempi di esecuzione
	private static long startTimeMeasure(String msg) {
		System.out.println(msg);
		return System.nanoTime();
	}

	private static void stopTimeMeasure(long startTime, String msg) {
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);

		System.out.println(msg);
		System.out.println("Eseguito in " + duration / 1000000 + " milliseconds");
		long seconds = TimeUnit.MILLISECONDS.toSeconds(duration / 1000000);
		System.out.println("Eseguito in " + seconds + " seconds");
	}
}
