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
		ITree tree = gumTreeResult.getNode();
		SimpleTree simpleTree = abstractBuggyTree(gumTreeResult, tree, null);
		SimpleTree rawTokenTree = abstractRawTokenBuggyTree(gumTreeResult, tree, null);
		SimpleTree astNodeTree = abstractASTNodeBuggyTree(gumTreeResult, tree, null);
		
		gumTreeResult.setSimpleTree(simpleTree);
		gumTreeResult.setRawTokenTree(rawTokenTree);
		gumTreeResult.setAstNodeTree(astNodeTree);
	}
	
	private static SimpleTree abstractASTNodeBuggyTree(HierarchicalActionSet gumTreeResult, ITree tree, SimpleTree parent) {
		SimpleTree simpleTree = new SimpleTree();

		String astNode = ASTNodeMap.map.get(tree.getType());
		simpleTree.setLabel(astNode);

		List<ITree> children = tree.getChildren();
		if (children.size() > 0) {
			List<SimpleTree> subTrees = new ArrayList<>();
			for (ITree child : children) {
				subTrees.add(abstractASTNodeBuggyTree(gumTreeResult, child, simpleTree));
			}
			simpleTree.setChildren(subTrees);
		}
		
		simpleTree.setParent(parent);
		return simpleTree;
	}

	private static SimpleTree abstractRawTokenBuggyTree(HierarchicalActionSet gumTreeResult, ITree tree,
			SimpleTree parent) {
		SimpleTree simpleTree = new SimpleTree();

		String label = tree.getLabel();
		String astNode = ASTNodeMap.map.get(tree.getType());

		List<ITree> children = tree.getChildren();
		if (children.size() > 0) {
			List<SimpleTree> subTrees = new ArrayList<>();
			for (ITree child : children) {
				subTrees.add(abstractRawTokenBuggyTree(gumTreeResult, child, simpleTree));
			}
			simpleTree.setChildren(subTrees);
			simpleTree.setLabel(astNode);
		} else {
			simpleTree.setLabel(label);
		}
		
		simpleTree.setParent(parent);
		return simpleTree;
	}

	private static SimpleTree abstractBuggyTree(HierarchicalActionSet gumTreeResult, ITree tree, SimpleTree parent) {
		SimpleTree simpleTree = new SimpleTree();
		// breadth first travel
		
		
		// deep first travel
		int position = tree.getPos();
		HierarchicalActionSet modifyAction = findHierarchicalActionSet(position, gumTreeResult);
		// Change of Statement Type
		// Change of Statement element
		// Change of expression
		// INS actions TODO

		String label = tree.getLabel();
		String astNode = ASTNodeMap.map.get(tree.getType());
		simpleTree.setParent(parent);
		
		
		if (modifyAction == null || !modifyAction.getActionString().contains("@@" + label)) {
			if (astNode.endsWith("Type")) {
				if (astNode.equals("WildcardType")) {
					simpleTree.setLabel("?");
				} else {
// ArrayType, PrimitiveType, SimpleType, ParameterizedType, QualifiedType, WildcardType, UnionType,NameQualifiedType, IntersectionType
					simpleTree.setLabel(astNode + "@@" + label);
				}
			} else if (astNode.endsWith("Name")) {
				// variableName, methodName, QualifiedName
				if (label.startsWith("MethodName:")) {
					label = label.substring(11);
					simpleTree.setLabel(label);
				} else if (label.startsWith("Name:")) {
					label = label.substring(5);
					String firstChar = label.substring(0, 1);
					if (firstChar.equals(firstChar.toUpperCase())) {
						simpleTree.setLabel(label);
					} else {// variableName
						simpleTree.setLabel("var");
					}
				} else {// variableName
					simpleTree.setLabel("var");
				}
			} else if (astNode.equals("BooleanLiteral")) {
				simpleTree.setLabel(label);
			} else if (astNode.equals("CharacterLiteral")) {
				simpleTree.setLabel("chConstant");
			} else if (astNode.equals("NullLiteral")) {
				simpleTree.setLabel("null");
			} else if (astNode.equals("NumberLiteral")) {
				simpleTree.setLabel("numConstant");
			} else if (astNode.equals("StringLiteral")) {
				simpleTree.setLabel("strConstant");
			} else {
				// SingleVariableDeclaration, VariableDeclarationFragment
				// CatchClause, BreakStatement+label,ContinueStatement+label,
				// element or expressions
				if (astNode.equals("ThisExpression")) {
					simpleTree.setLabel("this");
				} else if (astNode.equals("Modifier") || astNode.equals("Operator")) {
					simpleTree.setLabel(label);
				} else
				if (isExpressionType(astNode)) {
					simpleTree.setLabel("EXP");
				} else {
					simpleTree.setLabel(astNode);
				}
			}
		} else {
			simpleTree.setLabel(astNode + "@@" + label);
			simpleTree.setParent(parent);

			List<ITree> children = tree.getChildren();
			if (children.size() > 0) {
				List<SimpleTree> subTrees = new ArrayList<>();
				for (ITree child : children) {
					subTrees.add(abstractBuggyTree(gumTreeResult, child, simpleTree));
				}
				simpleTree.setChildren(subTrees);
			}
		}
		
		return simpleTree;
	}

	private static boolean isExpressionType(String astNode) {
		if (astNode.equals("Annotation") || astNode.equals("ArrayAccess") || astNode.equals("ArrayCreation") ||
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

	private static HierarchicalActionSet findHierarchicalActionSet(int position, HierarchicalActionSet gumTreeResult) {
		if (gumTreeResult.getStartPosition() == position) {
			return gumTreeResult;
		} else {
			for (HierarchicalActionSet subActionSet : gumTreeResult.getSubActions()) {
				return findHierarchicalActionSet(position, subActionSet);
			}
		}
		return null;
	}

}
