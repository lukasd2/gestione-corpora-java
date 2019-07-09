package it.unipi.gestione.corpora.controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import it.unipi.gestione.corpora.manager.TextAnalysisFromFiles;
import it.unipi.gestione.corpora.utils.UnzipFiles;

public class GestioneCorpora {
	public static void main(String args[]) throws IOException {
		final String UNIX = "/home/lukasz/gestione-corpora-java/0";
		final String WS = "F:\\Java Thingies\\tokenizeTest\\";

		final String WSdestPath = "F:\\Java Thingies\\parsing3\\";
		final String UnixdestPath = "/home/lukasz/gestione-corpora-java/output/";

		// get path with independent OS format
		String sourcePath = Paths.get(WS).toString();
		String destPath = Paths.get(WSdestPath).toString();

		UnzipFiles uf = new UnzipFiles();
		System.out.println("*** START UNZIPPING FILES ***");
		long startTime = System.nanoTime();

		uf.unzipToDirectory(sourcePath, destPath);
		
		System.out.println("*** STOP UNZIPPING FILES ***");

		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("ms " + duration / 1000000);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(duration / 1000000);
		System.out.println("UNZIP seconds " + seconds);

		long startTime2 = System.nanoTime();
		System.out.println("*** START ANALYZING FILES ***");
		
		TextAnalysisFromFiles rff = new TextAnalysisFromFiles(destPath);

		System.out.println("*** STOP ANALYZING FILES ***");
		
		long endTime2 = System.nanoTime();
		long duration2 = (endTime2 - startTime2);
		System.out.println("ms " + duration2 / 1000000);
		long seconds2 = TimeUnit.MILLISECONDS.toSeconds(duration2 / 1000000);
		System.out.println("Frequency seconds " + seconds2);

	}
}
