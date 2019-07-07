package it.unipi.gestione.corpora.controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import it.unipi.gestione.corpora.utils.ReadFromFiles;
import it.unipi.gestione.corpora.utils.UnzipFiles;

public class GestioneCorpora {
	public static void main(String args[]) throws IOException {
		// get path with independent OS format
		String sourcePath = Paths.get("F:\\Java Thingies\\0\\").toString();
		String destPath = Paths.get("F:\\Java Thingies\\tokenizeResult\\").toString();
		
		UnzipFiles uf = new UnzipFiles();
		long startTime = System.nanoTime();
		
		uf.unzipToDirectory(sourcePath, destPath);
		
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("ms "+duration/1000000);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(duration/1000000);
		System.out.println("UNZIP seconds " + seconds);
	
		long startTime2 = System.nanoTime();
		ReadFromFiles rff = new ReadFromFiles(destPath);
		
		long endTime2 = System.nanoTime();
		long duration2 = (endTime2 - startTime2);
		System.out.println("ms "+duration2/1000000);
		long seconds2 = TimeUnit.MILLISECONDS.toSeconds(duration2/1000000);
		System.out.println("Frequency seconds " + seconds2);
		
	}
}
