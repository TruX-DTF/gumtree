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


package com.github.gumtreediff.gen.jdt;


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.jdt.core.dom.*;

import com.github.gumtreediff.gen.jdt.cd.EntityType;

public class JdtVisitor2  extends AbstractJdtVisitor {
    public JdtVisitor2() {
        super();
    }

//    @Override
//    public void preVisit(ASTNode n) {
//        pushNode(n, getLabel(n));
//    }
    @Override
	public void preVisit(ASTNode node) {
	}
    
    //-------------------------------------------------------------------------
    //  Compilation Unit
    //  TypeDeclaration
    //-------------------------------------------------------------------------
    
    @Override
    public boolean visit(PackageDeclaration node) {
		return false;
    }
    
    @Override
    public boolean visit(ImportDeclaration node) {
    	pushNode(node, node.toString());
    	if (node.isStatic()) { // TODO
//    		List<Modifier> modifiers = new ArrayList<>();
//    		visitListAsNode(EntityType.MODIFIERS, modifiers);
//    		pushFakeNode(EntityType n, int startPosition, int length)
    	}
    	return false;
    }
    
    @Override
	public boolean visit(AnnotationTypeDeclaration node) {
		return false;
	}
    
    @Override 
	public boolean visit(AnnotationTypeMemberDeclaration node) {
		return false;
	}
    
    @Override
	public boolean visit(EnumDeclaration node) {
		System.out.println("EnumDeclaration: " + node.toString());
		return false;
	}
	
	@Override
	public boolean visit(EnumConstantDeclaration node) {
		System.out.println("EnumConstantDeclaration: " + node.toString());
		return false;
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
//		if (node.getJavadoc() != null) {
//			node.getJavadoc().accept(this);
//		}

		pushNode(node, node.getName().toString());
		visitListAsNode(EntityType.MODIFIERS, node.modifiers());
		visitListAsNode(EntityType.TYPE_ARGUMENTS, node.typeParameters());
		if (node.getSuperclassType() != null) {
			node.getSuperclassType().accept(this);
		}
		visitListAsNode(EntityType.SUPER_INTERFACE_TYPES, node.superInterfaceTypes());

		// Change Distiller does not check the changes at Class Field declaration
		for (FieldDeclaration fd : node.getFields()) {
			fd.accept(this);
		}

		// Visit Declaration and Body (inside MD visiting)
		for (MethodDeclaration md : node.getMethods()) {
			md.accept(this);
		}
		return false;
	}
	
	@Override
    public void endVisit(TypeDeclaration node) {
//        popNode();
    }
	
	@Override
    public boolean visit(FieldDeclaration node) {
//        if (node.getJavadoc() != null) {
//            node.getJavadoc().accept(this);
//        }
        pushNode(node, node.toString()); // TODO
        visitListAsNode(EntityType.MODIFIERS, node.modifiers());
        node.getType().accept(this);
        visitListAsNode(EntityType.FRAGMENTS, node.fragments());
        return false;
    }

    @Override
    public void endVisit(FieldDeclaration node) {
        popNode();
    }
    
    @Override
	public boolean visit(Initializer node) {
    	return false;
    }
    
    @Override
    public boolean visit(MethodDeclaration node) {
//        if (node.getJavadoc() != null) {
//            node.getJavadoc().accept(this);
//        }

    	pushNode(node, node.getName().toString());

        visitListAsNode(EntityType.MODIFIERS, node.modifiers());
        
        Type returnType = node.getReturnType2();
        if (returnType != null) {
//            node.getReturnType2().accept(this);
            pushNode(returnType, returnType.toString());
        }
        visitListAsNode(EntityType.TYPE_ARGUMENTS, node.typeParameters());
        visitListAsNode(EntityType.PARAMETERS, node.parameters());
        visitListAsNode(EntityType.THROW, node.thrownExceptionTypes());

        // The body can be null when the method declaration is from a interface
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(MethodDeclaration node) {
        popNode();
    }
	//-------------------------------------------------------------------------
    //  Class Declaration
    //  ClassDeclaration, ClassBody, ClassBodyDeclaration, MemberDecl, GenericMethodOrConstructorRest
    //  MethodDeclaratorRest, VoidMethodDeclaratorRest, ConstructorDeclaratorRest, MethodBody
    //-------------------------------------------------------------------------
    
    // ===============Statements===============
    
    // ===============Expressions===============

    protected String getLabel(ASTNode n) {
        if (n instanceof Name) return ((Name) n).getFullyQualifiedName();
        if (n instanceof Type) return n.toString();
        if (n instanceof Modifier) return n.toString();
        if (n instanceof StringLiteral) return ((StringLiteral) n).getEscapedValue();
        if (n instanceof NumberLiteral) return ((NumberLiteral) n).getToken();
        if (n instanceof CharacterLiteral) return ((CharacterLiteral) n).getEscapedValue();
        if (n instanceof BooleanLiteral) return ((BooleanLiteral) n).toString();
        if (n instanceof InfixExpression) return ((InfixExpression) n).getOperator().toString();
        if (n instanceof PrefixExpression) return ((PrefixExpression) n).getOperator().toString();
        if (n instanceof PostfixExpression) return ((PostfixExpression) n).getOperator().toString();
        if (n instanceof Assignment) return ((Assignment) n).getOperator().toString();
        if (n instanceof TextElement) return n.toString();
        if (n instanceof TagElement) return ((TagElement) n).getTagName();

        return "";
    }

    @Override
    public boolean visit(TagElement e) {
        return true;
    }

    @Override
    public void postVisit(ASTNode n) {
//        popNode();
    }
    
    @Override
    public boolean visit(Block node) {
        return true;
    }


    private static final String COLON_SPACE = ": ";
//    private boolean fEmptyJavaDoc;
    private boolean fInMethodDeclaration;
    
//    @Override
//    public boolean visit(Javadoc node) {
//        String string = "";
//        // @Inria: exclude doc
//        if (checkEmptyJavaDoc(string)) {
//            pushNode(node, string);
//        } else {
//            // @TODO not really robust, hopefully there is no nested javadoc comments.
//            fEmptyJavaDoc = true;
//        }
//        return false;
//    }
//    @Override
//    public void endVisit(Javadoc node) {
//        if (!fEmptyJavaDoc)
//            popNode();
//        else
//            fEmptyJavaDoc = false;
//    }
//    private boolean checkEmptyJavaDoc(String doc) {
//        String[] splittedDoc = doc.split("/\\*+\\s*");
//        String result = "";
//        for (String s : splittedDoc) {
//            result += s;
//        }
//        try {
//            result = result.split("\\s*\\*/")[0];
//        } catch (ArrayIndexOutOfBoundsException e) {
//            result = result.replace('/', ' ');
//        }
//        result = result.replace('*', ' ').trim();
//
//        return !result.equals("");
//    }

    @Override
    public boolean visit(Modifier node) {
        pushNode(node, node.getKeyword().toString());
        return false;
    }

    @Override
    public void endVisit(Modifier node) {
        popNode();
    }

    @Override
    public boolean visit(ParameterizedType node) {
        pushNode(node, "");
        node.getType().accept(this);
        visitListAsNode(EntityType.TYPE_ARGUMENTS, node.typeArguments());
        return false;
    }

    @Override
    public void endVisit(ParameterizedType node) {
        popNode();
    }

    @Override
    public boolean visit(PrimitiveType node) {
        String vName = "";
        if (fInMethodDeclaration) {
            vName += getCurrentParent().getLabel() + COLON_SPACE;
        }
        pushNode(node, vName + node.getPrimitiveTypeCode().toString());
        return false;
    }

    @Override
    public void endVisit(PrimitiveType node) {
        popNode();
    }

    @Override
    public boolean visit(QualifiedType node) {
        pushNode(node, "");
        return true;
    }

    @Override
    public void endVisit(QualifiedType node) {
        popNode();
    }

    @Override
    public boolean visit(SimpleType node) {
        String vName = "";
        if (fInMethodDeclaration) {
            vName += getCurrentParent().getLabel() + COLON_SPACE;
        }
        pushNode(node, vName + node.getName().getFullyQualifiedName());
        return false;
    }

    @Override
    public void endVisit(SimpleType node) {
        popNode();
    }

    @Override
    public boolean visit(SingleVariableDeclaration node) {
//        boolean isNotParam = getCurrentParent().getLabel() != EntityType.PARAMETERS.toString();// @inria
        pushNode(node, node.getName().getIdentifier());
        node.getType().accept(this);
        return false;
    }

    @Override
    public void endVisit(SingleVariableDeclaration node) {
        popNode();
    }

    @Override
    public boolean visit(TypeDeclarationStatement node) {
        // skip, only type declaration is interesting
        return true;
    }

    @Override
    public void endVisit(TypeDeclarationStatement node) {
        // do nothing
    }

    @Override
    public boolean visit(TypeLiteral node) {
        pushNode(node, ""); // TODO
        return true;
    }

    @Override
    public void endVisit(TypeLiteral node) {
        popNode();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean visit(TypeParameter node) {
        pushNode(node, node.getName().getFullyQualifiedName());
        visitList(node.typeBounds());
        return false;
    }

    @Override
    public void endVisit(TypeParameter node) {
        popNode();
    }

    @Override
    public boolean visit(VariableDeclarationExpression node) {
        pushNode(node, ""); // TODO
        visitListAsNode(EntityType.MODIFIERS, node.modifiers());
        node.getType().accept(this);
        visitListAsNode(EntityType.FRAGMENTS, node.fragments());
        return false;
    }

    @Override
    public void endVisit(VariableDeclarationExpression node) {
        popNode();
    }

    @Override
    public boolean visit(VariableDeclarationFragment node) {
        pushNode(node, node.getName().getFullyQualifiedName());
        return false;
    }

    @Override
    public void endVisit(VariableDeclarationFragment node) {
        popNode();
    }

    @Override
    public boolean visit(WildcardType node) {
        String bound = node.isUpperBound() ? "extends" : "super";
        pushNode(node, bound);
        return true;
    }

    @Override
    public void endVisit(WildcardType node) {
        popNode();
    }

    private void visitList(List<ASTNode> list) {
        for (ASTNode node : list) {
            (node).accept(this);
        }
    }

    private void visitListAsNode(EntityType fakeType, List<?> list1) {
    	List<ASTNode> list = new ArrayList<>();
    	for (Object obj : list1) {
    		list.add((ASTNode)obj);
    	}
        int start = startPosition(list);
        pushFakeNode(fakeType, start, endPosition(list) - start);
        if (!list.isEmpty()) {
            // As ChangeDistiller has empty nodes e.g. Type Argument, Parameter, Thown,
            // the push and pop are before the empty condition check
            visitList(list);
        }
//        popNode();
    }

    private int startPosition(List<ASTNode> list) {
        if (list.isEmpty())
            return -1;
        return list.get(0).getStartPosition();
    }

    private int endPosition(List<ASTNode> list) {
        if (list.isEmpty())
            return 0;
        ASTNode n = list.get(list.size() - 1);
        return n.getStartPosition() + n.getLength();
    }
    // /***************BODY VISITOR*************************
    private static final String COLON = ":";

    @Override
    public boolean visit(AssertStatement node) {
        String value = node.getExpression().toString();
        if (node.getMessage() != null) {
            value += COLON + node.getMessage().toString();
        }
        pushNode(node, value);
        return false;
    }

    @Override
    public void endVisit(AssertStatement node) {
        popNode();
    }

    @Override
    public boolean visit(BreakStatement node) {
        pushNode(node, node.getLabel() != null ? node.getLabel().toString() : "");
        return false;
    }

    @Override
    public void endVisit(BreakStatement node) {
        popNode();
    }

    @Override
    public boolean visit(CatchClause node) {
        pushNode(node, ((SimpleType) node.getException().getType()).getName().getFullyQualifiedName());
        // since exception type is used as value, visit children by hand
        node.getBody().accept(this);
        return false;
    }

    @Override
    public void endVisit(CatchClause node) {
        popNode();
    }

    @Override
    public boolean visit(ConstructorInvocation node) {
        pushNode(node, node.toString());
        return false;
    }

    @Override
    public void endVisit(ConstructorInvocation node) {
        popNode();
    }

    @Override
    public boolean visit(ContinueStatement node) {
        pushNode(node, node.getLabel() != null ? node.getLabel().toString() : "");
        return false;
    }

    @Override
    public void endVisit(ContinueStatement node) {
        popNode();
    }

    @Override
    public boolean visit(DoStatement node) {
        pushNode(node, node.getExpression().toString());
        return true;
    }

    @Override
    public void endVisit(DoStatement node) {
        popNode();
    }

    @Override
    public boolean visit(EmptyStatement node) {
        pushNode(node, "");
        return false;
    }

    @Override
    public void endVisit(EmptyStatement node) {
        popNode();
    }

    @Override
    public boolean visit(EnhancedForStatement node) {
        pushNode(node, node.getParameter().toString() + COLON + node.getExpression().toString());
        return true;
    }

    @Override
    public void endVisit(EnhancedForStatement node) {
        popNode();
    }

    @Override
    public boolean visit(ExpressionStatement node) {
        pushNode(node.getExpression(), node.toString());
        return false;
    }

    @Override
    public void endVisit(ExpressionStatement node) {
        popNode();
    }

    @Override
    public boolean visit(ForStatement node) {
        String value = "";
        if (node.getExpression() != null) {
            value = node.getExpression().toString();
        }
        pushNode(node, value);
        return true;
    }

    @Override
    public void endVisit(ForStatement node) {
        popNode();
    }

    @Override
    public boolean visit(IfStatement node) {
        pushNode(node, "if");

        Expression expression = node.getExpression();
        expression.accept(this);

        Statement stmt = node.getThenStatement();
        if (stmt != null) {
            pushNode(stmt, "ThenBlock");
            stmt.accept(this);
            popNode();
        }

        stmt = node.getElseStatement();
        if (stmt != null) {
            pushNode(stmt, "ElseBlock");
            stmt.accept(this);
            popNode();
        }
        return false;
    }

    @Override
    public void endVisit(IfStatement node) {
        popNode();
    }

    @Override
    public boolean visit(LabeledStatement node) {
        pushNode(node, node.getLabel().getFullyQualifiedName());
        node.getBody().accept(this);
        return false;
    }

    @Override
    public void endVisit(LabeledStatement node) {
        popNode();
    }

    @Override
    public boolean visit(ReturnStatement node) {
        pushNode(node, node.getExpression() != null ? node.getExpression().toString() : "");
        return false;
    }

    @Override
    public void endVisit(ReturnStatement node) {
        popNode();
    }

    @Override
    public boolean visit(SuperConstructorInvocation node) {
        pushNode(node, node.toString());
        return false;
    }

    @Override
    public void endVisit(SuperConstructorInvocation node) {
        popNode();
    }

    @Override
    public boolean visit(SwitchCase node) {
        pushNode(node, node.getExpression() != null ? node.getExpression().toString() : "default");
        return false;
    }

    @Override
    public void endVisit(SwitchCase node) {
        popNode();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean visit(SwitchStatement node) {
        pushNode(node, node.getExpression().toString());
        visitList(node.statements());
        return false;
    }

    @Override
    public void endVisit(SwitchStatement node) {
        popNode();
    }

    @Override
    public boolean visit(SynchronizedStatement node) {
        pushNode(node, node.getExpression().toString());
        return true;
    }

    @Override
    public void endVisit(SynchronizedStatement node) {
        popNode();
    }

    @Override
    public boolean visit(ThrowStatement node) {
        pushNode(node, node.getExpression().toString());
        return false;
    }

    @Override
    public void endVisit(ThrowStatement node) {
        popNode();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean visit(TryStatement node) {
        pushNode(node, "");

        Statement stmt = node.getBody();
        pushNode(stmt, "");
        stmt.accept(this);
        popNode();

        visitListAsNode(EntityType.CATCH_CLAUSES, node.catchClauses());

        stmt = node.getFinally();
        if (stmt != null) {
            // @Inria
            pushNode(stmt, "");
            stmt.accept(this);
            popNode();
        }
        return false;
    }

    @Override
    public void endVisit(TryStatement node) {
        popNode();
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
        pushNode(node, node.toString());
        return false;
    }

    @Override
    public void endVisit(VariableDeclarationStatement node) {
        popNode();
    }

    @Override
    public boolean visit(WhileStatement node) {
        pushNode(node, node.getExpression().toString());
        return true;
    }

    @Override
    public void endVisit(WhileStatement node) {
        popNode();
    }

    @Override
	public boolean visit(ArrayAccess node) {
		/**
		 * ArrayAccess:
		 *    Expression <b>[</b> Expression <b>]</b>
		 */
		Expression arrayExpression = node.getArray(); // MyExpression.leftHandExpression
		Expression indexExpression = node.getIndex(); // MyExpression.rightHandExpression
		return false;
	}

	@Override
	public boolean visit(ArrayCreation node) {
		/**
		 * ArrayCreation:
		 *    <b>new</b> PrimitiveType <b>[</b> Expression <b>]</b> { <b>[</b> Expression <b>]</b> } { <b>[</b> <b>]</b> }
		 *    <b>new</b> TypeName [ <b>&lt;</b> Type { <b>,</b> Type } <b>&gt;</b> ]
		 *        <b>[</b> Expression <b>]</b> { <b>[</b> Expression <b>]</b> } { <b>[</b> <b>]</b> }
		 *    <b>new</b> PrimitiveType <b>[</b> <b>]</b> { <b>[</b> <b>]</b> } ArrayInitializer
		 *    <b>new</b> TypeName [ <b>&lt;</b> Type { <b>,</b> Type } <b>&gt;</b> ]
		 *        <b>[</b> <b>]</b> { <b>[</b> <b>]</b> } ArrayInitializer
		 */
		ArrayType arrayType = node.getType();   // MyExpression.qualifier
		if (arrayType != null) {
		}
		
		List<?> dimensions = node.dimensions(); // MyExpression.subExpresions
		for (Object obj : dimensions) {
			Expression exp = (Expression) obj;
		}
		
		ArrayInitializer initializer = node.getInitializer(); // MyExpression.arguments
		if (initializer != null) {
		}
		return false;
	}

	@Override
	public boolean visit(ArrayInitializer node) {
		List<?> exps = node.expressions();
		for (Object obj : exps) {
			Expression exp = (Expression) obj;
		}
		return false;
	}

	@Override
	public boolean visit(Assignment node) {
		/**
		 * Assignment:
		 *    Expression AssignmentOperator Expression
		 * AssignmentOperator:<code>
		 *    <b>=</b> ASSIGN
		 *    <b>+=</b> PLUS_ASSIGN
		 *    <b>-=</b> MINUS_ASSIGN
		 *    <b>*=</b> TIMES_ASSIGN
		 *    <b>/=</b> DIVIDE_ASSIGN
		 *    <b>&amp;=</b> BIT_AND_ASSIGN
		 *    <b>|=</b> BIT_OR_ASSIGN
		 *    <b>^=</b> BIT_XOR_ASSIGN
		 *    <b>%=</b> REMAINDER_ASSIGN
		 *    <b>&lt;&lt;=</b> LEFT_SHIFT_ASSIGN
		 *    <b>&gt;&gt;=</b> RIGHT_SHIFT_SIGNED_ASSIGN
		 *    <b>&gt;&gt;&gt;=</b> RIGHT_SHIFT_UNSIGNED_ASSIGN</code>
		 */
		Expression leftHandExp = node.getLeftHandSide();
		Expression rightHandExp = node.getRightHandSide();
		Assignment.Operator op = node.getOperator();
		
		return false;
	}

	@Override 
	public boolean visit(BooleanLiteral node) {
		return false;
	}
	
	@Override
	public boolean visit(CastExpression node) {
		/**
		 * Cast expression AST node type.
		 * CastExpression:
		 *    <b>(</b> Type <b>)</b> Expression
		 */
		Type castType = node.getType();        // qualifier
		Expression exp = node.getExpression(); // rightHandExpression
		return false;
	}

	@Override 
	public boolean visit(CharacterLiteral node) {
		return false;
	}
	
	@Override
	public boolean visit(ClassInstanceCreation node) {
		/**
		 * ClassInstanceCreation:
		 *        [ Expression <b>.</b> ]
		 *            <b>new</b> [ <b>&lt;</b> Type { <b>,</b> Type } <b>&gt;</b> ]
		 *            Type <b>(</b> [ Expression { <b>,</b> Expression } ] <b>)</b>
		 *            [ AnonymousClassDeclaration ]
		 */
		Type type = node.getType(); // qualifier, Constructor
		Expression exp = node.getExpression(); 
		if (exp != null) { // File.newFile();
		}
		
		List<?> arguments = node.arguments();
		if (arguments != null) {
			for (int i = 0, size = arguments.size(); i < size; i ++) {
				Expression argument = (Expression) arguments.get(i);
			}
		}
		
//		/**
//		 * AnonymousClassDeclaration:
//		 *        <b>{</b> ClassBodyDeclaration <b>}</b>
//		 */
//		AnonymousClassDeclaration acd = node.getAnonymousClassDeclaration();
//		if (acd != null) {
//			@SuppressWarnings("unused")
//			List<?> bodyDeclarations = acd.bodyDeclarations(); // a Class 
//			System.out.println("AnonymousClassDeclaration" + node.toString());
//		}

		return false;
	}
	
	@Override
	public boolean visit(ConditionalExpression node) {
		// Expression <b>?</b> Expression <b>:</b> Expression
		Expression conditionalExp = node.getExpression(); // left
		Expression thenExp = node.getThenExpression();    // middle
		return false;
	}

	@Override
	public boolean visit(CreationReference node) {
		/**
		 * Creation reference expression AST node type (added in JLS8 API).
		 * CreationReference:
		 *     Type <b>::</b> 
		 *         [ <b>&lt;</b> Type { <b>,</b> Type } <b>&gt;</b> ]
		 *         <b>new</b>
		 */
		Type type = node.getType();
		List<?> typeArguments =  node.typeArguments();
		if (typeArguments != null) {
			for (Object obj : typeArguments) {
				Type typeArgument = (Type) obj;
			}
		}
		return false;
	}

	@Override
	public boolean visit(ExpressionMethodReference node) {
		/**
		 * ExpressionMethodReference:
		 *     Expression <b>::</b> 
		 *         [ <b>&lt;</b> Type { <b>,</b> Type } <b>&gt;</b> ]
		 *         Identifier
		 */
		Expression exp = node.getExpression();
		List<?> typeArguments = node.typeArguments();
		if (typeArguments != null) {
			for (Object obj : typeArguments) {
				Type typeArgument = (Type) obj;
			}
		}
		String methodName = node.getName().getIdentifier();
		return false;
	}

	@Override
	public boolean visit(FieldAccess node) {
		/**
		 * FieldAccess:
		 * 		Expression <b>.</b> Identifier
		 */ 
		Expression exp = node.getExpression();    // leftHandExp
		String identifier = node.getName().toString();  // identifier
		return false;
	}

	@Override
	public boolean visit(InfixExpression node) {
		/**
		 * InfixExpression:
		 *    Expression InfixOperator Expression { InfixOperator Expression }
		 * InfixOperator:<code>
		 *    <b>*</b>	TIMES
		 *    <b>/</b>  DIVIDE
		 *    <b>%</b>  REMAINDER
		 *    <b>+</b>  PLUS
		 *    <b>-</b>  MINUS
		 *    <b>&lt;&lt;</b>  LEFT_SHIFT
		 *    <b>&gt;&gt;</b>  RIGHT_SHIFT_SIGNED
		 *    <b>&gt;&gt;&gt;</b>  RIGHT_SHIFT_UNSIGNED
		 *    <b>&lt;</b>  LESS
		 *    <b>&gt;</b>  GREATER
		 *    <b>&lt;=</b>  LESS_EQUALS
		 *    <b>&gt;=</b>  GREATER_EQUALS
		 *    <b>==</b>  EQUALS
		 *    <b>!=</b>  NOT_EQUALS
		 *    <b>^</b>  XOR
		 *    <b>&amp;</b>  AND
		 *    <b>|</b>  OR
		 *    <b>&amp;&amp;</b>  CONDITIONAL_AND
		 *    <b>||</b>  CONDITIONAL_OR</code>
		 */
		InfixExpression.Operator infixOperator = node.getOperator();
		Expression leftExp = node.getLeftOperand();
		
		List<?> extendedOperands = node.extendedOperands();
		if (extendedOperands != null && extendedOperands.size() > 0) {
			String nodeStr = node.toString();
			nodeStr = nodeStr.substring(leftExp.toString().length()).trim();
			nodeStr = nodeStr.substring(infixOperator.toString().length()).trim();
			
		} else {
			Expression rightExp = node.getRightOperand();
		}
		
		return false;
	}

	@Override
	public boolean visit(InstanceofExpression node) {
		/**
		 * InstanceofExpression:
		 *    Expression <b>instanceof</b> Type
		 */
		Expression exp = node.getLeftOperand();
		Type type = node.getRightOperand();
		
		return false;
	}

	@Override
	public boolean visit(LambdaExpression node) {
		// Lambda: (argument) -> (body)
		List<?> parameters = node.parameters();//VariableDeclaration   arguments
		for (Object obj : parameters) {
			VariableDeclaration var = (VariableDeclaration) obj;

			if (var instanceof SingleVariableDeclaration) {
				/**
				 * SingleVariableDeclaration:
				 *    { ExtendedModifier } Type {Annotation} [ <b>...</b> ] Identifier { Dimension } [ <b>=</b> Expression ]
				 */
				SingleVariableDeclaration svd = (SingleVariableDeclaration) var;
				Type type = svd.getType();
				String identifier = svd.getName().getIdentifier(); // Identifier
				Vector<String> tokenV = new Vector<>();
				tokenV.add(type.toString());
				tokenV.add(identifier);

				Expression exp = svd.getInitializer();
				if (exp != null) {
				}
			} else if (var instanceof VariableDeclarationFragment) {
				/**
				 * VariableDeclarationFragment:
				 *    Identifier { Dimension } [ <b>=</b> Expression ]
				 * Dimension:
				 * 	{ Annotation } <b>[]</b>
				 */
				VariableDeclarationFragment fragment = (VariableDeclarationFragment) var;
				String identifier = fragment.getName().getIdentifier(); // Identifier
				Vector<String> tokenV = new Vector<String>();
				tokenV.add(identifier);
				List<?> dimensions = fragment.extraDimensions();

				if (dimensions != null) {
					int dimensionSize = dimensions.size();
				}
				Expression exp = fragment.getInitializer();
				if (exp != null) {
				}
			}
		}
		ASTNode body = node.getBody();
		if (body != null) {
			if (body instanceof Block) {
				// TODO
//				return super.visit((Block) body);
			} else if (body instanceof Expression) {
				Expression exp = (Expression) body;
			}
		}
		return false;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		/**
		 * MethodInvocation:
		 *     [ Expression <b>.</b> ]
		 *         [ <b>&lt;</b> Type { <b>,</b> Type } <b>&gt;</b> ]
		 *         Identifier <b>(</b> [ Expression { <b>,</b> Expression } ] <b>)</b>
		 */
		Expression firstExp = node.getExpression(); // may be null, get() / A.get();
		if (firstExp != null) {  // leftHandExp
		}

		String methodName = node.getName().toString();
		pushNode(node, methodName); // TODO
		
		List<?> arguments = node.arguments();
		if (arguments != null) {
			for (int i = 0, size = arguments.size(); i < size; i ++) {
				Expression exp = (Expression) arguments.get(i);
				pushNode(exp, exp.toString()); // TODO
//				exp.accept(this);
			}
		}
		popNode();
		return false;
	}
	
	@Override 
	public boolean visit(NullLiteral node) {
		// NullLiteral null;
		return false;
	}

	@Override
	public boolean visit(NumberLiteral node) {
		// NumberLiteral + number
		return false;
	}

	@Override
	public boolean visit(ParenthesizedExpression node) {
		// ( Expression )
		Expression exp = node.getExpression();
		return false;
	}

	@Override
	public boolean visit(PostfixExpression node) {
		// Expression PostfixOperator
		Expression exp = node.getOperand(); // leftHandExp
		/**
		 * PostfixOperator:
		 *    <b><code>++</code></b>  <code>INCREMENT</code>
		 *    <b><code>--</code></b>  <code>DECREMENT</code>
		 */
		PostfixExpression.Operator operator = node.getOperator();
		
		return false;
	}

	@Override
	public boolean visit(PrefixExpression node) {
		// PrefixOperator Expression
		/**
		 * PrefixOperator:
		 *    <b><code>++</code></b>  <code>INCREMENT</code>
		 *    <b><code>--</code></b>  <code>DECREMENT</code>
		 *    <b><code>+</code></b>  <code>PLUS</code>
		 *    <b><code>-</code></b>  <code>MINUS</code>
		 *    <b><code>~</code></b>  <code>COMPLEMENT</code>
		 *    <b><code>!</code></b>  <code>NOT</code>
		 */
		Expression exp = node.getOperand(); // rightHandExp
		PrefixExpression.Operator op = node.getOperator();
		// TODO
		pushNode(node, op.toString());
		pushNode(exp, exp.toString()); // TODO
//		exp.accept(this);
		popNode();
		return false;
	}

	@Override
	public boolean visit(QualifiedName node) {
		// Name <b>.</b> SimpleName
		Name name = node.getQualifier();
		String simpleName = node.getName().getIdentifier();
		
		if (name.isSimpleName()) {
		} else {
		}
		return false;
	}
	
	@Override
	public boolean visit(SimpleName node) {
		return false;
	}
	
	@Override
	public boolean visit(StringLiteral node) {
		// StringLiteral + "......"
		return false;
	}

	@Override
	public boolean visit(SuperFieldAccess node) {
		// [ ClassName <b>.</b> ] <b>super</b> <b>.</b> Identifier
		Name className = node.getQualifier(); // may be null, qualifier
		if (className != null) {
			if (className.isSimpleName()) {
			} else {
			}
		}

		String identifier = node.getName().getIdentifier(); // identifier
		return false;
	}

	@Override
	public boolean visit(SuperMethodInvocation node) {
		/**
		 * Simple or qualified "super" method invocation expression AST node type.
		 * SuperMethodInvocation:
		 *     [ ClassName <b>.</b> ] <b>super</b> <b>.</b>
		 *         [ <b>&lt;</b> Type { <b>,</b> Type } <b>&gt;</b> ]
		 *         Identifier <b>(</b> [ Expression { <b>,</b> Expression } ] <b>)</b>
		 */
		Name className = node.getQualifier(); // may be null
		if (className != null) {
			if (className.isSimpleName()) {
			} else {
			}
		}
		String methodName = node.getName().getIdentifier(); /// method name, identifier
		
		List<?> arguments = node.arguments();
		if (arguments != null) {
			for (int i = 0, size = arguments.size(); i < size; i ++ ) {
				Expression exp = (Expression) arguments.get(i);
			}
		}
		return false;
	}

	@Override
	public boolean visit(ThisExpression node) {
		Name className = node.getQualifier(); // may be null, qualifier
		if (className != null) {
			if (className.isSimpleName()) {
			} else {
			}
		}
		// ThisExpression + this
		return false;
	}
	
//
//	@Override
//	public boolean visit(VariableDeclarationExpression node) {
//		/**
//		 * VariableDeclarationExpression:
//		 *    { ExtendedModifier } Type VariableDeclarationFragment
//		 *         { <b>,</b> VariableDeclarationFragment }
//		 */
//		String modifier = parseModifier(node.modifiers());
//		myExpression.setModifier(modifier);
//		Type type = node.getType(); // qualifier
//		myExpression.setExpressionType(ExpressionType.VariableDeclarationExpression);
//		myExpression.setQualifier(type.toString());
//		if (!"".equals(modifier)) {
//			tokenVec.addAll(Arrays.asList(modifier.split(", ")));
//		}
//		tokenVec.add(type.toString());
//		
//		List<?> fragments = node.fragments(); //subExpression
//		for (Object obj : fragments) {
//			/**
//			 * VariableDeclarationFragment:
//			 *    Identifier { Dimension } [ <b>=</b> Expression ]
//			 * Dimension:
//			 * 	{ Annotation } <b>[]</b>
//			 */
//			MyExpression myExp = new MyExpression();
//			myExp.setExpressionType(ExpressionType.VariableDeclarationFragment);
//			VariableDeclarationFragment fragment = (VariableDeclarationFragment) obj;
//			String identifier = fragment.getName().getIdentifier(); // Identifier
//			myExp.setIdentifier(identifier);
//			Vector<String> tokenV = new Vector<String>();
//			tokenV.add(identifier);
//
//			Expression exp = fragment.getInitializer();
//			if (exp != null) {
//				ExpressionVisitor expVisitor = new ExpressionVisitor();
//				exp.accept(expVisitor);
//				myExp.setRightHandExpression(expVisitor.myExpression);
//				tokenV.addAll(expVisitor.getTokenVec());
//			}
//			myExp.setTokenVec(tokenV);
//			tokenVec.addAll(tokenV);
//			myExpression.getSubExpresions().add(myExp);
//		}
//		myExpression.setTokenVec(tokenVec);
//		return false;
//	}
}
