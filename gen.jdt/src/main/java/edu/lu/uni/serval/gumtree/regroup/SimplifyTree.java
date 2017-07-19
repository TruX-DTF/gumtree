package edu.lu.uni.serval.gumtree.regroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gumtreediff.tree.ITree;

import edu.lu.uni.serval.gumtree.utils.ASTNodeMap;

public class SimplifyTree {
	
	private static final String ABSTRACT_TYPE = "T";
	private static final String ABSTRACT_NAME = "N";
	private static final String ABSTRACT_METHOD = "m";
	private static final String ABSTRACT_VARIABLE = "v";
	
	private Map<String, String> abstractTypeIdentifiers = new HashMap<>();
	private Map<String, String> abstractMethodIdentifiers = new HashMap<>();
	private Map<String, String> abstractNameIdentifiers = new HashMap<>();
	private Map<String, String> abstractVariableIdentifiers = new HashMap<>();
	
	/**
	 * Convert ITree to a source code simple tree, an abstract identifier simple tree, and a semi-source code simple tree.
	 * 
	 * @param gumTreeResult
	 */
	public void abstractTree(HierarchicalActionSet gumTreeResult) {
		SimpleTree sourceCodeSimpleTree = sourceCodeTree(gumTreeResult, null, null);            // source code tree and AST node type tree
		SimpleTree abstractIdentifierTree = abstractIdentifierTree(gumTreeResult, null, null);  // abstract identifier tree
		SimpleTree abstractSimpleTree = semiSourceCodeTree(gumTreeResult, null, null);          // semi-source code tree. and AST node type tree
		
		gumTreeResult.setAbstractSimpleTree(abstractSimpleTree);
		gumTreeResult.setAbstractIdentifierTree(abstractIdentifierTree);
		gumTreeResult.setSimpleTree(sourceCodeSimpleTree);
	}
	
	private SimpleTree sourceCodeTree(HierarchicalActionSet actionSet, ITree tree,
			SimpleTree parent) {
		if (parent == null){
			actionSet = buggyTreeAction(actionSet);
			if (actionSet == null) {
				return null;
			}
			tree = actionSet.getNode();
		}
		SimpleTree simpleTree = new SimpleTree();

		String label = tree.getLabel();
		String astNode = ASTNodeMap.map.get(tree.getType());

		simpleTree.setNodeType(astNode);
		List<ITree> children = tree.getChildren();
		if (children.size() > 0) {
			List<SimpleTree> subTrees = new ArrayList<>();
			for (ITree child : children) {
				subTrees.add(sourceCodeTree(actionSet, child, simpleTree));
			}
			simpleTree.setChildren(subTrees);
			simpleTree.setLabel(astNode);
		} else {
			simpleTree.setLabel(label);
		}
		
		simpleTree.setParent(parent);
		return simpleTree;
	}

	private SimpleTree abstractIdentifierTree(HierarchicalActionSet actionSet, ITree tree, SimpleTree parent) {
		if (parent == null){
			actionSet = buggyTreeAction(actionSet);
			if (actionSet == null) {
				return null;
			}
			tree = actionSet.getNode();
		}
		SimpleTree simpleTree = new SimpleTree();

		String label = tree.getLabel();
		String astNode = ASTNodeMap.map.get(tree.getType());

		simpleTree.setNodeType(astNode);
		List<ITree> children = tree.getChildren();
		if (children.size() > 0) {
			if (astNode.endsWith("Type")) {
				simpleTree.setNodeType("Type");
				if (astNode.equals("WildcardType")) {
					simpleTree.setLabel("?");
				} else {
					simpleTree.setLabel(getAbstractLabel(abstractTypeIdentifiers, label, ABSTRACT_TYPE)); // abstract Type identifier
				}
			} else {
				List<SimpleTree> subTrees = new ArrayList<>();
				for (ITree child : children) {
					subTrees.add(sourceCodeTree(actionSet, child, simpleTree));
				}
				simpleTree.setChildren(subTrees);
				simpleTree.setLabel(astNode);
			}
		} else {
			if (astNode.endsWith("Type")) {
				simpleTree.setNodeType("Type");
				if (astNode.equals("WildcardType")) {
					simpleTree.setLabel("?");
				} else {
					simpleTree.setLabel(getAbstractLabel(abstractTypeIdentifiers, label, ABSTRACT_TYPE)); // abstract Type identifier
				}
			} else if (astNode.endsWith("Name")) {
				// variableName, methodName, QualifiedName
				if (label.startsWith("MethodName:")) { // <Method, name>
					label = label.substring(11);
					simpleTree.setNodeType("Method");
					simpleTree.setLabel(getAbstractLabel(abstractMethodIdentifiers, label, ABSTRACT_METHOD)); // abstract method identifier
				} else if (label.startsWith("Name:")) {
					label = label.substring(5);
					String firstChar = label.substring(0, 1);
					if (firstChar.equals(firstChar.toUpperCase())) {
						simpleTree.setNodeType("Name");
						simpleTree.setLabel(getAbstractLabel(abstractNameIdentifiers, label, ABSTRACT_NAME)); // abstract Name identifier
					} else {// variableName: <Variable, var>
						simpleTree.setNodeType("Variable");
						simpleTree.setLabel(getAbstractLabel(abstractVariableIdentifiers, label, ABSTRACT_VARIABLE));// abstract Variable identifier
					}
				} else {// variableName: <Variable, var>
					simpleTree.setNodeType("Variable");
					simpleTree.setLabel(getAbstractLabel(abstractVariableIdentifiers, label, ABSTRACT_VARIABLE));// abstract Variable identifier
				}
			} else if (astNode.equals("BooleanLiteral") || astNode.equals("CharacterLiteral") || astNode.equals("NullLiteral")
						|| astNode.equals("NumberLiteral") || astNode.equals("StringLiteral") || astNode.equals("ThisExpression")
						|| astNode.equals("Modifier") || astNode.equals("Operator")) {
				simpleTree.setNodeType(astNode);
				simpleTree.setLabel(label);
			}
		}
		
		simpleTree.setParent(parent);
		return simpleTree;
	}

	private SimpleTree semiSourceCodeTree(HierarchicalActionSet actionSet, ITree tree, SimpleTree parent) {
		if (parent == null){
			actionSet = buggyTreeAction(actionSet);
			if (actionSet == null) {
				return null;
			}
			tree = actionSet.getNode();
		}
		SimpleTree simpleTree = new SimpleTree();
		simpleTree.setParent(parent);
		// deep first
		abstractBuggyTreeDeepFirst(actionSet, tree, simpleTree);
		
		return simpleTree;
	}
	
	private void abstractBuggyTreeDeepFirst(HierarchicalActionSet actionSet, ITree tree, SimpleTree simpleTree) {
		List<ITree> children = tree.getChildren();
		HierarchicalActionSet modifyAction = findHierarchicalActionSet(tree.getPos(), tree.getLength(), actionSet);
		String label = tree.getLabel();
		String astNode = ASTNodeMap.map.get(tree.getType());

		if (isExpressionType(astNode)) {
			if (modifyAction == null || !modifyAction.getActionString().contains("@@" + label)) {
				simpleTree.setNodeType("Expression");
				simpleTree.setLabel("EXP"); // astNode
			}
		} else {
			if (astNode.endsWith("Type")) { // <Type, ?> TODO: sub Type
				simpleTree.setNodeType("Type");
				// simpleTree.setLabel("?");
				if (astNode.equals("WildcardType")) {
					simpleTree.setLabel("?");
				} else { // ArrayType, PrimitiveType, SimpleType, ParameterizedType, QualifiedType, WildcardType, UnionType,NameQualifiedType, IntersectionType
					simpleTree.setLabel(astNode + "@@" + label);
				}
			} else if (astNode.endsWith("Name")) { // variableName, methodName, QualifiedName
				if (label.startsWith("MethodName:")) { // <Method, name>
					label = label.substring(11);
					simpleTree.setNodeType("Method");
					simpleTree.setLabel(label);
				} else if (label.startsWith("Name:")) {
					label = label.substring(5);
					String firstChar = label.substring(0, 1);
					if (firstChar.equals(firstChar.toUpperCase())) {
						simpleTree.setNodeType("Name");
						simpleTree.setLabel(label); // <Name, name>
					} else {// variableName: <Variable, var>
						simpleTree.setNodeType("Variable");
						simpleTree.setLabel(getAbstractLabel(abstractVariableIdentifiers, label, ABSTRACT_VARIABLE));
					}
				} else {// variableName: <Variable, var>
					simpleTree.setNodeType("Variable");
					simpleTree.setLabel(getAbstractLabel(abstractVariableIdentifiers, label, ABSTRACT_VARIABLE));
				}
			} else if (astNode.equals("BooleanLiteral") ||astNode.equals("CharacterLiteral") || astNode.equals("ThisExpression")
						|| astNode.equals("NullLiteral") || astNode.equals("NumberLiteral") || astNode.equals("StringLiteral")
						|| astNode.equals("Modifier") || astNode.equals("Operator")) {
				simpleTree.setNodeType(astNode);
				simpleTree.setLabel(label);
			} else {
				simpleTree.setNodeType(astNode);
				simpleTree.setLabel(astNode);
			}
		}
		
		List<SimpleTree> simpleChildren = new ArrayList<>();
		if (children != null && !astNode.endsWith("Type")) {
			for (ITree child : children) {
				simpleChildren.add(semiSourceCodeTree(actionSet, child, simpleTree));
			}
		}
		simpleTree.setChildren(simpleChildren);
	}

	private HierarchicalActionSet buggyTreeAction(HierarchicalActionSet actionSet) {
		if (actionSet.getActionString().startsWith("INS")) {
			List<HierarchicalActionSet> subActions = actionSet.getSubActions();
			HierarchicalActionSet subActionSet = null;
			SubAction: {
				while (subActions.size() > 0) { // find the non-INSERT action in a bread-first traveling way.
					List<HierarchicalActionSet> subActions2 = new ArrayList<>();
					for (HierarchicalActionSet subAction : subActions) {
						if (!subAction.getActionString().startsWith("INS")) {
							subActionSet = subAction; // FIXME: e.g. add a TryStatement as the parent of multiple statements.
							break SubAction;
						}
						subActions2.addAll(subAction.getSubActions());
					}
					subActions.clear();
					subActions.addAll(subActions2);
				}
			}
			
			if (subActionSet == null) {
				return null;
			} else {
				actionSet = subActionSet;
			}
		}
		
		// FIXME: pure INS action
		return actionSet;
	}

	private String getAbstractLabel(Map<String, String> map, String label, String nameType) {
		if (map.containsKey(label)) {
			return map.get(label);
		} else {
			String name = nameType + map.size();
			map.put(label, name);
			return name;
		}
	}

	private boolean isExpressionType(String astNode) {
		if (astNode.equals("ArrayAccess") || astNode.equals("ArrayCreation") ||
				astNode.equals("ArrayInitializer") || astNode.equals("Assignment") || astNode.equals("CastExpression") ||
				astNode.equals("ClassInstanceCreation") || astNode.equals("ConditionalExpression") || astNode.equals("CreationReference") ||
				astNode.equals("ExpressionMethodReference") || astNode.equals("FieldAccess") || astNode.equals("InfixExpression") ||
				astNode.equals("InstanceofExpression") || astNode.equals("LambdaExpression") || astNode.equals("MethodInvocation")  ||
				astNode.equals("MethodReference") || astNode.equals("ParenthesizedExpression") || astNode.equals("PostfixExpression")  ||
				astNode.equals("PrefixExpression") || astNode.equals("SuperFieldAccess") || astNode.equals("SuperMethodInvocation")  ||
				astNode.equals("SuperMethodReference") || astNode.equals("TypeLiteral") || astNode.equals("TypeMethodReference") 
				|| astNode.equals("VariableDeclarationExpression") ) {
			return true;
		}
		return false;
	}

	private HierarchicalActionSet findHierarchicalActionSet(int position, int length, HierarchicalActionSet actionSet) {
		if (actionSet.getStartPosition() == position && actionSet.getLength() == length && !actionSet.getActionString().startsWith("INS")) {
			return actionSet;
		} else {
			for (HierarchicalActionSet subActionSet : actionSet.getSubActions()) {
				HierarchicalActionSet actSet = findHierarchicalActionSet(position, length, subActionSet);
				if (actSet != null) {
					return actSet;
				}
			}
		}
		return null;
	}

	public Map<String, String> getAbstractTypeIdentifiers() {
		return abstractTypeIdentifiers;
	}

	public Map<String, String> getAbstractMethodIdentifiers() {
		return abstractMethodIdentifiers;
	}

	public Map<String, String> getAbstractNameIdentifiers() {
		return abstractNameIdentifiers;
	}

	public Map<String, String> getAbstractVariableIdentifiers() {
		return abstractVariableIdentifiers;
	}

}
