package edu.lu.uni.serval.gen.jdt.exp;

import java.util.List;

import com.github.gumtreediff.gen.jdt.cd.CdJdtVisitor;

import org.eclipse.jdt.core.dom.*;

/**
 * Extend CdJdtVisitor by visiting Expressions and adding expression type to tree label.
 * 
 * @author kui.liu
 *
 */
public class ExpJdtVisitor extends CdJdtVisitor {
	////---------------Expressions---------------
	//  ----------------Annotation---------------
	@Override
	public boolean visit(MarkerAnnotation node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void endVisit(MarkerAnnotation node) {
	}

	@Override
	public boolean visit(NormalAnnotation node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void endVisit(NormalAnnotation node) {
	}

	@Override
	public boolean visit(SingleMemberAnnotation node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public void endVisit(SingleMemberAnnotation node) {
	}
	// ---------------Annotation---------------
	
    @Override
	public boolean visit(ArrayAccess node) {
		pushNode(node, node.toString());
    	Expression arrayExpression = node.getArray();
		Expression indexExpression = node.getIndex();
		arrayExpression.accept(this);
		indexExpression.accept(this);
		return false;
	}

	@Override
	public void endVisit(ArrayAccess node) {
		popNode();
	}

	@Override
	public boolean visit(ArrayCreation node) {
		pushNode(node, node.toString());
		ArrayType arrayType = node.getType();
		arrayType.accept(this);
		//List<?> dimensions = node.dimensions();// TODO
		ArrayInitializer initializer = node.getInitializer();
		if (initializer != null) {
			initializer.accept(this);
		}
		return false;
	}

	@Override
	public void endVisit(ArrayCreation node) {
		popNode();
	}

	@Override
	public boolean visit(ArrayInitializer node) {
		pushNode(node, node.toString());
		return false;
	}

	@Override
	public void endVisit(ArrayInitializer node) {
		popNode();
	}

	@Override
	public boolean visit(Assignment node) {
		pushNode(node, node.getOperator().toString());
		Expression leftHandExp = node.getLeftHandSide();
		leftHandExp.accept(this);
		String op = node.getOperator().toString();
		push(0, "Operator", op, leftHandExp.getStartPosition() + leftHandExp.getLength(), op.length());
		popNode();
		Expression rightHandExp = node.getRightHandSide();
		rightHandExp.accept(this);
		return false;
	}

	@Override
	public void endVisit(Assignment node) {
		popNode();
	}

	@Override
	public boolean visit(BooleanLiteral node) {
		pushNode(node, node.toString());
		return false;
	}

	@Override
	public void endVisit(BooleanLiteral node) {
		popNode();
	}

	@Override
	public boolean visit(CastExpression node) {
		pushNode(node, node.toString());
		Type castType = node.getType();
		castType.accept(this);
		Expression exp = node.getExpression();
		exp.accept(this);
		return false;
	}

	@Override
	public void endVisit(CastExpression node) {
		popNode();
	}

	@Override
	public boolean visit(CharacterLiteral node) {
		pushNode(node, node.getEscapedValue());
		return false;
	}

	@Override
	public void endVisit(CharacterLiteral node) {
		popNode();
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		pushNode(node, node.toString());
		Expression exp = node.getExpression();
		if (exp != null) {
			exp.accept(this);
		}
		Type type = node.getType();
		type.accept(this);
		List<?> arguments = node.arguments();
		visitList(arguments);
		return false;
	}

	@Override
	public void endVisit(ClassInstanceCreation node) {
		popNode();
	}
	
	@Override
	public boolean visit(ConditionalExpression node) {
		pushNode(node, node.toString());
		Expression conditionalExp = node.getExpression();
		Expression thenExp = node.getThenExpression(); 
		Expression elseExp = node.getElseExpression();
		conditionalExp.accept(this);
		thenExp.accept(this);
		elseExp.accept(this);
		return false;
	}

	@Override
	public void endVisit(ConditionalExpression node) {
		popNode();
	}

	@Override
	public boolean visit(FieldAccess node) {
		pushNode(node, node.toString());
		Expression exp = node.getExpression();
		exp.accept(this);
		SimpleName identifier = node.getName();
		identifier.accept(this);
		return false;
	}

	@Override
	public void endVisit(FieldAccess node) {
		popNode();
	}

	@Override
	public boolean visit(InfixExpression node) {
		InfixExpression.Operator infixOperator = node.getOperator();
		pushNode(node, infixOperator.toString());
		Expression leftExp = node.getLeftOperand();
		List<?> extendedOperands = node.extendedOperands();
		Expression rightExp;
		if (extendedOperands != null && extendedOperands.size() > 0) {
			String nodeStr = node.toString();
			nodeStr = nodeStr.substring(leftExp.toString().length()).trim();
			nodeStr = nodeStr.substring(infixOperator.toString().length()).trim();
			ExpressionRebuilder expRebuilder = new ExpressionRebuilder();
			rightExp = expRebuilder.createExpression(nodeStr);
		} else {
			rightExp = node.getRightOperand();
		}
		leftExp.accept(this);
		rightExp.accept(this);
		return false;
	}

	@Override
	public void endVisit(InfixExpression node) {
		popNode();
	}

	@Override
	public boolean visit(InstanceofExpression node) {
		pushNode(node, node.toString());
		Expression exp = node.getLeftOperand();
		exp.accept(this);
		Type type = node.getRightOperand();
		type.accept(this);
		return false;
	}

	@Override
	public void endVisit(InstanceofExpression node) {
		popNode();
	}

	@Override
	public boolean visit(LambdaExpression node) {
		pushNode(node, node.toString());
		List<?> parameters = node.parameters();
		visitList(parameters);
		return false;
	}

	@Override
	public void endVisit(LambdaExpression node) {
		popNode();
	}

	@Override
	public boolean visit(MethodInvocation node) {
		pushNode(node, node.toString());
		Expression exp = node.getExpression();
		if (exp != null) {
			exp.accept(this);
		}
		List<?> arguments = node.arguments();
		visitList(arguments);
		return false;
	}

	@Override
	public void endVisit(MethodInvocation node) {
		popNode();
	}
	
	// ----------------MethodReference----------------
	@Override
	public boolean visit(CreationReference node) {
		pushNode(node, node.toString());
		Type type = node.getType();
		type.accept(this);
		List<?> typeArguments =  node.typeArguments();
		for (Object obj : typeArguments) {
			Type typeArgument = (Type) obj;
			typeArgument.accept(this);
		}
		return false;
	}

	@Override
	public void endVisit(CreationReference node) {
		popNode();
	}

	@Override
	public boolean visit(ExpressionMethodReference node) {
		pushNode(node, node.getName().getFullyQualifiedName());
		Expression exp = node.getExpression();
		exp.accept(this);
		List<?> typeArguments = node.typeArguments();
		for (Object obj : typeArguments) {
			Type typeArgument = (Type) obj;
			typeArgument.accept(this);
		}
		return false;
	}

	@Override
	public void endVisit(ExpressionMethodReference node) {
		popNode();
	}

	@Override
	public boolean visit(SuperMethodReference node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public void endVisit(SuperMethodReference node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}
	
	@Override
	public boolean visit(TypeMethodReference node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public void endVisit(TypeMethodReference node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}
	// ----------------MethodReference----------------
	
	// ----------------Name----------------
	@Override
	public boolean visit(QualifiedName node) {
		pushNode(node, node.getFullyQualifiedName());
		Name name = node.getQualifier();
		SimpleName simpleName = node.getName();
		name.accept(this);
		simpleName.accept(this);
		return false;
	}

	@Override
	public void endVisit(QualifiedName node) {
		popNode();
	}

	@Override
	public boolean visit(SimpleName node) {
		pushNode(node, node.getFullyQualifiedName());
		return false;
	}

	@Override
	public void endVisit(SimpleName node) {
		popNode();
	}
	// ----------------Name----------------

	@Override
	public boolean visit(NullLiteral node) {
		pushNode(node, "null");
		return false;
	}

	@Override
	public void endVisit(NullLiteral node) {
		popNode();
	}

	@Override
	public boolean visit(NumberLiteral node) {
		pushNode(node, node.getToken());
		return false;
	}

	@Override
	public void endVisit(NumberLiteral node) {
		popNode();
	}

	@Override
	public boolean visit(ParenthesizedExpression node) {
		pushNode(node, node.toString());
		Expression exp = node.getExpression();
		exp.accept(this);
		return false;
	}

	@Override
	public void endVisit(ParenthesizedExpression node) {
		popNode();
	}

	@Override
	public boolean visit(PostfixExpression node) {
		pushNode(node, node.getOperator().toString());
		Expression exp = node.getOperand();
		exp.accept(this);
		return false;
	}

	@Override
	public void endVisit(PostfixExpression node) {
		popNode();
	}

	@Override
	public boolean visit(PrefixExpression node) {
		pushNode(node, node.getOperator().toString());
		Expression exp = node.getOperand();
		exp.accept(this);
		return false;
	}

	@Override
	public void endVisit(PrefixExpression node) {
		popNode();
	}
	
	@Override
	public boolean visit(StringLiteral node) {
		pushNode(node, node.getEscapedValue());
		return false;
	}

	@Override
	public void endVisit(StringLiteral node) {
		popNode();
	}

	@Override
	public boolean visit(SuperFieldAccess node) {
		pushNode(node, node.toString());
		Name className = node.getQualifier();
		SimpleName identifier = node.getName();
		if (className != null) {
			className.accept(this);
		}
		identifier.accept(this);
		return false;
	}

	@Override
	public void endVisit(SuperFieldAccess node) {
		popNode();
	}

	@Override
	public boolean visit(SuperMethodInvocation node) {
		pushNode(node, node.toString());
		Name className = node.getQualifier();
		if (className != null) {
			className.accept(this);
		}
		SimpleName methodName = node.getName();
		methodName.accept(this);
		List<?> arguments = node.arguments();
		visitList(arguments);
		return false;
	}

	@Override
	public void endVisit(SuperMethodInvocation node) {
		popNode();
	}

	@Override
	public boolean visit(ThisExpression node) {
		return false;
	}

	@Override
	public void endVisit(ThisExpression node) {
	}

    @Override
    public boolean visit(TypeLiteral node) {
        pushNode(node, node.toString());
        return false;
    }

    @Override
    public void endVisit(TypeLiteral node) {
        popNode();
    }
	
    @Override
    public boolean visit(VariableDeclarationExpression node) {
        pushNode(node, node.toString());
        visitList(node.modifiers());
        node.getType().accept(this);
        visitList(node.fragments());
        return false;
    }

    @Override
    public void endVisit(VariableDeclarationExpression node) {
        popNode();
    }
    ////---------------End of Expressions---------------
    
    ////////
    @Override
   	public boolean visit(ArrayType node) {
   		pushNode(node, node.toString());
   		Type type = node.getElementType();
   		type.accept(this);
//   		List<?> dimensions = node.dimensions();
   		return false;
   	}

   	@Override
   	public void endVisit(ArrayType node) {
   		popNode();
   	}
   	
	@Override
	public boolean visit(Dimension node) {
		// TODO
		return false;
	}

	@Override
	public void endVisit(Dimension node) {
	}

	/////////////
    @Override
	public boolean visit(ImportDeclaration node) {
    	String nodeStr = node.toString();
    	nodeStr = nodeStr.substring(0, nodeStr.length() - 1);
    	pushNode(node, nodeStr);
    	return false;
    }
    
    @Override
    public void endVisit(ImportDeclaration node) {
    	popNode();
    }
    
    @Override
    public boolean visit(Javadoc node) {
        return false;
    }

    @Override
    public void endVisit(Javadoc node) {
    }

    @Override
    public boolean visit(TypeDeclaration node) {
    	pushNode(node, node.getName().toString());
        return true;
    }

    @Override
    public boolean visit(FieldDeclaration node) {
    	String nodeStr = node.toString();
    	nodeStr = nodeStr.substring(0, nodeStr.length() - 1);
    	pushNode(node, nodeStr);
        visitList(node.modifiers());
        node.getType().accept(this);
        visitList(node.fragments());
        return false;
    }

    @Override
	public boolean visit(Initializer node) {
    	pushNode(node, "Initializer");
		Block body = node.getBody();
		if (body != null) {
			body.accept(this);
		}
		return false;
	}
    
    @Override
    public void endVisit(Initializer node) {
    	popNode();
    }
    

	@Override
	public boolean visit(MethodDeclaration node) {
		List<?> modifiers = node.modifiers();
		Type returnType = node.isConstructor() ? null : node.getReturnType2();
		List<?> typeParameters = node.typeParameters();
		SimpleName methodName = node.getName();
		List<?> parameters = node.parameters();
		List<?> exceptionTypes = node.thrownExceptionTypes();
		
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
		pushNode(node, methodLabel);
		visitList(modifiers);
		if (returnType != null) {
			returnType.accept(this);
		}
		visitList(typeParameters);
		node.getName().accept(this);
		visitList(parameters);
		visitList(exceptionTypes);

		// The body can be null when the method declaration is from a interface
		if (node.getBody() != null) {
			node.getBody().accept(this);
		}
		return false;

	}

    //-----------------Types-----------------
    @Override
    public boolean visit(NameQualifiedType node) {
    	// Name <b>.</b> { Annotation } SimpleName
    	pushNode(node, node.toString());
    	return false;
    }
    
    @Override
    public void endVisit(NameQualifiedType node) {
    	popNode();
    }
    
    @Override
    public boolean visit(ParameterizedType node) {
        pushNode(node, node.toString());
        node.getType().accept(this);
        visitList(node.typeArguments());
        return false;
    }

    @Override
    public boolean visit(PrimitiveType node) {
        pushNode(node, node.getPrimitiveTypeCode().toString());
        return false;
    }

    @Override
    public boolean visit(QualifiedType node) {
        pushNode(node, node.toString());
        return false;
    }

    @Override
    public boolean visit(SimpleType node) {
        pushNode(node, node.getName().getFullyQualifiedName());
        return false;
    }
    //-----------------Types-----------------
    
    @Override
    public boolean visit(SingleVariableDeclaration node) {
    	Type type = node.getType();
    	SimpleName variableName = node.getName();
    	Expression exp = node.getInitializer();
        pushNode(node, node.toString());
        type.accept(this);
        variableName.accept(this);
        if (exp != null) {
        	exp.accept(this);
        }
        return false;
    }
    
    @Override
    public boolean visit(VariableDeclarationFragment node) {
    	pushNode(node, node.toString());
        Expression exp = node.getInitializer();
        if (exp != null) {
        	exp.accept(this);
        }
        return false;
    }

    /////////////////////
    private void visitList(List<?> list) {
        for (Object obj : list) {
        	ASTNode node = (ASTNode) obj;
            (node).accept(this);
        }
    }
    ///////////////////
    
    ////***************BODY VISITOR*************************
    private static final String COLON = ":";

    ////-------------------Statements-------------------
    @Override
    public boolean visit(AssertStatement node) {
    	Expression exp = node.getExpression();
    	Expression msg = node.getMessage();
        String value = exp.getClass().getSimpleName() + COLON + exp.toString();
        if (msg != null) {
            value += ", Msg-" + msg.getClass().getSimpleName() + COLON + msg.toString();
            pushNode(node, value);
            exp.accept(this);
            msg.accept(this);
        } else {
        	pushNode(node, value);
            exp.accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(ConstructorInvocation node) {
        String nodeStr = node.toString();
        nodeStr = nodeStr.substring(0, nodeStr.length() - 1);
        pushNode(node, nodeStr);
        List<?> typeArguments = node.typeArguments();
        List<?> arguments = node.arguments();
        visitList(typeArguments);
        visitList(arguments);
        return false;
    }

    @Override
    public boolean visit(DoStatement node) {
    	Expression exp = node.getExpression();
        pushNode(node, exp.getClass().getSimpleName() + COLON + exp.toString());
        exp.accept(this);
        node.getBody().accept(this);
        return false;
    }

    @Override
    public boolean visit(EmptyStatement node) {
        return false;
    }

    @Override
    public void endVisit(EmptyStatement node) {
    }

    @Override
    public boolean visit(EnhancedForStatement node) {
    	SingleVariableDeclaration parameter = node.getParameter();
    	Expression exp = node.getExpression();
        pushNode(node, parameter.toString() + ", " + exp.getClass().getSimpleName() + COLON + exp.toString());
        parameter.accept(this);
        exp.accept(this);
        node.getBody().accept(this);
        return false;
    }

    @Override
    public boolean visit(ExpressionStatement node) {
    	Expression exp = node.getExpression();
        pushNode(node, exp.getClass().getSimpleName() + COLON + exp.toString());
        exp.accept(this);
        return false;
    }

    @Override
    public boolean visit(ForStatement node) {
        String value = "";
        List<?> init = node.initializers();
		Expression exp = node.getExpression();
		List<?> update = node.updaters();
        value += init.toString() + ";";
		if (exp != null) {
			value += exp.toString() + "; ";
		}
		value += update.toString();
        
        pushNode(node, value);
		visitList(init);
		if (exp != null) {
			exp.accept(this);
		}
		visitList(update);
		
		node.getBody().accept(this);
        return false;
    }

    @Override
    public boolean visit(IfStatement node) {
        Expression exp = node.getExpression();
        pushNode(node, exp.getClass().getSimpleName() + COLON + exp.toString());
        
        exp.accept(this);
        Statement stmt = node.getThenStatement();
        if (stmt != null) {
            pushNode(stmt, "ThenBlock");
            stmt.accept(this);
            popNode();
        }

        stmt = node.getElseStatement();
        if (stmt != null) {
            pushNode(stmt, "ElseBlock");
            node.getElseStatement().accept(this);
            popNode();
        }
        return false;
    }

    @Override
    public boolean visit(ReturnStatement node) {
    	Expression exp = node.getExpression();
    	if (exp != null) {
    		pushNode(node, exp.getClass().getSimpleName() + COLON + exp.toString());
    		exp.accept(this);
    	} else {
            pushNode(node, "");
    	}
        return false;
    }

    @Override
    public boolean visit(SuperConstructorInvocation node) {
    	String nodeStr = node.toString();
    	nodeStr = nodeStr.substring(0, nodeStr.length() - 1);
        pushNode(node, node.toString());
        visitList(node.arguments());
        return false;
    }

    @Override
    public boolean visit(SwitchCase node) {
    	Expression exp = node.getExpression();
    	if (exp != null) {
    		pushNode(node, exp.getClass().getSimpleName() + COLON + exp.toString());
    		exp.accept(this);
    	} else {
    		pushNode(node, "default");
    	}
        return false;
    }

    @Override
    public boolean visit(SwitchStatement node) {
    	Expression exp = node.getExpression();
        pushNode(node, exp.getClass().getSimpleName() + COLON + exp.toString());
        exp.accept(this);
        visitList(node.statements());
        return false;
    }

    @Override
    public boolean visit(SynchronizedStatement node) {
    	Expression exp = node.getExpression();
        pushNode(node, exp.getClass().getSimpleName() + COLON + exp.toString());
        exp.accept(this);
        node.getBody().accept(this);
        return false;
    }

    @Override
    public boolean visit(ThrowStatement node) {
    	Expression exp = node.getExpression();
        pushNode(node, exp.getClass().getSimpleName() + COLON + exp.toString());
        exp.accept(this);
        return false;
    }

    @Override
    public boolean visit(TryStatement node) {
    	List<?> resources = node.resources();
    	if (resources != null) {
    		pushNode(node, "try:" + resources.toString());
    		visitList(resources);
    	} else {
    		pushNode(node, "try");
    	}

    	node.getBody().accept(this);

        visitList(node.catchClauses());

        Statement stmt = node.getFinally();
        if (stmt != null) {
            pushNode(stmt, "Finally");
            stmt.accept(this);
            popNode();
        }
        return false;
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
    	String nodeStr = node.toString();
    	nodeStr = nodeStr.substring(0, nodeStr.length() - 1);
        pushNode(node, nodeStr);
    	Type type = node.getType();
    	type.accept(this);
    	List<?> fragments = node.fragments();
    	visitList(fragments);
        return false;
    }

    @Override
    public boolean visit(WhileStatement node) {
    	Expression exp = node.getExpression();
        pushNode(node, exp.getClass().getSimpleName() + COLON + exp.toString());
        exp.accept(this);
        node.getBody().accept(this);
        return false;
    }

    @Override
    public void endVisit(WhileStatement node) {
        popNode();
    }

}
