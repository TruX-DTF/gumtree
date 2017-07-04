package edu.lu.uni.serval.gumtree.utils;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {

	/**
	 * 
	 * @param filePath
	 */
	public static void createDirectory(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}
	
	public static void deleteDirectory(String dir) {
		File file = new File(dir);
		
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				if (files.length > 0) {
					for (File f : files) {
						if (f.isFile()) {
							deleteFile(f.getAbsolutePath());
						} else {
							deleteDirectory(f.getAbsolutePath());
						}
					}
				}
				file.delete();
			} else {
				deleteFile(dir);
			}
		}
	}
	
	public static void deleteFiles(String dir) {
		File file = new File(dir);
		
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				if (files.length > 0) {
					for (File f : files) {
						if (f.isFile()) {
							deleteFile(f.getAbsolutePath());
						} else {
							deleteFiles(f.getAbsolutePath());
						}
					}
				}
			} else {
				deleteFile(dir);
			}
		}
	}
	
	public static void deleteFile(String fileName) {
		File file = new File(fileName);
		
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else {
				deleteDirectory(fileName);
			}
		} 
	}
	
	public static List<File> getAllDirectories(String filePath) {
		return listAllDirectories(new File(filePath));
	}
	
	/**
	 * List all files in the directory.
	 * 
	 * @param filePath
	 * @param type
	 * @return
	 */
	public static List<File> getAllFiles(String filePath, String type) {
		return listAllFiles(new File(filePath), type);
	}
	
	public static List<File> getAllFilesInCurrentDiectory(String filePath, String type) {
		List<File> fileList = new ArrayList<>();
		
		File directory = new File(filePath);
		if (!directory.exists()) {
			return null;
		}
		
		File[] files = directory.listFiles();
		
		for (File file : files) {
			if (file.isFile()) {
				if (file.toString().endsWith(type)) {
					fileList.add(file);
				}
			} 
		}
		
		return fileList;
	}
	
	public static String getFileName(String filePath) {
		File file = new File(filePath);
		
		if (file.exists()) {
			return file.getName();
		} else {
			return null;
		}
	}
	
	public static String getFileNameWithoutExtension(File file) {
		if (file.exists()) {
			String fileName = file.getName();
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
			return fileName;
		} else {
			return null;
		}
	}

	public static String getFileExtension(File file) {
		String fileName = file.getName();
		String extension = fileName.substring(fileName.lastIndexOf("."));
		return extension;
	}
	
	/**
	 * Check whether a file path is valid or not.
	 * 
	 * @param path, file path.
	 * @return true, the file path is valid.
	 * 		   false, the file path is invalid.
	 */
	public static boolean isValidPath(String path) {
		File file = new File(path);
		
		if (file.exists()) {
			return true;
		}
		
		return false;
	}

	/**
	 * Recursively list all files in file.
	 * 
	 * @param file
	 * @return
	 */
	private static List<File> listAllFiles(File file, String type) {
		List<File> fileList = new ArrayList<>();
		
		if (!file.exists()) {
			return null;
		}
		
		File[] files = file.listFiles();
		
		for (File f : files) {
			if (f.isFile()) {
				if (f.toString().endsWith(type)) {
					fileList.add(f);
				}
			} else {
				fileList.addAll(listAllFiles(f, type));
			}
		}
		
		return fileList;
	}
	
	/**
	 * Recursively list all directories in file.
	 * 
	 * @param file
	 * @return
	 */
	private static List<File> listAllDirectories(File file) {
		List<File> fileList = new ArrayList<>();
		
		File[] files = file.listFiles();
		
		for (File f : files) {
			if (f.isDirectory()) {
				fileList.add(f);
				fileList.addAll(listAllDirectories(f));
			} 
		}
		
		return fileList;
	}
	
	public static void makeDirectory(String fileName) {
		deleteFile(fileName);
		File file = new File(fileName.substring(0, fileName.lastIndexOf("/")));
		if (!file.exists()) {
			file.mkdirs();
		}
	}
	
	/**
	 * Read the content of a file.
	 * 
	 * @param fileName
	 * @return String, the content of a file.
	 */
	public static String readFile(String fileName) {
		byte[] input = null;
		BufferedInputStream bis = null;
		
		try {
			
			bis = new BufferedInputStream(new FileInputStream(fileName));
			input = new byte[bis.available()];
			bis.read(input);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(bis);
		}
		
		String sourceCode = null;
		if (input != null) {
			sourceCode = new String(input);
		}
		
		return sourceCode;
	}
	
	/**
	 * Read the content of a file.
	 * 
	 * @param file
	 * @return String, the content of a file.
	 */
	public static String readFile(File file) {
		byte[] input = null;
		BufferedInputStream bis = null;
		
		try {
			
			bis = new BufferedInputStream(new FileInputStream(file));
			input = new byte[bis.available()];
			bis.read(input);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(bis);
		}
		
		String sourceCode = null;
		if (input != null) {
			sourceCode = new String(input);
		}
		
		return sourceCode;
	}
	
	public static void renameFile() {
		List<File> files = getAllDirectories("outputData/cnn/");
		files.addAll(getAllDirectories("outputData/encoder/"));
		
//		for (File file : files) {
//			
//			String fileName = file.getPath();
//			if ("RAW_CAMEL_TOKENIATION".equals(file.getName())) {
//				//1
//				file.renameTo(new File(fileName.replace(file.getName(), "1")));
//			} else if ("SIMPLIFIED_NLP".equals(file.getName())) {
//				//2
//				file.renameTo(new File(fileName.replace(file.getName(), "2")));
//			} else if ("SIMPLIFIED_NLP(2)".equals(file.getName())) {
//				//3
//				file.renameTo(new File(fileName.replace(file.getName(), "3")));
//			} else if ("TOKENAZATION_WITH_NLP".equals(file.getName())) {
//				//4
//				file.renameTo(new File(fileName.replace(file.getName(), "4")));
//			} else if ("TOKENAZATION_WITH_NLP(2)".equals(file.getName())) {
//				//5
//				file.renameTo(new File(fileName.replace(file.getName(), "5")));
//			}
//		}
	}
	
	/**
	 * Output output into a file.
	 * @param fileName, output file name.
	 * @param data, output data.
	 * @param append, the output data will be appended previous data in the file or not.
	 */
	public static void outputToFile(String fileName, StringBuilder data, boolean append) {
		File file = new File(fileName);
		FileWriter writer = null;
		BufferedWriter bw = null;

		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			writer = new FileWriter(file, append);
			bw = new BufferedWriter(writer);
			bw.write(data.toString());
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(bw);
			close(writer);
		}
	}

	private static void close(FileWriter writer) {
		try {
			if (writer != null) {
				writer.close();
				writer = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void close(BufferedWriter bw) {
		try {
			if (bw != null) {
				bw.close();
				bw = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void close(BufferedInputStream bis) {
		try {
			if (bis != null) {
				bis.close();
				bis = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
