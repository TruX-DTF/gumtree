package edu.lu.uni.serval.gen.jdt.exp;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Expression;

public class ExpressionRebuilder {
	
	public Expression createExpression(String expressionCode) {
		ASTParser parser = createASTParser(expressionCode.toCharArray());
		parser.setKind(ASTParser.K_EXPRESSION);
		Expression expression = (Expression) parser.createAST(null);
		return expression;
	}
	
	private ASTParser createASTParser(char[] javaCode) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(javaCode);

		return parser;
	}
}
