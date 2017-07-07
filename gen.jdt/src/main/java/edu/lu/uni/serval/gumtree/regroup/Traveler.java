package edu.lu.uni.serval.gumtree.regroup;

import java.util.ArrayList;
import java.util.List;

import com.github.gumtreediff.tree.ITree;

import edu.lu.uni.serval.gumtree.utils.ASTNodeMap;

/**
 * A traveler to travel a tree-constructed object.
 * 
 * @author kui.liu
 *
 */
public class Traveler {

	public List<List<String>> list = new ArrayList<>();
	
	/**
	 * Get all action string by traveling HierarchicalActionSet in a deep-first way.
	 *  
	 * @param actionSet
	 * @param astNodeTypeActionQueue
	 */
	public void travelActionSetDeepFirstToASTNodeQueue(HierarchicalActionSet actionSet, List<String> astNodeTypeActionQueue) {
		if (actionSet == null) {
			System.err.println("Null Action set!");
		} else {
			if (astNodeTypeActionQueue == null) {
				astNodeTypeActionQueue = new ArrayList<>();
			}
			
			String actionStr = actionSet.getActionString();
			actionStr = actionStr.substring(0, actionStr.indexOf("@@"));
			astNodeTypeActionQueue.add(actionStr); // RawToken: TODO
			
			if (actionStr.startsWith("DEL")) {
				list.add(astNodeTypeActionQueue); // FIXME BUG: Change AST node type 1 to AST node type 2. Solve method: a list is one pattern.
			} else {
				List<HierarchicalActionSet> subActionSet = actionSet.getSubActions();
				int size = subActionSet.size();
				if (size > 0) {
					for (HierarchicalActionSet subAction : subActionSet) {
						List<String> astNodeTypeActionQueue_ = new ArrayList<>();
						astNodeTypeActionQueue_.addAll(astNodeTypeActionQueue);
						travelActionSetDeepFirstToASTNodeQueue(subAction, astNodeTypeActionQueue_);
					}
				} else {
					list.add(astNodeTypeActionQueue);
				}
			}
		}
	}
	
	/**
	 * Get all AST node types of a root tree by traveling the root tree in a deep-first way.
	 * 
	 * @param root
	 * @return
	 */
	public static List<String> travelTreeDeepFirstToASTNodeQueue(ITree root) {
		if (root == null) {
			System.err.println("Null tree!");
			return null;
		}
		
		List<String> astNodeTypeQueue = new ArrayList<>();
		astNodeTypeQueue.add(ASTNodeMap.map.get(root.getType())); // RawToken: root.getLabel();
		
		List<ITree> childrenTreeList = root.getChildren();
		
		if (childrenTreeList != null && childrenTreeList.size() > 0) {
			for (ITree childTree : childrenTreeList) {
				astNodeTypeQueue.addAll(travelTreeDeepFirstToASTNodeQueue(childTree));
			}
		}
		return astNodeTypeQueue;
	}
	
	/**
	 *  Get all AST node types of a root tree by traveling the root tree in a breadth-first way.
	 *  
	 * @param root
	 * @return
	 */
	public static List<String> travelTreeBreadthFirstToASTNodeQueue(ITree root) {
		if (root == null) {
			System.err.println("Null tree.");
			return null;
		}
		
		List<String> astNodeTypeQueue = new ArrayList<>();
		astNodeTypeQueue.add(ASTNodeMap.map.get(root.getType())); // RawToken: root.getLabel();
		
		List<ITree> treeList = new ArrayList<>();
		treeList.add(root);
		while (!treeList.isEmpty()) {
			List<ITree> childrenTreeList = new ArrayList<>();
			for (ITree tree : treeList) {
				astNodeTypeQueue.addAll(travelTreeBreadthFirstToASTNodeQueue(tree));
				childrenTreeList.addAll(tree.getChildren());
			}
			
			treeList.clear();
			treeList.addAll(childrenTreeList);
		}
		return astNodeTypeQueue;
	}
	
	/**
	 * Convert a root ITree into a SimpleTree by traveling the root tree in a deep-first way.
	 * 
	 * SimpleTree node label is root.toShortString().
	 * 
	 * @param root
	 * @param parent
	 * @return
	 */
	public static SimpleTree travelITreeDeepFirstToSimpleTree(ITree root, SimpleTree parent) {
		if (root == null) {
			System.err.println("Null tree!");
			return null;
		}
		SimpleTree simpleTree = new SimpleTree();
		simpleTree.setLabel(root.toShortString());
		simpleTree.setParent(parent);
		List<SimpleTree> children = new ArrayList<>();
		
		List<ITree> childrenTreeList = root.getChildren();
		if (childrenTreeList != null && childrenTreeList.size() > 0) {
			for (ITree childTree : childrenTreeList) {
				children.add(travelITreeDeepFirstToSimpleTree(childTree, simpleTree));
			}
		}
		simpleTree.setChildren(children);
		return simpleTree;
	}
	
	/**
	 * 
	 * @param gumTreeResult
	 */
	public static void abstractBuggyTree(HierarchicalActionSet gumTreeResult) {
		SimpleTree abstractSimpleTree = abstractBuggyTree(gumTreeResult, null, null);
		SimpleTree simpleTree = buggyTree(gumTreeResult, null, null);
		
		gumTreeResult.setAbstractSimpleTree(abstractSimpleTree);
		gumTreeResult.setSimpleTree(simpleTree);
	}
	
	private static SimpleTree buggyTree(HierarchicalActionSet actionSet, ITree tree,
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
				subTrees.add(buggyTree(actionSet, child, simpleTree));
			}
			simpleTree.setChildren(subTrees);
			simpleTree.setLabel(astNode); // TODO label or astNode
		} else {
			simpleTree.setLabel(label);
		}
		
		simpleTree.setParent(parent);
		return simpleTree;
	}

	private static SimpleTree abstractBuggyTree(HierarchicalActionSet actionSet, ITree tree, SimpleTree parent) {
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

	private static HierarchicalActionSet buggyTreeAction(HierarchicalActionSet actionSet) {
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

	private static void abstractBuggyTreeDeepFirst(HierarchicalActionSet actionSet, ITree tree, SimpleTree simpleTree) {
		List<ITree> children = tree.getChildren();
		List<SimpleTree> simpleChildren = new ArrayList<>();
		if (children != null) {
			for (ITree child : children) {
				simpleChildren.add(abstractBuggyTree(actionSet, child, simpleTree));
			}
		}
		simpleTree.setChildren(simpleChildren);
		
		HierarchicalActionSet modifyAction = findHierarchicalActionSet(tree.getPos(), tree.getLength(), actionSet);

		String label = tree.getLabel();
		String astNode = ASTNodeMap.map.get(tree.getType());

		if (modifyAction == null || !modifyAction.getActionString().contains("@@" + label)) {
			if (astNode.endsWith("Type")) { // <Type, ?>
				simpleTree.setNodeType("Type");
				// simpleTree.setLabel("?");
				if (astNode.equals("WildcardType")) {
					simpleTree.setLabel("?");
				} else {
					// ArrayType, PrimitiveType, SimpleType, ParameterizedType,
					// QualifiedType, WildcardType, UnionType,NameQualifiedType,
					// IntersectionType
					simpleTree.setLabel(astNode + "@@" + label);
				}
			} else if (astNode.endsWith("Name")) {
				// variableName, methodName, QualifiedName
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
						simpleTree.setLabel("var");
					}
				} else {// variableName: <Variable, var>
					simpleTree.setNodeType("Variable");
					simpleTree.setLabel("var");
				}
			} else if (astNode.equals("BooleanLiteral")) { // Literal TODO
				simpleTree.setNodeType(astNode);
				simpleTree.setLabel(label);
			} else if (astNode.equals("CharacterLiteral")) {
				simpleTree.setNodeType(astNode);
				simpleTree.setLabel(label);
			} else if (astNode.equals("NullLiteral")) {
				simpleTree.setNodeType(astNode);
				simpleTree.setLabel("null");
			} else if (astNode.equals("NumberLiteral")) {
				simpleTree.setNodeType(astNode);
				simpleTree.setLabel(label); //"numConstant"
			} else if (astNode.equals("StringLiteral")) {
				simpleTree.setNodeType(astNode);
				simpleTree.setLabel(label);
			} else {
				if (astNode.equals("ThisExpression")) {
					simpleTree.setNodeType(astNode);
					simpleTree.setLabel("this");
				} else if (astNode.equals("Modifier") || astNode.equals("Operator")) {
					simpleTree.setNodeType(astNode);
					simpleTree.setLabel(label);
				} else if (isExpressionType(astNode)) {
					simpleTree.setNodeType("Expression");
					simpleTree.setLabel("EXP");
				} else { // other parts or elements are not abstracted.
					simpleTree.setNodeType(astNode);
					simpleTree.setLabel("");
				}
			}
		} else {
			simpleTree.setNodeType(astNode);
			if (children.size() > 0) {
				simpleTree.setLabel(astNode);
			} else {
				simpleTree.setLabel(label);
			}
		}
	}

	private static boolean isExpressionType(String astNode) {
		if (astNode.endsWith("Annotation") || astNode.equals("ArrayAccess") || astNode.equals("ArrayCreation") ||
				astNode.equals("ArrayInitializer") || astNode.equals("Assignment") || astNode.equals("CastExpression") ||
				astNode.equals("ClassInstanceCreation") || astNode.equals("ConditionalExpression") || astNode.equals("CreationReference") ||
				astNode.equals("ExpressionMethodReference") || astNode.equals("FieldAccess") || astNode.equals("InfixExpression") ||
				astNode.equals("InstanceofExpression") || astNode.equals("LambdaExpression") || astNode.equals("MethodInvocation")  ||
				astNode.equals("MethodReference") || astNode.equals("ParenthesizedExpression") || astNode.equals("PostfixExpression")  ||
				astNode.equals("PrefixExpression") || astNode.equals("SuperFieldAccess") || astNode.equals("SuperMethodInvocation")  ||
				astNode.equals("SuperMethodReference") || astNode.equals("TypeLiteral") || astNode.equals("TypeMethodReference") 
				|| astNode.equals("ThisExpression") || astNode.equals("VariableDeclarationExpression") ) {
			return true;
		}
		return false;
	}

	private static HierarchicalActionSet findHierarchicalActionSet(int position, int length, HierarchicalActionSet actionSet) {
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

}
