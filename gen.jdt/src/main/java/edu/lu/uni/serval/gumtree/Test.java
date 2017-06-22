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

import edu.lu.uni.serval.gen.jdt.rowToken.RowTokenJdtTreeGenerator;

public class Test {

	public static void main(String[] args) {
		String a = "File sr = FileUtil.newFile(home); int a = 1; if (!a){}"; //"{if (!a){}}";
		String b = "File sr = new File(home);if (!isTrue(a)){} int a = 1;";//"{if (isTrue(a)){}}";

		ArrayList<String> ret = compareTwoFilesWithGumTree(a, b);
		System.out.println(ret + "\n");
		
		/**
		 * Position of actions:
		 * DEL, UPD, MOV: the positions of changed source code are the positions of these source code in previous java file.
		 * INS: the positions of changed source code is the position of the source code in revised java file.
		 */
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
			tc1 = new RowTokenJdtTreeGenerator().generateFromString(fileA, ASTParser.K_STATEMENTS);
			tc2 = new RowTokenJdtTreeGenerator().generateFromString(fileB, ASTParser.K_STATEMENTS);
//			tc1 = new JdtTreeGenerator3().generateFromString(fileA, ASTParser.K_STATEMENTS);
//			tc2 = new JdtTreeGenerator3().generateFromString(fileB, ASTParser.K_STATEMENTS);
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
