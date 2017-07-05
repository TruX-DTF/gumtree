package edu.lu.uni.serval.gumtree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.lu.uni.serval.gumtree.utils.CUCreator;
import edu.lu.uni.serval.gumtree.utils.FileHelper;
import edu.lu.uni.serval.gumtree.regroup.ActionFilter;
import edu.lu.uni.serval.gumtree.regroup.HierarchicalActionSet;
import edu.lu.uni.serval.gumtree.regroup.Traveler;

public class App {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		/**
		 * Notes:
		 * Position of actions:
		 * DEL, UPD, MOV: the positions of changed source code are the positions of these source code in previous java file.
		 * INS: the positions of changed source code is the position of the source code in revised java file.
		 */
//		String a = "int a = 0;if (!isSymlink(file)) { size = size.add(BigInteger.valueOf(sizeOf(file)));}";
//		String b = "int a = 1;if (!isSymlink(file)) { size = size.add(BigInteger.valueOf(sizeOf(file)));}";
//		List<HierarchicalActionSet> gumTreeResults = compareTwoFilesWithGumTree(a, b);
//		for (HierarchicalActionSet str : gumTreeResults) {
//			System.out.println(str);
//		}
		/**
		 * TODO list:
		 * 1. Separate the modification by elements of Statements.
		 * 2. AST node sequence: AST node1 --> AST node2 --> AST node3 ...
		 * 3. Pure raw token Source code Tree --> Semi-abstract/pseudo-code tree --> Pure AST node type tree.
		 * 4. Modified/Changed Expressions.
		 */
		String testDataPath = "Dataset/commons-io/";
		File revisedFileFolder = new File(testDataPath + "revFiles/");
		File[] revisedFiles = revisedFileFolder.listFiles();
		
		System.out.println(revisedFiles.length);
		for (File revisedFile : revisedFiles) {
			if (revisedFile.toString().endsWith(".java")) {
				String revisedFileName = revisedFile.getName();
				File previousFile = new File(testDataPath + "prevFiles/prev_" + revisedFileName);
				
				List<HierarchicalActionSet> gumTreeResults = new GumTreeComparer().compareTwoFilesWithGumTree(previousFile, revisedFile);
				// Filter out modified actions of changing method names, method parameters, variable names and field names in declaration part.
				gumTreeResults = new ActionFilter().filterOutUselessActions(gumTreeResults);
				
				if (gumTreeResults.size() > 0) {
					File diffentryFile = new File(testDataPath + "DiffEntries/" + revisedFileName.replace(".java", ".txt"));
		            StringBuilder builder = new StringBuilder();
		            builder.append(FileHelper.readFile(diffentryFile) + "\n");
		            StringBuilder astNodeBuilder = new StringBuilder();
		            astNodeBuilder.append(FileHelper.readFile(diffentryFile) + "\n");
		            StringBuilder rawCodeBuilder = new StringBuilder();
		            rawCodeBuilder.append(FileHelper.readFile(diffentryFile) + "\n");
		            
		            CUCreator cuCreator = new CUCreator();
		            CompilationUnit prevUnit = cuCreator.createCompilationUnit(previousFile);
		            CompilationUnit revUnit = cuCreator.createCompilationUnit(revisedFile);
		            
		            for (HierarchicalActionSet gumTreeResult : gumTreeResults) {
		            	// set line numbers
		            	String actionStr = gumTreeResult.getActionString();
		            	CompilationUnit unit;
		            	if (actionStr.startsWith("INS")) {
		            		unit = revUnit;
		            	} else {
		            		unit = prevUnit;
		            	}
	            		int position = gumTreeResult.getStartPosition();
	            		gumTreeResult.setStartLineNum(unit.getLineNumber(position));
	            		gumTreeResult.setEndLineNum(unit.getLineNumber(position + gumTreeResult.getLength()));
		            	
	            		// convert the ITree of buggy code to a simple tree.
	            		Traveler.abstractBuggyTree(gumTreeResult);
	            		
	            		builder.append("@@ " + gumTreeResult.getStartLineNum() + " -- " + gumTreeResult.getEndLineNum() + "\n");
		            	builder.append(gumTreeResult.getRawTokenTree().toString() + "\n");
		            	builder.append(gumTreeResult.getSimpleTree().toString() + "\n");
		            	builder.append(gumTreeResult.getAstNodeTree().toString() + "\n");
		            	builder.append(gumTreeResult + "\n");
		            	
		            	astNodeBuilder.append(gumTreeResult.toASTNodeLevelAction() + "\n");
		            	Traveler traveller = new Traveler();
		            	traveller.travelActionSetDeepFirstToASTNodeQueue(gumTreeResult, null);
		            	List<List<String>> list = traveller.list;
		            	for (List<String> l : list) {
		            		astNodeBuilder.append(l.toString() + "\n");
		            	}
		            	astNodeBuilder.append("\n");
		            	
		            	rawCodeBuilder.append(gumTreeResult.toRawCodeLevelAction() + "\n");
		            }
		            
		            FileHelper.outputToFile("OUTPUT/GumTreeResults_Exp/" + revisedFileName.replace(".java", ".txt"), builder, false);
		            FileHelper.outputToFile("OUTPUT/GumTreeResults_Exp_ASTNode/" + revisedFileName.replace(".java", ".txt"), astNodeBuilder, false);
		            FileHelper.outputToFile("OUTPUT/GumTreeResults_Exp_RawCode/" + revisedFileName.replace(".java", ".txt"), rawCodeBuilder, false);
				}
			}
		}
	}
	
}
