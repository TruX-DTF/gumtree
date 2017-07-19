package edu.lu.uni.serval.gumtree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.lu.uni.serval.gumtree.regroup.ActionFilter;
import edu.lu.uni.serval.gumtree.regroup.HierarchicalActionSet;
import edu.lu.uni.serval.gumtree.utils.CUCreator;
import edu.lu.uni.serval.gumtree.utils.FileHelper;

public class Inputer {
	
	public static void main(String[] args) {
		try {
			inputData();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void inputData() throws FileNotFoundException, IOException {
		String inputPath = "../../OUTPUT/"; //DiffEntries  prevFiles  revFiles
		File inputFileDirector = new File(inputPath);
		File[] files = inputFileDirector.listFiles();   // project folders
		
		FileHelper.deleteDirectory("../../GumTreeResults/Exp/");
		FileHelper.deleteDirectory("../../GumTreeResults/Exp_ASTNode/");
		FileHelper.deleteDirectory("../../GumTreeResults/Exp_RawCode/");
		
		StringBuilder astEditScriptsBuilder = new StringBuilder();
		
		for (File file : files) {
			String projectFolder = file.getPath();
			File revFileFolder = new File(projectFolder + "/revFiles/");// revised file folder
			File[] revFiles = revFileFolder.listFiles();
			
			for (File revFile : revFiles) {
				if (revFile.getName().endsWith(".java")) {
					File prevFile = new File(projectFolder + "/prevFiles/prev_" + revFile.getName());// previous file
					File diffentryFile = new File(projectFolder + "/DiffEntries/" + revFile.getName().replace(".java", ".txt")); // DiffEntry file
					
					// Three files: revFile, prevFile, diffEntryFile.
					// GumTree results
					List<HierarchicalActionSet> gumTreeResults = new GumTreeComparer().compareTwoFilesWithGumTree(prevFile, revFile);
					// Filter out modified actions of changing method names, method parameters, variable names and field names in declaration part.
					gumTreeResults = new ActionFilter().filterOutUselessActions(gumTreeResults);
					if (gumTreeResults.size() > 0) {
			            CUCreator cuCreator = new CUCreator();
			            CompilationUnit prevUnit = cuCreator.createCompilationUnit(prevFile);
			            CompilationUnit revUnit = cuCreator.createCompilationUnit(revFile);
			            
			            // Commit Message  TODO
			            // COMMIT_MSG#Num.
			            // Source Code.    TODO	 
			            // SOURCE_CODE#Num.
			            String sourceCode = FileHelper.readFile(diffentryFile);
			            for (HierarchicalActionSet gumTreeResult : gumTreeResults) {
			            	// set line numbers
			            	String actionStr = gumTreeResult.getActionString();
			            	CompilationUnit unit;
			            	if (actionStr.startsWith("INS")) {
			            		unit = revUnit;
			            		continue;
			            	} else {
			            		unit = prevUnit;
			            	}
		            		int position = gumTreeResult.getStartPosition();
		            		int startLineNum = unit.getLineNumber(position);
		            		int endLineNum = unit.getLineNumber(position + gumTreeResult.getLength());
		            		if (startLineNum != endLineNum) { // only single buggy line statements.
		            			continue;
		            		}
		            		gumTreeResult.setStartLineNum(startLineNum);
		            		gumTreeResult.setEndLineNum(endLineNum);
		            		
		            		// Source Code of patches.
		            		String patchSourceCode = getPatchSourceCode(sourceCode, startLineNum);
		            		if (patchSourceCode != null) {
		            			
		            		}
		            		
		            		// Simple tree of buggy code. TODO

			            	/**
			            	 * select edit scripts for deep learning.
			            	 */
			            	// 1. First level: AST node type.
			            	String astEditScripts = getASTEditScripts(gumTreeResult);
			            	astEditScriptsBuilder.append(astEditScripts + "\n");
			            	
			            	// 2. source code TODO
			            	
			            	// 3. abstract identifiers TODO
			            	// 4. semi-source code. TODO
			            }
					}
				}
			}
			
			FileHelper.outputToFile("../../GumTreeResults/Exp_ASTNode/EditScripts.list", astEditScriptsBuilder, true);
			astEditScriptsBuilder.setLength(0);
		}
		
	}
	
	private static String getPatchSourceCode(String sourceCode, int startLineNum) {
//		@@ -592,4 +592,5 @@
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new StringReader(sourceCode));
			String line = null;
			boolean contained = false;
			int startLine = 0;
			String buggyStatement = "";
			String fixedStatement = "";
			while ((line = reader.readLine()) != null) {
				if ( !contained && line.startsWith("@@ -")) {
					String lineNum = line.substring(4);
					lineNum = lineNum.substring(0, lineNum.indexOf(" "));
					String[] nums = lineNum.split(",");
					startLine = Integer.parseInt(nums[0].trim());
					int range = Integer.parseInt(nums[1].trim());
					if (startLine > startLineNum) {
						return null; // Wrong Matching.
					}
					if (startLine + range < startLineNum) {
						continue;
					}
					// startLine <= startLineNum <= startLine + range.
					contained = true;
					continue;
				}
				if (contained) {
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
					reader = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null; // TODO
	}

	/**
	 * Get the AST node based edit script of patches in terms of breadth first.
	 * 
	 * @param actionSet
	 * @return
	 */
	private static String getASTEditScripts(HierarchicalActionSet actionSet) {
		String editScript = "";
		
		List<HierarchicalActionSet> actionSets = new ArrayList<>();
		actionSets.add(actionSet);
		while (actionSets.size() != 0) {
			List<HierarchicalActionSet> subSets = new ArrayList<>();
			for (HierarchicalActionSet set : actionSets) {
				subSets.addAll(set.getSubActions());
				String actionStr = set.getActionString();
				int index = actionStr.indexOf("@@");
				String singleEdit = actionStr.substring(0, index).replace(" ", "");
				if (singleEdit.endsWith("SimpleName")) {
					actionStr = actionStr.substring(index + 2);
					if (actionStr.startsWith("MethodName")) {
						singleEdit = singleEdit.replace("SimpleName", "MethodName");
					} else {
						if (actionStr.startsWith("Name")) {
							actionStr = actionStr.substring(5, 6);
							if (!actionStr.equals(actionStr.toLowerCase())) {
								singleEdit = singleEdit.replace("SimpleName", "Name");
							} else {
								singleEdit = singleEdit.replace("SimpleName", "Variable");
							}
						} else {
							singleEdit = singleEdit.replace("SimpleName", "Variable");
						}
					}
				}
				editScript += singleEdit + " ";
			}
			actionSets.clear();
			actionSets.addAll(subSets);
		}
		return editScript;
	}
	
	private static void clearITree(HierarchicalActionSet actionSet) {
		actionSet.getAction().setNode(null);
		for (HierarchicalActionSet subActionSet : actionSet.getSubActions()) {
			clearITree(subActionSet);
		}
	}
}
