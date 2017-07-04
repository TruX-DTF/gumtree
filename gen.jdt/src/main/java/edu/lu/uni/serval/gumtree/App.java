package edu.lu.uni.serval.gumtree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;

import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.ITree;

import edu.lu.uni.serval.gumtree.utils.CUCreator;
import edu.lu.uni.serval.gumtree.utils.FileHelper;
import edu.lu.uni.serval.gumtree.GumTreeGenerator.GumTreeType;
import edu.lu.uni.serval.gumtree.regroup.HierarchicalActionSet;
import edu.lu.uni.serval.gumtree.regroup.HierarchicalRegouper;
import edu.lu.uni.serval.gumtree.regroup.SimpleTree;
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
		 * 2. AST node1 --> AST node2 --> AST node3 ...
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
				
				List<HierarchicalActionSet> gumTreeResults = compareTwoFilesWithGumTree(previousFile, revisedFile);
				if (gumTreeResults.size() > 0) {
					// TODO:
					// Filter out modified actions of method declaration which contains change method names or change parameters.
					// Filter out modified actions of variable declaration which changes variable name.
					// Filter out modified actions of field declaration which changes field name.
					
					
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
	
	@SuppressWarnings("unused")
	private static List<HierarchicalActionSet> compareTwoFilesWithGumTree(String a, String b) {
		List<HierarchicalActionSet> actionSets = new ArrayList<>();

		// Generate GumTree.
		ITree oldTree = GumTreeGenerator.generateITreeForCodeBlock(a, GumTreeType.EXP_JDT);
		ITree newTree = GumTreeGenerator.generateITreeForCodeBlock(b, GumTreeType.EXP_JDT);
		if (oldTree != null && newTree != null) {
			if (oldTree.isIsomorphicTo(newTree)) { // TODO: this method should be improved.
				System.out.println(true);
			}

			SimpleTree simpleTree = Traveler.travelITreeDeepFirstToSimpleTree(oldTree, null);
			System.out.println(simpleTree.toString() + "\n");
			
			Matcher m = Matchers.getInstance().getMatcher(oldTree, newTree);
			m.match();
			ActionGenerator ag = new ActionGenerator(oldTree, newTree, m.getMappings());
			ag.generate();
			List<Action> actions = ag.getActions(); // change actions from bug to patch

			// Regroup GumTree results
			actionSets = HierarchicalRegouper.regroupGumTreeResults(actions);
		}
		
		return actionSets;
	}
	
	public static List<HierarchicalActionSet> compareTwoFilesWithGumTree(File prevFile, File revFile) {
		List<HierarchicalActionSet> actionSets = new ArrayList<>();
		
		// Generate GumTree.
		ITree oldTree = GumTreeGenerator.generateITreeForJavaFile(prevFile, GumTreeType.EXP_JDT);
		ITree newTree = GumTreeGenerator.generateITreeForJavaFile(revFile, GumTreeType.EXP_JDT);
		if (oldTree != null && newTree != null) {
			Matcher m = Matchers.getInstance().getMatcher(oldTree, newTree);
			m.match();
			ActionGenerator ag = new ActionGenerator(oldTree, newTree, m.getMappings());
			ag.generate();
			List<Action> actions = ag.getActions(); // change actions from bug to patch
			
			// Regroup GumTree results
			actionSets = HierarchicalRegouper.regroupGumTreeResults(actions);
		}

		return actionSets;
	}
	
}
