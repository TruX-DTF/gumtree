package edu.lu.uni.serval.gumtree.regroup;

import java.util.ArrayList;
import java.util.List;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Addition;
import com.github.gumtreediff.tree.ITree;

import edu.lu.uni.serval.gumtree.ListSorter;

/**
 * Regroup GumTree results to a hierarchical construction.
 * 
 * @author kui.liu
 *
 */
public class HierarchicalRegouper {
	
	public static List<HierarchicalActionSet> regroupGumTreeResults(List<Action> actions) {
		List<HierarchicalActionSet> actionSets = new ArrayList<>();
		
		/*
		 * First, sort actions by their positions.
		 */
		ListSorter<Action> sorter = new ListSorter<>(actions);
		actions = sorter.sortAscending();
		HierarchicalActionSet actionSet = null;
		
		/*
		 * Second, group actions by their positions.
		 */
		for(Action act : actions){
			Action parentAct = findParentAction(act, actions);
			if (parentAct == null) {
				actionSet = new HierarchicalActionSet();
				actionSet.setAction(act);
				actionSet.setParentAction(parentAct);
				actionSets.add(actionSet);
			} else {
				if (!addToAactionSet(act, parentAct, actionSets)) {
					// The index of the parent action in the actions' list is larger than the index of this action.
					actionSet = new HierarchicalActionSet();
					actionSet.setAction(act);
					actionSet.setParentAction(parentAct);
					actionSets.add(actionSet);
				}
			}
		}
		
		/*
		 * Third, add the subActionSet to its parent ActionSet.
		 */
		List<HierarchicalActionSet> reActionSets = new ArrayList<>();
		for (HierarchicalActionSet actSet : actionSets) {
			Action parentAct = actSet.getParentAction();
			if (parentAct != null) {
				addToActionSets(actSet, parentAct, actionSets);
			} else {
				reActionSets.add(actSet);
			}
		}
		
		return reActionSets;
	}

	private static void addToActionSets(HierarchicalActionSet actionSet, Action parentAct, List<HierarchicalActionSet> actionSets) {
		for (HierarchicalActionSet actSet : actionSets) {
			if (actSet.equals(actionSet)) continue;
			if (actSet.getAction().equals(parentAct)) {
				actSet.getSubActions().add(actionSet);
				ListSorter<HierarchicalActionSet> sorter = new ListSorter<HierarchicalActionSet>(actSet.getSubActions());
				actSet.setSubActions(sorter.sortAscending());
				break;
			} else {
				addToActionSets(actionSet, parentAct, actSet.getSubActions());
			}
		}
	}

	private static boolean addToAactionSet(Action act, Action parentAct, List<HierarchicalActionSet> actionSets) {
		ITree parentTree = parentAct.getNode();
		for(HierarchicalActionSet actionSet : actionSets) {
			ITree tree = actionSet.getAction().getNode();
			if (tree.equals(parentTree)) {
				HierarchicalActionSet actSet = new HierarchicalActionSet();
				actSet.setAction(act);
				actionSet.getSubActions().add(actSet);
				return true;
			} else {
				List<HierarchicalActionSet> subActionSets = actionSet.getSubActions();
				if (subActionSets.size() > 0) {
					boolean added = addToAactionSet(act, parentAct, subActionSets);
					if (added) {
						return true;
					} else {
						continue;
					}
				}
			}
		}
		return false;
	}

	private static Action findParentAction(Action action, List<Action> actions) {
		
		ITree parent = action.getNode().getParent();
		if (action instanceof Addition) {
			parent = ((Addition) action).getParent();
		}
		String label = parent.getLabel();
		for (Action act : actions) {
			ITree actNode = act.getNode();
			if (actNode.equals(parent)) {
				return act;
			} else {
				if (label.equals(actNode.getLabel())) {
					return act;
				}
			}
		}
		return null;
	}
}
