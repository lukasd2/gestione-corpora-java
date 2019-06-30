package it.unipi.gestione.corpora.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class unzipFiles {
	
	private static final int BUFFER_SIZE = 4*1024;
	public static String REGEX_PATTERN_FILE_NAME = "(.*)/(.*.txt)";
	public static String REGEX_PATTERN_MP3_FILE_NAME = "(.*)_M|JPE|PDF|MID|MP3|MUS|PDF|README";
	private List<String> pathList;
	private List<String> normalizedPathList = new ArrayList<String>();
	
	Charset charset = UTF_8;
	
	public unzipFiles() {
		this.pathList = new ArrayList<String>();
	}
	
	public void readFile(String path) throws IOException {
		// get path with independent OS format
		//String path = Paths.get("F:\\Java Thingies\\0\\").toString();
		//System.out.println(path);
		File directory = new File(path);
		this.pathList = listAllfiles(directory, pathList);
		if(this.pathList.size() > 1) { 
			// TODO add this condition to the rest of method
		}
		int currentElem = 1;
		for (Iterator<String> it = this.pathList.iterator(); it.hasNext(); ) {
		    String dupl = it.next();	        
		}
		
		for(int i = 1; i < this.pathList.size() - 1; i++) {
			String elem = this.pathList.get(i);
			String prevElem = this.pathList.get(i - 1);
			String nextElem = this.pathList.get(i + 1);
			
			//System.out.println("standart list " + this.pathList.get(i));
			if(elem.contains("_8.ZIP")) {
				String check = elem.replaceAll("_8", "");
				//System.out.println(check + " " + elem);
				
				prevElem.substring(prevElem.lastIndexOf(System.getProperty("file.separator")) + 1);
				nextElem.substring(nextElem.lastIndexOf(System.getProperty("file.separator")) + 1);
				
				//System.out.println("check " + check);
				//System.out.println("prev " + prevElem);
				
				if(check.equals(prevElem) || check.equals(nextElem)) {
					//this.pathList.remove(prevElem);
					this.normalizedPathList.add(elem);
				} else {
					
				}
				//if(check.equals(nextElem)) this.pathList.remove(nextElem);
			}
			//System.out.println("Normalized List " + this.pathList.get(i));
			
		}
		
		for(String s : this.normalizedPathList) {
			System.out.println("Normalized List " + s);
		}
		
		String destPath = Paths.get("F:\\Java Thingies\\parsing1\\").toString();
		File destDir = new File(destPath);
		byte[] buffer = new byte[BUFFER_SIZE];
		for (String p : this.pathList) {
			//System.out.println("\t p "+ p);
			ZipInputStream zis = new ZipInputStream(new FileInputStream(p), UTF_8);
			ZipEntry zipEntry = zis.getNextEntry();
			System.out.println("  before>" + zipEntry.getName());

			while (zipEntry != null && zipEntry.getName().contains("txt")) {
				//System.out.println("\tafter>" + zipEntry.getName());
				File newFile = newFile(destDir, zipEntry);
								
				FileOutputStream fos = new FileOutputStream(newFile);
		        BufferedOutputStream bufout = new BufferedOutputStream(fos);
		        
				int len;
				while ((len = zis.read(buffer)) > 0) {
					bufout.write(buffer, 0, len);
				}		
				bufout.flush();
				bufout.close();
				fos.close();
				zipEntry = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
		}
	}

	public List<String> listAllfiles(File directory, List<String> pathList) throws IOException {
		// TODO eliminare i zip doppi con encoding differente, scelgliere uno tra ASCII e ISO latin 1
		Pattern pattern = Pattern.compile(REGEX_PATTERN_MP3_FILE_NAME);
		Pattern fileNameNorm = Pattern.compile("(.*)\\.|(_8)");
		for (final File f : directory.listFiles()) {
			Matcher matcher = pattern.matcher(f.getName());
			if (matcher.find()) continue;
			if (f.isDirectory())  {
				if(f.listFiles().length > 1) {
					//System.out.println("more than one file bro" + f.getCanonicalPath());
				}
				listAllfiles(f, pathList);
			}
			//String lastEntry = pathList.get(pathList.size() - 1);
			//lastEntry.substring(0, lastEntry.indexOf("."));
			if (f.isFile()) {
				String currentEntry = f.getName();
				//System.out.println("Current Entry " + currentEntry );
				/*if(lastEntry.length() != currentEntry.length()) {
					if(lastEntry.contains("_8")) lastEntry.substring(0, lastEntry.indexOf("_"));
					else if(lastEntry.contains("_8")) lastEntry.substring(0, lastEntry.indexOf("_"));
				}*/
				// again inconsistencies between OS's
				/*String duplicateCheck = f.getCanonicalPath();
				System.out.println(duplicateCheck.substring(duplicateCheck.lastIndexOf(System.getProperty("file.separator")) + 1,
						duplicateCheck.indexOf('.')));
				String normalizer = duplicateCheck.substring(duplicateCheck.lastIndexOf(System.getProperty("file.separator")) + 1,
						duplicateCheck.indexOf('.'));
				if(pathList.get(pathList.size() - 1) != normalizer) {
					
				}*/
				
				String normalizer = currentEntry.substring(0, currentEntry.indexOf("."));
				
				//System.out.println(f.getCanonicalPath().substring(f.getCanonicalPath().lastIndexOf(System.getProperty("file.separator")) + 1));
				pathList.add(f.getCanonicalPath());
				
				
			}
		}
		return pathList;
	}
	
	
	// od guards against writing files to the file system outside target folder
	public File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
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