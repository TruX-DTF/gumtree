package edu.lu.uni.serval.gumtree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTParser;

import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator3;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeContext;

public class Test {

	public static void main(String[] args) {
		String a = "File sr = new File(home); int a = 1; if (!a){}"; //"{if (!a){}}";
		String b = "File sr = new File(home, Property.PROPERTIES_FILE);if (isTrue(a)){} int a = 1;";//"{if (isTrue(a)){}}";
		ArrayList<String> ret = compareTwoFilesWithGumTree(a, b);
		System.out.println(ret);
		for (String str : ret) {
			System.out.println(str);
		}
	}
	
	public static ArrayList<String> compareTwoFilesWithGumTree(String fileA, String fileB) {
		
		ArrayList<String> ret = new ArrayList<String>();
		
		try {
			TreeContext tc1 = null;
			TreeContext tc2 = null;
//			Run.initGenerators();
//			tc1 = Generators.getInstance().getTree(fileA);
//			tc2 = Generators.getInstance().getTree(fileB);
			tc1 = new JdtTreeGenerator3().generateFromString(fileA, ASTParser.K_STATEMENTS);
			tc2 = new JdtTreeGenerator3().generateFromString(fileB, ASTParser.K_STATEMENTS);
//			tc1 = new CdJdtTreeGenerator().generateFromString(fileA, ASTParser.K_STATEMENTS);
//			tc2 = new CdJdtTreeGenerator().generateFromString(fileB, ASTParser.K_STATEMENTS);
//			tc1 = new ExpJdtTreeGenerator().generateFromString(fileA, ASTParser.K_STATEMENTS);
//			tc2 = new ExpJdtTreeGenerator().generateFromString(fileB, ASTParser.K_STATEMENTS);
			ITree t1 = tc1.getRoot();
			ITree t2 = tc2.getRoot();
			
			Matcher m = Matchers.getInstance().getMatcher(t1, t2);
			m.match();
			
			ActionGenerator ag = new ActionGenerator(t1, t2, m.getMappings());
			ag.generate();
			List<Action> actions = ag.getActions();
			for(Action ac : actions){
				String s = parseAction(ac.toString());
				if (!"".equals(s)){
					ret.add(s);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ret;
	}

	private static String parseAction(String actStr) {
		// UPD 25@@!a from !a to isTrue(a) at 69
		int nodeTypePos = actStr.indexOf(" ");
		String nodeTypeInteger = actStr.substring(nodeTypePos, actStr.indexOf("@@")).trim();
		String nodeTypeStr = mapping.get(Integer.parseInt(nodeTypeInteger));
		String act = actStr.substring(0, actStr.indexOf(nodeTypeInteger));
		act += nodeTypeStr;
		act += actStr.substring(actStr.indexOf("@@"));
		return act;
	}
	
	private static Map<Integer, String> mapping;
	
	static {
		mapping  = new HashMap<Integer, String>();
		mapping.put(1, "AnonymousClassDeclaration");
		mapping.put(2, "ArrayAccess");
		mapping.put(3, "ArrayCreation");
		mapping.put(4, "ArrayInitializer");
		mapping.put(5, "ArrayType");
		mapping.put(6, "AssertStatement");
		mapping.put(7, "Assignment");
		mapping.put(8, "Block");
		mapping.put(9, "BooleanLiteral");
		mapping.put(10, "BreakStatement");
		mapping.put(11, "CastExpression");
		mapping.put(12, "CatchClause");
		mapping.put(13, "CharacterLiteral");
		mapping.put(14, "ClassInstanceCreation");
		mapping.put(15, "CompilationUnit");
		mapping.put(16, "ConditionalExpression");
		mapping.put(17, "ConstructorInvocation");
		mapping.put(18, "ContinueStatement");
		mapping.put(19, "DoStatement");
		mapping.put(20, "EmptyStatement");
		mapping.put(21, "ExpressionStatement");
		mapping.put(22, "FieldAccess");
		mapping.put(23, "FieldDeclaration");
		mapping.put(24, "ForStatement");
		mapping.put(25, "IfStatement");
		mapping.put(26, "ImportDeclaration");
		mapping.put(27, "InfixExpression");
		mapping.put(28, "Initializer");
		mapping.put(29, "Javadoc");
		mapping.put(30, "LabeledStatement");
		mapping.put(31, "MethodDeclaration");
		mapping.put(32, "MethodInvocation");
		mapping.put(33, "NullLiteral");
		mapping.put(34, "NumberLiteral");
		mapping.put(35, "PackageDeclaration");
		mapping.put(36, "ParenthesizedExpression");
		mapping.put(37, "PostfixExpression");
		mapping.put(38, "PrefixExpression");
		mapping.put(39, "PrimitiveType");
		mapping.put(40, "QualifiedName");
		mapping.put(41, "ReturnStatement");
		mapping.put(42, "SimpleName");
		mapping.put(43, "SimpleType");
		mapping.put(44, "SingleVariableDeclaration");
		mapping.put(45, "StringLiteral");
		mapping.put(46, "SuperConstructorInvocation");
		mapping.put(47, "SuperFieldAccess");
		mapping.put(48, "SuperMethodInvocation");
		mapping.put(49, "SwitchCase");
		mapping.put(50, "SwitchStatement");
		mapping.put(51, "SynchronizedStatement");
		mapping.put(52, "ThisExpression");
		mapping.put(53, "ThrowStatement");
		mapping.put(54, "TryStatement");
		mapping.put(55, "TypeDeclaration");
		mapping.put(56, "TypeDeclarationStatement");
		mapping.put(57, "TypeLiteral");
		mapping.put(58, "VariableDeclarationExpression");
		mapping.put(59, "VariableDeclarationFragment");
		mapping.put(60, "VariableDeclarationStatement");
		mapping.put(61, "WhileStatement");
		mapping.put(62, "InstanceofExpression");
		mapping.put(63, "LineComment");
		mapping.put(64, "BlockComment");
		mapping.put(65, "TagElement");
		mapping.put(66, "TextElement");
		mapping.put(67, "MemberRef");
		mapping.put(68,"MethodRef");
		mapping.put(69, "MethodRefParameter");
		mapping.put(70, "EnhancedForStatement");
		mapping.put(71, "EnumDeclaration");
		mapping.put(72, "EnumConstantDeclaration");
		mapping.put(73, "TypeParameter");
		mapping.put(74, "ParameterizedType");
		mapping.put(75, "QualifiedType");
		mapping.put(76, "WildcardType");
		mapping.put(77, "NormalAnnotation");
		mapping.put(78, "MarkerAnnotation");
		mapping.put(79, "SingleMemberAnnotation");
		mapping.put(80, "MemberValuePair");
		mapping.put(81, "AnnotationTypeDeclaration");
		mapping.put(82, "AnnotationTypeMemberDeclaration");
		mapping.put(83, "Modifier");
		mapping.put(84, "UnionType"); 
		mapping.put(85, "Dimension");
		mapping.put(86, "LambdaExpression");
		mapping.put(87, "IntersectionType");
		mapping.put(88, "NameQualifiedType");
		mapping.put(89, "CreationReference");
		mapping.put(90, "ExpressionMethodReference");
		mapping.put(91, "SuperMethhodReference");
		mapping.put(92, "TypeMethodReference");
	}
}
