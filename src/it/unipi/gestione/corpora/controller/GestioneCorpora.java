package it.unipi.gestione.corpora.controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import it.unipi.gestione.corpora.utils.unzipFiles;

public class GestioneCorpora {
	public static void main(String args[]) throws IOException {
		// get path with independent OS format
		String path = Paths.get("F:\\Java Thingies\\0\\").toString();
		
		unzipFiles uf = new unzipFiles();
		long startTime = System.nanoTime();
		
		uf.readFile(path);
		
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("ms"+duration/1000000);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(duration/1000000);
		System.out.println("seconds" + seconds);
	}
}
