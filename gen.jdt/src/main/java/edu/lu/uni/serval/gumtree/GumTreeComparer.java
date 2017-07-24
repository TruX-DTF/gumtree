package edu.lu.uni.serval.gumtree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.ITree;

import edu.lu.uni.serval.gumtree.GumTreeGenerator.GumTreeType;
import edu.lu.uni.serval.gumtree.regroup.HierarchicalActionSet;
import edu.lu.uni.serval.gumtree.regroup.HierarchicalRegouper;
import edu.lu.uni.serval.gumtree.regroup.SimpleTree;
import edu.lu.uni.serval.gumtree.regroup.Traveler;

public class GumTreeComparer {

	public List<HierarchicalActionSet> compareTwoCodeBlocksWithGumTree(String oldCodeBlock, String newCodeBlock) {
		List<HierarchicalActionSet> actionSets = new ArrayList<>();

		// Generate GumTree.
		ITree oldTree = new GumTreeGenerator().generateITreeForCodeBlock(oldCodeBlock, GumTreeType.EXP_JDT);
		ITree newTree = new GumTreeGenerator().generateITreeForCodeBlock(newCodeBlock, GumTreeType.EXP_JDT);
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
	
	public List<HierarchicalActionSet> compareTwoFilesWithGumTree(File prevFile, File revFile) {
		List<HierarchicalActionSet> actionSets = new ArrayList<>();
		
		// Generate GumTree.
		ITree oldTree = null;
		ITree newTree = null;
		try {
			oldTree = new GumTreeGenerator().generateITreeForJavaFile(prevFile, GumTreeType.EXP_JDT);
			newTree = new GumTreeGenerator().generateITreeForJavaFile(revFile, GumTreeType.EXP_JDT);
		} catch (Exception e) {
			if (oldTree == null) {
				System.out.println("Previous File: " + prevFile.getPath());
			} else if (newTree == null) {
				System.out.println("Revised File: " + revFile.getPath());
			}
			e.printStackTrace();
		}
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
