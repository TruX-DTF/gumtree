package edu.lu.uni.serval.gumtree.regroup;

import java.util.ArrayList;
import java.util.List;

import com.github.gumtreediff.actions.model.Action;

/**
 * Hierarchical-level results of GumTree results
 * 
 * @author kui.liu
 *
 */
public class HierarchicalActionSet implements Comparable<HierarchicalActionSet> {
	
	private String astNodeType;
	private Action action;
	private Action parentAction;
	private String actionString;
	private List<HierarchicalActionSet>	subActions = new ArrayList<>();

	public String getAstNodeType() {
		return astNodeType;
	}

	public void setAstNodeType(String astNodeType) {
		this.astNodeType = astNodeType;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Action getParentAction() {
		return parentAction;
	}

	public void setParentAction(Action parentAction) {
		this.parentAction = parentAction;
	}

	public String getActionString() {
		return actionString;
	}

	public void setActionString(String actionString) {
		this.actionString = actionString;
	}

	public List<HierarchicalActionSet> getSubActions() {
		return subActions;
	}

	public void setSubActions(List<HierarchicalActionSet> subActions) {
		this.subActions = subActions;
	}

	@Override
	public int compareTo(HierarchicalActionSet o) {
		return this.action.compareTo(o.action);
	}
	
	private List<String> strList = new ArrayList<>();

	@Override
	public String toString() {
		String str = actionString;//parseAction(action.toString());
		strList.add(str);
		for (HierarchicalActionSet actionSet : subActions) {
			actionSet.toString();
			List<String> strList1 = actionSet.strList;
			for (String str1 : strList1) {
				strList.add("----" + str1);
			}
		}
		
		str = "";
		for (String str1 : strList) {
			str += str1 + "\n";
		}
		 
		return str;
	}
	
	public String toASTNodeLevelAction() {
//		strList.clear();
//		toString();
		String astNodeStr = "";
		for (String str : strList) {
			astNodeStr += str.substring(0, str.indexOf("@@")) + "\n";
		}
		return astNodeStr;
	}
	
	public String toRawCodeLevelAction() {
//		strList.clear();
//		toString();
		String astNodeStr = "";
		for (String str : strList) {
			str = str.substring(0, str.indexOf(" @AT@")) + "\n";
			int index1 = str.indexOf(" ") + 1;
			int index2 = str.indexOf("@@") + 2;
			astNodeStr += str.substring(0, index1) + str.substring(index2);
		}
		return astNodeStr;
	}
}
