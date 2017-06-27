package edu.lu.uni.serval.gumtree.regroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gumtreediff.actions.model.Action;

public class ActionSet {
	
	private String astNodeType;
	private Action action;
	private List<ActionSet>	subActions = new ArrayList<>();

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

	public List<ActionSet> getSubActions() {
		return subActions;
	}

	public void setSubActions(List<ActionSet> subActions) {
		this.subActions = subActions;
	}
	
	private List<String> strList = new ArrayList<>();

	@Override
	public String toString() {
		String str = parseAction(action.toString());
		strList.add(str);
		for (ActionSet actionSet : subActions) {
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
	
	private String parseAction(String actStr) {
		// UPD 25@@!a from !a to isTrue(a) at 69
		String[] actStrArrays = actStr.split("@@");
		actStr = "";
		int length = actStrArrays.length;
		for (int i =0; i < length - 1; i ++) {
			String actStrFrag = actStrArrays[i];
			int index = actStrFrag.lastIndexOf(" ") + 1;
			String nodeType = actStrFrag.substring(index);
			nodeType = ASTNodeMapping.get(Integer.parseInt(nodeType));
			actStrFrag = actStrFrag.substring(0, index) + nodeType + "@@";
			actStr += actStrFrag;
		}
		actStr += actStrArrays[length - 1];
		return actStr;
	}
	
	private static Map<Integer, String> ASTNodeMapping;
	
	static {
		ASTNodeMapping  = new HashMap<Integer, String>();
		ASTNodeMapping.put(0, "ASTNode");
		ASTNodeMapping.put(1, "AnonymousClassDeclaration");
		ASTNodeMapping.put(2, "ArrayAccess");
		ASTNodeMapping.put(3, "ArrayCreation");
		ASTNodeMapping.put(4, "ArrayInitializer");
		ASTNodeMapping.put(5, "ArrayType");
		ASTNodeMapping.put(6, "AssertStatement");
		ASTNodeMapping.put(7, "Assignment");
		ASTNodeMapping.put(8, "Block");
		ASTNodeMapping.put(9, "BooleanLiteral");
		ASTNodeMapping.put(10, "BreakStatement");
		ASTNodeMapping.put(11, "CastExpression");
		ASTNodeMapping.put(12, "CatchClause");
		ASTNodeMapping.put(13, "CharacterLiteral");
		ASTNodeMapping.put(14, "ClassInstanceCreation");
		ASTNodeMapping.put(15, "CompilationUnit");
		ASTNodeMapping.put(16, "ConditionalExpression");
		ASTNodeMapping.put(17, "ConstructorInvocation");
		ASTNodeMapping.put(18, "ContinueStatement");
		ASTNodeMapping.put(19, "DoStatement");
		ASTNodeMapping.put(20, "EmptyStatement");
		ASTNodeMapping.put(21, "ExpressionStatement");
		ASTNodeMapping.put(22, "FieldAccess");
		ASTNodeMapping.put(23, "FieldDeclaration");
		ASTNodeMapping.put(24, "ForStatement");
		ASTNodeMapping.put(25, "IfStatement");
		ASTNodeMapping.put(26, "ImportDeclaration");
		ASTNodeMapping.put(27, "InfixExpression");
		ASTNodeMapping.put(28, "Initializer");
		ASTNodeMapping.put(29, "Javadoc");
		ASTNodeMapping.put(30, "LabeledStatement");
		ASTNodeMapping.put(31, "MethodDeclaration");
		ASTNodeMapping.put(32, "MethodInvocation");
		ASTNodeMapping.put(33, "NullLiteral");
		ASTNodeMapping.put(34, "NumberLiteral");
		ASTNodeMapping.put(35, "PackageDeclaration");
		ASTNodeMapping.put(36, "ParenthesizedExpression");
		ASTNodeMapping.put(37, "PostfixExpression");
		ASTNodeMapping.put(38, "PrefixExpression");
		ASTNodeMapping.put(39, "PrimitiveType");
		ASTNodeMapping.put(40, "QualifiedName");
		ASTNodeMapping.put(41, "ReturnStatement");
		ASTNodeMapping.put(42, "SimpleName");
		ASTNodeMapping.put(43, "SimpleType");
		ASTNodeMapping.put(44, "SingleVariableDeclaration");
		ASTNodeMapping.put(45, "StringLiteral");
		ASTNodeMapping.put(46, "SuperConstructorInvocation");
		ASTNodeMapping.put(47, "SuperFieldAccess");
		ASTNodeMapping.put(48, "SuperMethodInvocation");
		ASTNodeMapping.put(49, "SwitchCase");
		ASTNodeMapping.put(50, "SwitchStatement");
		ASTNodeMapping.put(51, "SynchronizedStatement");
		ASTNodeMapping.put(52, "ThisExpression");
		ASTNodeMapping.put(53, "ThrowStatement");
		ASTNodeMapping.put(54, "TryStatement");
		ASTNodeMapping.put(55, "TypeDeclaration");
		ASTNodeMapping.put(56, "TypeDeclarationStatement");
		ASTNodeMapping.put(57, "TypeLiteral");
		ASTNodeMapping.put(58, "VariableDeclarationExpression");
		ASTNodeMapping.put(59, "VariableDeclarationFragment");
		ASTNodeMapping.put(60, "VariableDeclarationStatement");
		ASTNodeMapping.put(61, "WhileStatement");
		ASTNodeMapping.put(62, "InstanceofExpression");
		ASTNodeMapping.put(63, "LineComment");
		ASTNodeMapping.put(64, "BlockComment");
		ASTNodeMapping.put(65, "TagElement");
		ASTNodeMapping.put(66, "TextElement");
		ASTNodeMapping.put(67, "MemberRef");
		ASTNodeMapping.put(68,"MethodRef");
		ASTNodeMapping.put(69, "MethodRefParameter");
		ASTNodeMapping.put(70, "EnhancedForStatement");
		ASTNodeMapping.put(71, "EnumDeclaration");
		ASTNodeMapping.put(72, "EnumConstantDeclaration");
		ASTNodeMapping.put(73, "TypeParameter");
		ASTNodeMapping.put(74, "ParameterizedType");
		ASTNodeMapping.put(75, "QualifiedType");
		ASTNodeMapping.put(76, "WildcardType");
		ASTNodeMapping.put(77, "NormalAnnotation");
		ASTNodeMapping.put(78, "MarkerAnnotation");
		ASTNodeMapping.put(79, "SingleMemberAnnotation");
		ASTNodeMapping.put(80, "MemberValuePair");
		ASTNodeMapping.put(81, "AnnotationTypeDeclaration");
		ASTNodeMapping.put(82, "AnnotationTypeMemberDeclaration");
		ASTNodeMapping.put(83, "Modifier");
		ASTNodeMapping.put(84, "UnionType"); 
		ASTNodeMapping.put(85, "Dimension");
		ASTNodeMapping.put(86, "LambdaExpression");
		ASTNodeMapping.put(87, "IntersectionType");
		ASTNodeMapping.put(88, "NameQualifiedType");
		ASTNodeMapping.put(89, "CreationReference");
		ASTNodeMapping.put(90, "ExpressionMethodReference");
		ASTNodeMapping.put(91, "SuperMethhodReference");
		ASTNodeMapping.put(92, "TypeMethodReference");
	}
}
