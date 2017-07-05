package edu.lu.uni.serval.gumtree.regroup;

import java.util.ArrayList;
import java.util.List;

public class ActionFilter {
	
	private List<String> methodNames = new ArrayList<>();
	private List<String> variableNames = new ArrayList<>();

	/**
	 * Filter out the modify actions of changing method names, method parameters, variable names and field names in declaration part.
	 * 
	 * @param actionSets
	 * @return
	 */
	public List<HierarchicalActionSet> filterOutUselessActions(List<HierarchicalActionSet> actionSets) {
		List<HierarchicalActionSet> uselessActions = findoutUselessActions(actionSets);
		actionSets.removeAll(uselessActions);
		uselessActions.clear();
		
		uselessActions = findoutUselessActionSets(actionSets, true);
		actionSets.removeAll(uselessActions);
		return actionSets;
	}

	private List<HierarchicalActionSet> findoutUselessActionSets(List<HierarchicalActionSet> actionSets, boolean isRoot) {
		List<HierarchicalActionSet> uselessActions = new ArrayList<>();
		boolean breakFor = false;
		
		for (HierarchicalActionSet actionSet : actionSets) {
			if (!isRoot) {
				String actionStr = actionSet.getActionString();
				if (actionStr.startsWith("UPD MethodInvocation")) {
					String label = actionSet.getAction().getNode().getLabel();
					for (String methodName : methodNames) {
						if (actionSet.getActionString().startsWith("UPD MethodInvocation@@" + methodName + "(") 
								|| label.contains("." + methodName + "(")) {
							addToUselessActions(actionSet, uselessActions);
							breakFor = true;
							break;
						}
					}
				} else if (actionStr.startsWith("UPD SimpleName")) {
					String label = actionSet.getAction().getNode().getLabel();
					for (String variableName : variableNames) {
						if (label.equals(variableName)) {
							addToUselessActions(actionSet, uselessActions);
							breakFor = true;
							break;
						}
					}
				}
				if (breakFor) break;
				
				List<HierarchicalActionSet> uselessActionSets = findoutUselessActionSets(actionSet.getSubActions(), false);
				if (uselessActionSets.size() > 0) {
					uselessActions.addAll(uselessActionSets);
					break;
				}
			} else {
				uselessActions.addAll(findoutUselessActionSets(actionSet.getSubActions(), false));
			}
		}
		return uselessActions;
	}

	private void addToUselessActions(HierarchicalActionSet actionSet, List<HierarchicalActionSet> uselessActions) {
		while (actionSet.getParent() != null) {
			actionSet = actionSet.getParent();
		}
		if (!uselessActions.contains(actionSet)) {
			uselessActions.add(actionSet);
		}
	}

	/**
	 * Identify the the modify actions of changing method names, method parameters, variable names and field names in declaration part.
	 * 
	 * @param actionSets
	 * @return
	 */
	private List<HierarchicalActionSet> findoutUselessActions(List<HierarchicalActionSet> actionSets) {
		List<HierarchicalActionSet> uselessActions = new ArrayList<>();
		
		for (HierarchicalActionSet actionSet : actionSets) {
			String actionType = actionSet.getAstNodeType();
			if (actionType.equals("MethodDeclaration")) {
				uselessActions.add(actionSet); // INS, MOV, DEL: useful?, UPD, except the modifier actions
				String label = actionSet.getNode().getLabel();
				String methodName = label.substring(label.indexOf("MethodName:"));
				methodName = methodName.substring(11, methodName.indexOf(","));
				methodNames.add(methodName); // "MethodName:***"
				
				// Update parameters.
				if (actionSet.getActionString().startsWith("UPD ")) {
					List<HierarchicalActionSet> subActionSets = actionSet.getSubActions();
					if (subActionSets.size() > 0) {
						for (HierarchicalActionSet subActionSet : subActionSets) {
							if (subActionSet.getActionString().startsWith("UPD SingleVariableDeclaration")) {
								List<HierarchicalActionSet> subActionSets2 = subActionSet.getSubActions(); // <Type, identifier>
								HierarchicalActionSet actSet = subActionSets2.get(subActionSets2.size() - 1);
								if (actSet.getActionString().startsWith("UPD SimpleName")) {
									String variableName = actSet.getNode().getLabel();
									variableNames.add(variableName); // "SimpleName:" + variableName TODO: effect range
								}
							}
						}
					}
				}
			} else if (actionType.equals("FieldDeclaration") || actionType.equals("VariableDeclarationStatement")) { 
				// UPD VariableDeclarationFragment
				if (actionSet.getActionString().startsWith("UPD ")) {
					List<HierarchicalActionSet> subActionSets = actionSet.getSubActions();
					if (subActionSets.size() > 0) {
						for (HierarchicalActionSet subActionSet : subActionSets) { // VariableDeclarationFragments
							if (identifyUpdateVDF(subActionSet) && !uselessActions.contains(actionSet)) {
								uselessActions.add(actionSet);
							}
						}
					}
				}
			} else if (actionType.equals("TryStatement")) {
				if (actionSet.getActionString().startsWith("UPD ")) {
					List<HierarchicalActionSet> subActionSets = actionSet.getSubActions();
					if (subActionSets.size() > 0) {
						for (HierarchicalActionSet subActionSet : subActionSets) {
							if (subActionSet.getActionString().startsWith("UPD VariableDeclarationExpression")) {
								List<HierarchicalActionSet> subActionSets2 = subActionSet.getSubActions(); // VariableDeclarationFragments
								for (HierarchicalActionSet subActionSet2 : subActionSets2) {
									if (identifyUpdateVDF(subActionSet2) && !uselessActions.contains(actionSet)) {
										uselessActions.add(actionSet);
									}
								}
							} else {
								break;
							}
						}
					}
				}
			} else if (actionType.equals("EnhancedForStatement")) { // SingleVariableDeclaration
				if (actionSet.getActionString().startsWith("UPD ")) {
					List<HierarchicalActionSet> subActionSets = actionSet.getSubActions();
					if (subActionSets.size() > 0) {
						HierarchicalActionSet subActionSet = subActionSets.get(0);
						if (subActionSet.getActionString().startsWith("UPD SingleVariableDeclaration")) {
							List<HierarchicalActionSet> subActionSets2 = subActionSet.getSubActions();
							for (HierarchicalActionSet subActionSet2 : subActionSets2) { // Type or Identifier
								if (subActionSet2.getActionString().startsWith("UPD SimpleName")) {
									String variableName = subActionSet2.getNode().getLabel();
									variableNames.add(variableName); // "SimpleName:" + variableName TODO: effect range
									if (!uselessActions.contains(actionSet)) {
										uselessActions.add(actionSet);
									}
									break;
								}
							}
						}
					}
				}
				// TODO: SingleVariableDeclaration: catch clause. lambda expression
			}
		}
		return uselessActions;
	}

	/**
	 * Identify the AST node of this ActionSet is VariableDeclarationFragment or not.
	 * And, whether the action is happened on the Variable name or not.
	 * 
	 * @param actionSet
	 */
	private boolean identifyUpdateVDF(HierarchicalActionSet actionSet) {
		if (actionSet.getActionString().startsWith("UPD VariableDeclarationFragment")) {
			List<HierarchicalActionSet> subActionSets = actionSet.getSubActions();
			if (subActionSets == null || subActionSets.size() == 0) {
				// modification of Dimension
				return true;
			}
			HierarchicalActionSet actSet = subActionSets.get(0);
			if (actSet.getActionString().startsWith("UPD SimpleName")) {
				String variableName = actSet.getNode().getLabel();
				variableNames.add(variableName); // "SimpleName:" + variableName TODO: effect range
				return true;
			}
		}
		return false;
	}

}
