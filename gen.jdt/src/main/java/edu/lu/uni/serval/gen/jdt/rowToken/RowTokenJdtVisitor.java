/*
 * This file is part of GumTree.
 *
 * GumTree is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GumTree is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GumTree.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2011-2015 Jean-Rémy Falleri <jr.falleri@gmail.com>
 * Copyright 2011-2015 Floréal Morandat <florealm@gmail.com> *
 */


package edu.lu.uni.serval.gen.jdt.rowToken;


import java.util.List;

import org.eclipse.jdt.core.dom.*;

public class RowTokenJdtVisitor  extends AbstractRowTokenJdtVisitor {
    public RowTokenJdtVisitor() {
        super();
    }

    @Override
    public void preVisit(ASTNode n) {
    	if (! (n instanceof Comment || n instanceof TagElement || n instanceof TextElement)) {
    		pushNode(n, getLabel(n));
    	}
    }

    protected String getLabel(ASTNode n) {
    	if (n instanceof AnonymousClassDeclaration) {
    		
		} else if (n instanceof BodyDeclaration) {
			if (n instanceof MethodDeclaration) { // methods and constructors
				MethodDeclaration method = (MethodDeclaration) n;
				List<?> modifiers = method.modifiers();
				Type returnType = method.isConstructor() ? null : method.getReturnType2();
				List<?> typeParameters = method.typeParameters();
				SimpleName methodName = method.getName();
				List<?> parameters = method.parameters();
				List<?> exceptionTypes = method.thrownExceptionTypes();
				String methodLabel = "";
				for (Object obj : modifiers) {
					methodLabel += obj.toString() + ", ";
				}
				methodLabel += (returnType == null) ? "" : (returnType.toString() + ", ");
				for (Object obj : typeParameters) {
					methodLabel += obj.toString() + ", ";
				}
				methodLabel += methodName + ", ";
				for (Object obj : parameters) {
					methodLabel += obj.toString() + ", ";
				}
				for (Object obj : exceptionTypes) {
					methodLabel += obj.toString() + ", ";
				}
				return "MethodDeclaration:" + methodLabel;
			} else if (n instanceof AbstractTypeDeclaration) {
				if (n instanceof TypeDeclaration) { // classes and interfaces
					TypeDeclaration node = (TypeDeclaration) n;
					return node.getName().toString();
				} else if (n instanceof EnumDeclaration) {
					
				} else {
					// AnnotationTypeDeclaration
				}
			} else if (n instanceof AnnotationTypeMemberDeclaration) {
			} else if (n instanceof EnumConstantDeclaration) {
			} else if (n instanceof FieldDeclaration) {
				FieldDeclaration node = (FieldDeclaration) n;
				String nodeStr = node.toString();
		    	nodeStr = nodeStr.substring(0, nodeStr.length() - 1);
		    	return "FieldDeclaration:" + nodeStr;
			} else { // Initializer
			} 
		} else if (n instanceof CatchClause) {
			CatchClause catchClause = (CatchClause) n;
			return "CatchClause:" + catchClause.getException().toString();
		} else if (n instanceof Comment) {
			// if (n instanceof BlockComment) {
			// } else if (n instanceof Javadoc) {
			// } else { //LineComment
			// }
		// } else if (n instanceof Dimension) {
		} else if (n instanceof Expression) {
			if (n instanceof Annotation) { // MarkerAnnotation,NormalAnnotation,SingleMemberAnnotation
			} else if (n instanceof ArrayAccess) {
				return "ArrayAccess:" + ((ArrayAccess)n).toString();
			} else if (n instanceof ArrayCreation) {
				return "ArrayCreation:" + ((ArrayCreation)n).toString();
			} else if (n instanceof ArrayInitializer) {
				return "ArrayInitializer:" + ((ArrayInitializer)n).toString();
			} else if (n instanceof Assignment) { // = += -= etc.
				return "Assignment:" + ((Assignment) n).getOperator().toString();
			} else if (n instanceof BooleanLiteral) {// true, false
				return "BooleanLiteral:" + ((BooleanLiteral) n).toString();
			} else if (n instanceof CastExpression) {
				return "CastExpression:" + ((CastExpression)n).toString();
			} else if (n instanceof CharacterLiteral) {
				return "CharacterLiteral:" + ((CharacterLiteral) n).getEscapedValue();
			} else if (n instanceof ClassInstanceCreation) {
				return "ClassInstanceCreation:" + ((ClassInstanceCreation)n).toString();
			} else if (n instanceof ConditionalExpression) {
				return "ConditionalExpression" + ((ConditionalExpression)n).toString();
			} else if (n instanceof FieldAccess) {
				return "FieldAccess:" + ((FieldAccess)n).toString();
			} else if (n instanceof InfixExpression) {
				return "InfixExpression:" + ((InfixExpression) n).getOperator().toString();
			} else if (n instanceof InstanceofExpression) {
				return "InstanceofExpression:" + ((InstanceofExpression)n).toString();
			} else if (n instanceof LambdaExpression) {
				return "LambdaExpression:"+((LambdaExpression)n).toString();
			} else if (n instanceof MethodInvocation) {
				return "MethodInvocation:"+((MethodInvocation)n).toString();
			} else if (n instanceof MethodReference) { 
				// CreationReference, ExpressionMethodReference, SuperMethodReference, TypeMethodReference.
			} else if (n instanceof Name) { // (SimpleName, QualifiedName)
				if (n instanceof QualifiedName) {
					return "QualifiedName:"+ ((QualifiedName) n).getFullyQualifiedName();
				} else {
					return "SimpleName:"+ ((SimpleName) n).getFullyQualifiedName();
				}
			} else if (n instanceof NullLiteral) {
				return "NullLiteral:null";
			} else if (n instanceof NumberLiteral) {
				return "NumberLiteral:" +((NumberLiteral) n).getToken();
			} else if (n instanceof ParenthesizedExpression) {
				return "ParenthesizedExpression:"+((ParenthesizedExpression)n).toString();
			} else if (n instanceof PostfixExpression) {
				return "PostfixExpression:"+((PostfixExpression) n).getOperator().toString();
			} else if (n instanceof PrefixExpression) {
				return "PrefixExpression:"+((PrefixExpression) n).getOperator().toString();
			} else if (n instanceof StringLiteral) {
				return "StringLiteral:"+((StringLiteral) n).getEscapedValue();
			} else if (n instanceof ThisExpression) {
				return "this";
			} else if (n instanceof SuperFieldAccess) {
				return "super:" + ((SuperFieldAccess)n).toString();
			} else if (n instanceof SuperMethodInvocation) {
				return ((SuperMethodInvocation)n).toString();
			} else if (n instanceof TypeLiteral) {
				return "TypeLiteral:" +((TypeLiteral)n).toString();
			} else if (n instanceof VariableDeclarationExpression){
				return "VariableDeclarationExpression:" +((VariableDeclarationExpression)n).toString();
			}
		} else if (n instanceof ImportDeclaration) {
			ImportDeclaration node = (ImportDeclaration) n;
			String nodeStr = node.toString();
	    	nodeStr = nodeStr.substring(0, nodeStr.length() - 1);
	    	return "ImportDeclaration:" + nodeStr;
		// } else if (n instanceof MemberRef) {
		// } else if (n instanceof MemberValuePair) {
		// } else if (n instanceof MethodRef) {
		// } else if (n instanceof MethodRefParameter) {
		} else if (n instanceof Modifier) { // public, protected, private, static, abstract, final, native, synchronized, transient, volatile, strictfp
			Modifier node = (Modifier) n;
			return "Modifier:" + node.getKeyword().toString();
		// } else if (n instanceof PackageDeclaration) {
		} else if (n instanceof Statement) {
			if (n instanceof Block) {
			} else if (n instanceof AssertStatement) {
				AssertStatement node = (AssertStatement) n;
				Expression exp = node.getExpression();
		    	Expression msg = node.getMessage();
		        String value = exp.getClass().getSimpleName() + ":" + exp.toString();
		        if (msg != null) {
		            value += ", Msg-" + msg.getClass().getSimpleName() + ":" + msg.toString();
		        }
				return "AssertStatement:"+value;
			} else if (n instanceof BreakStatement) {
				BreakStatement node = (BreakStatement) n;
				return "BreakStatement:"+node.getLabel() != null ? node.getLabel().toString() : "";
			} else if (n instanceof ConstructorInvocation) {
				ConstructorInvocation node = (ConstructorInvocation) n;
				String nodeStr = node.toString();
		        nodeStr = nodeStr.substring(0, nodeStr.length() - 1);
		        return "ConstructorInvocation:"+nodeStr;
			} else if (n instanceof ContinueStatement) {
				ContinueStatement node = (ContinueStatement) n;
				return "ContinueStatement:"+node.getLabel() != null ? node.getLabel().toString() : "";
			} else if (n instanceof DoStatement) {
				DoStatement node = (DoStatement) n;
				Expression exp = node.getExpression();
				return "DoStatement:"+exp.getClass().getSimpleName() + ":" + exp.toString();
			} else if (n instanceof EmptyStatement) {
			} else if (n instanceof EnhancedForStatement) {
				EnhancedForStatement node = (EnhancedForStatement)n;
				SingleVariableDeclaration parameter = node.getParameter();
		    	Expression exp = node.getExpression();
				return "EnhancedForStatement:"+parameter.toString() + ", " + exp.getClass().getSimpleName() + ":" + exp.toString();
			} else if (n instanceof ExpressionStatement) {
				ExpressionStatement node = (ExpressionStatement)n;
				Expression exp = node.getExpression();
				return "ExpressionStatement:"+exp.getClass().getSimpleName() + ":" + exp.toString();
			} else if (n instanceof ForStatement) {
				ForStatement node = (ForStatement)n;
				String value = "";
		        List<?> init = node.initializers();
				Expression exp = node.getExpression();
				List<?> update = node.updaters();
		        value += init.toString() + ";";
				if (exp != null) {
					value += exp.toString() + "; ";
				}
				value += update.toString();
				return "ForStatement:"+value;
			} else if (n instanceof IfStatement) {
				IfStatement node = (IfStatement) n;
				Expression exp = node.getExpression();
				return "IfStatement:"+exp.getClass().getSimpleName() + ":" + exp.toString();
			} else if (n instanceof LabeledStatement) {
				return "LabeledStatement:"+((LabeledStatement)n).getLabel().getFullyQualifiedName();
			} else if (n instanceof ReturnStatement) {
				ReturnStatement node = (ReturnStatement) n;
				Expression exp = node.getExpression();
				if (exp != null) {
					return "ReturnStatement:" + exp.getClass().getSimpleName() + ":" + exp.toString();
				} else {
					return "ReturnStatement: ";
				}
			} else if (n instanceof SuperConstructorInvocation) {
				SuperConstructorInvocation node = (SuperConstructorInvocation) n;
				String nodeStr = node.toString();
		    	nodeStr = nodeStr.substring(0, nodeStr.length() - 1);
				return "SuperConstructorInvocation:" + nodeStr;
			} else if (n instanceof SwitchCase) {
				SwitchCase node = (SwitchCase) n;
				Expression exp = node.getExpression();
		    	if (exp != null) {
		    		return "SwitchCase:" + exp.getClass().getSimpleName() + ":" + exp.toString();
		    	} else {
		    		return "SwitchCase:default";
		    	}
			} else if (n instanceof SwitchStatement) {
				SwitchStatement node = (SwitchStatement)n;
				Expression exp = node.getExpression();
				return "SwitchStatement:"+exp.getClass().getSimpleName() + ":" + exp.toString();
			} else if (n instanceof SynchronizedStatement) { // synchronized  (Expression) Block
				SynchronizedStatement node = (SynchronizedStatement)n;
				Expression exp = node.getExpression();
				return "SynchronizedStatement:"+exp.getClass().getSimpleName() + ":" + exp.toString();
			} else if (n instanceof ThrowStatement) {
				ThrowStatement node = (ThrowStatement)n;
				Expression exp = node.getExpression();
				return "ThrowStatement:"+exp.getClass().getSimpleName() + ":" + exp.toString();
			} else if (n instanceof TryStatement) {
				TryStatement node = (TryStatement) n;
				List<?> resources = node.resources();
		    	if (resources != null) {
		    		return "TryStatement:"+resources.toString();
		    	} else {
		    		return "TryStatement: ";
		    	}
		    	// TODO how to solve the Finally node of TryStatement?
			} else if (n instanceof TypeDeclarationStatement) {
				// TypeDeclaration, EnumDeclaration
			} else if (n instanceof VariableDeclarationStatement) {
				VariableDeclarationStatement node = (VariableDeclarationStatement) n;
				String nodeStr = node.toString();
		    	nodeStr = nodeStr.substring(0, nodeStr.length() - 1);
		    	return "VariableDeclarationStatement:"+nodeStr;
			} else if (n instanceof WhileStatement){
				WhileStatement node = (WhileStatement)n;
				Expression exp = node.getExpression();
				return "WhileStatement:"+exp.getClass().getSimpleName() + ":" + exp.toString();
			}
//		 } else if (n instanceof TagElement) {
//			 return ((TagElement) n).getTagName();
//		 } else if (n instanceof TextElement) {
//	        return n.toString();
		} else if (n instanceof Type) {
			if (n instanceof WildcardType) {
				WildcardType node = (WildcardType) n;
				return "WildcardType:"+ (node.isUpperBound() ? "extends" : "super");
			} else {
				return n.getClass().getSimpleName().toString() + ":" + n.toString();
			}
//			if (n instanceof NameQualifiedType) {
//			} else if (n instanceof PrimitiveType) {
//			} else if (n instanceof QualifiedType) {
//			} else if (n instanceof SimpleType) {
//			} else if (n instanceof ArrayType) {
//			} else if (n instanceof IntersectionType) {
//			} else if (n instanceof ParameterizedType) {
//			} else if (n instanceof UnionType) {
//			}
		} else if (n instanceof TypeParameter) {
			TypeParameter node = (TypeParameter) n;
			return "TypeParameter:" +node.getName().getFullyQualifiedName();
		} else if (n instanceof SingleVariableDeclaration) {
			SingleVariableDeclaration node = (SingleVariableDeclaration)n;
			return "SingleVariableDeclaration:"+node.toString();
		} else if (n instanceof VariableDeclarationFragment) {
			VariableDeclarationFragment node = (VariableDeclarationFragment) n;
			return "VariableDeclarationFragment:"+node.toString();
		}
    	
        return "";
    }

    @Override 
    public boolean visit(Javadoc node) {
    	return false;
    }
    
    @Override
    public boolean visit(QualifiedName node) {
    	return false;
    }
    
    @Override
    public void postVisit(ASTNode n) {
    	if (!(n instanceof Comment || n instanceof TagElement || n instanceof TextElement)) {
    		popNode();
    	}
    }
}
