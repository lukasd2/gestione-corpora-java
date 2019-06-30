package it.unipi.gestione.corpora.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class readFile {
	
	private static final int BUFFER_SIZE = 4096;
	public static String REGEX_PATTERN_FILE_NAME = "(.*)/(.*.txt)";
	public static String REGEX_PATTERN_MP3_FILE_NAME = "(.*)_M|JPE|PDF|MID|MP3|MUS|PDF|README";


	
	public static void main(String[] args) throws IOException {
		// get path with independent OS format
		long startTime = System.nanoTime();
		String path = Paths.get("F:\\Java Thingies\\1\\").toString();
		System.out.println(path);
		File directory = new File(path);
		List<String> pathList = new ArrayList<>();
		listAllfiles(directory, pathList);

		String destPath = Paths.get("F:\\Java Thingies\\corpora").toString();
		File destDir = new File(destPath);
		byte[] buffer = new byte[BUFFER_SIZE];
		for (String p : pathList) {
			System.out.println(p);
			
			ZipInputStream zis = new ZipInputStream(new FileInputStream(p));
			ZipEntry zipEntry = zis.getNextEntry();
			System.out.println(zipEntry.getName());
			System.out.println("  before>" + zipEntry.getName());

			while (zipEntry != null && zipEntry.getName().contains("txt")) {
				System.out.println("\tafter>" + zipEntry.getName());
				File newFile = newFile(destDir, zipEntry);
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				zipEntry = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
		}
		/*
		 * ZipInputStream zis = new ZipInputStream(new FileInputStream(path)); ZipEntry
		 * zipEntry = zis.getNextEntry(); while (zipEntry != null) { File newFile =
		 * newFile(destDir, zipEntry); FileOutputStream fos = new
		 * FileOutputStream(newFile); int len; while ((len = zis.read(buffer)) > 0) {
		 * fos.write(buffer, 0, len); } fos.close(); zipEntry = zis.getNextEntry(); }
		 * zis.closeEntry(); zis.close();
		 */
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("ms"+duration/1000000);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(duration/1000000);
		System.out.println("seconds" + seconds);
	}

	public static List<String> listAllfiles(File directory, List<String> pathList) throws IOException {
		for (final File f : directory.listFiles()) {
			Pattern pattern = Pattern.compile(REGEX_PATTERN_MP3_FILE_NAME);
			Matcher matcher = pattern.matcher(f.getName());
			if (matcher.find()) continue;
			if (f.isDirectory())  {
				listAllfiles(f, pathList);
			}
			if (f.isFile()) {

				pathList.add(f.getCanonicalPath());
			}
		}
		return pathList;
	}
	// od guards against writing files to the file system outside target folder
	public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		String s = zipEntry.getName();		
		Pattern pattern = Pattern.compile(REGEX_PATTERN_FILE_NAME);
		Matcher matcher = pattern.matcher(s);
		if (matcher.find()) {
			s = matcher.group(2);
		}
		
		File destFile = new File(destinationDir, s);

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}
}
