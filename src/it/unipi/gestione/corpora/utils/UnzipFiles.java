package it.unipi.gestione.corpora.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipFiles {

	private static final int BUFFER_SIZE = 4 * 1024;
	public static String REGEX_PATTERN_FILE_NAME = "(.*)/(.*.txt)";
	public static String UNWANTED_FILE_EXTENSIONS = "(.*)_M|JPE|PDF|MID|MP3|MUS|PDF|README";
																								

	private List<String> pathListForExtraction;
	private List<String> pathListEncodedFiles; // contains files with different encoding

	public UnzipFiles() {
		this.pathListForExtraction = new ArrayList<String>();
		this.pathListEncodedFiles = new ArrayList<String>();
	}

	public void unzipToDirectory(String sourcePath, String destinationPath) throws IOException {
		File directory = new File(sourcePath);
		this.pathListForExtraction = listAllfiles(directory, pathListForExtraction);

		int originalSize = pathListForExtraction.size() + pathListEncodedFiles.size();
		System.out.println("La lista originale aveva " + originalSize);

		_filterDuplicates(pathListForExtraction, pathListEncodedFiles);

		System.out.println("La lista filtrata ha " + pathListForExtraction.size());

		File destDir = new File(destinationPath);

		byte[] buffer = new byte[BUFFER_SIZE];
		for (String pl : this.pathListForExtraction) {
			ZipInputStream zis = new ZipInputStream(new FileInputStream(pl), UTF_8);
			ZipEntry zipEntry = zis.getNextEntry();

			while (zipEntry != null && zipEntry.getName().contains("txt")) {
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

	private void _filterDuplicates(List<String> pathList, List<String> normalizedPathList) {
		for (String npl : normalizedPathList) {
			String normalized = npl;
			normalized = normalized.replace("_8.ZIP", ".ZIP");
			if (pathList.indexOf(normalized) >= 0) {
				pathList.set(pathList.indexOf(normalized), npl);
			} else {
				pathList.add(npl);
			}
		}
	}

	public List<String> listAllfiles(File directory, List<String> pathList) throws IOException {
		Pattern pattern = Pattern.compile(UNWANTED_FILE_EXTENSIONS);
		for (final File f : directory.listFiles()) {
			Matcher matcher = pattern.matcher(f.getName());
			if (matcher.find()) {
				continue;
			}
			if (f.isDirectory()) {
				listAllfiles(f, pathList);
			}

			if (f.isFile()) {
				String currentEntry = f.getName();
				if (currentEntry.endsWith("_8.ZIP")) {
					pathListEncodedFiles.add(f.getCanonicalPath());
				} else {
					pathList.add(f.getCanonicalPath());
				}
			}
		}
		return pathList;
	}

	// guards against writing files to the file system outside target folder
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