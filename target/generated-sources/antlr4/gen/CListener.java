// Generated from gen/C.g4 by ANTLR 4.13.1
package gen;

package gen;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CParser}.
 */
public interface CListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(CParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(CParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(CParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(CParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#preprocessorDirective}.
	 * @param ctx the parse tree
	 */
	void enterPreprocessorDirective(CParser.PreprocessorDirectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#preprocessorDirective}.
	 * @param ctx the parse tree
	 */
	void exitPreprocessorDirective(CParser.PreprocessorDirectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#includeDirective}.
	 * @param ctx the parse tree
	 */
	void enterIncludeDirective(CParser.IncludeDirectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#includeDirective}.
	 * @param ctx the parse tree
	 */
	void exitIncludeDirective(CParser.IncludeDirectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#libraryPath}.
	 * @param ctx the parse tree
	 */
	void enterLibraryPath(CParser.LibraryPathContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#libraryPath}.
	 * @param ctx the parse tree
	 */
	void exitLibraryPath(CParser.LibraryPathContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#defineDirective}.
	 * @param ctx the parse tree
	 */
	void enterDefineDirective(CParser.DefineDirectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#defineDirective}.
	 * @param ctx the parse tree
	 */
	void exitDefineDirective(CParser.DefineDirectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#structDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterStructDeclaration(CParser.StructDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#structDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitStructDeclaration(CParser.StructDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#unionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterUnionDeclaration(CParser.UnionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#unionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitUnionDeclaration(CParser.UnionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDeclaration(CParser.FunctionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDeclaration(CParser.FunctionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void enterParameterList(CParser.ParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void exitParameterList(CParser.ParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(CParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(CParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(CParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(CParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(CParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(CParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(CParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(CParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(CParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(CParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(CParser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(CParser.WhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#doWhileStatement}.
	 * @param ctx the parse tree
	 */
	void enterDoWhileStatement(CParser.DoWhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#doWhileStatement}.
	 * @param ctx the parse tree
	 */
	void exitDoWhileStatement(CParser.DoWhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#forStatement}.
	 * @param ctx the parse tree
	 */
	void enterForStatement(CParser.ForStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#forStatement}.
	 * @param ctx the parse tree
	 */
	void exitForStatement(CParser.ForStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#forInit}.
	 * @param ctx the parse tree
	 */
	void enterForInit(CParser.ForInitContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#forInit}.
	 * @param ctx the parse tree
	 */
	void exitForInit(CParser.ForInitContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#forCond}.
	 * @param ctx the parse tree
	 */
	void enterForCond(CParser.ForCondContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#forCond}.
	 * @param ctx the parse tree
	 */
	void exitForCond(CParser.ForCondContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#forUpdate}.
	 * @param ctx the parse tree
	 */
	void enterForUpdate(CParser.ForUpdateContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#forUpdate}.
	 * @param ctx the parse tree
	 */
	void exitForUpdate(CParser.ForUpdateContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#declarationNoSemi}.
	 * @param ctx the parse tree
	 */
	void enterDeclarationNoSemi(CParser.DeclarationNoSemiContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#declarationNoSemi}.
	 * @param ctx the parse tree
	 */
	void exitDeclarationNoSemi(CParser.DeclarationNoSemiContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#assignmentNoSemi}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentNoSemi(CParser.AssignmentNoSemiContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#assignmentNoSemi}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentNoSemi(CParser.AssignmentNoSemiContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#switchStatement}.
	 * @param ctx the parse tree
	 */
	void enterSwitchStatement(CParser.SwitchStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#switchStatement}.
	 * @param ctx the parse tree
	 */
	void exitSwitchStatement(CParser.SwitchStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#caseBlock}.
	 * @param ctx the parse tree
	 */
	void enterCaseBlock(CParser.CaseBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#caseBlock}.
	 * @param ctx the parse tree
	 */
	void exitCaseBlock(CParser.CaseBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#breakStatement}.
	 * @param ctx the parse tree
	 */
	void enterBreakStatement(CParser.BreakStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#breakStatement}.
	 * @param ctx the parse tree
	 */
	void exitBreakStatement(CParser.BreakStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(CParser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(CParser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(CParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(CParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#logicalOrExpr}.
	 * @param ctx the parse tree
	 */
	void enterLogicalOrExpr(CParser.LogicalOrExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#logicalOrExpr}.
	 * @param ctx the parse tree
	 */
	void exitLogicalOrExpr(CParser.LogicalOrExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#logicalAndExpr}.
	 * @param ctx the parse tree
	 */
	void enterLogicalAndExpr(CParser.LogicalAndExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#logicalAndExpr}.
	 * @param ctx the parse tree
	 */
	void exitLogicalAndExpr(CParser.LogicalAndExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#equalityExpr}.
	 * @param ctx the parse tree
	 */
	void enterEqualityExpr(CParser.EqualityExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#equalityExpr}.
	 * @param ctx the parse tree
	 */
	void exitEqualityExpr(CParser.EqualityExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#relationalExpr}.
	 * @param ctx the parse tree
	 */
	void enterRelationalExpr(CParser.RelationalExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#relationalExpr}.
	 * @param ctx the parse tree
	 */
	void exitRelationalExpr(CParser.RelationalExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#additiveExpr}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpr(CParser.AdditiveExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#additiveExpr}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpr(CParser.AdditiveExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#multiplicativeExpr}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpr(CParser.MultiplicativeExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#multiplicativeExpr}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpr(CParser.MultiplicativeExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#unaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpr(CParser.UnaryExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#unaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpr(CParser.UnaryExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#postfixExpr}.
	 * @param ctx the parse tree
	 */
	void enterPostfixExpr(CParser.PostfixExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#postfixExpr}.
	 * @param ctx the parse tree
	 */
	void exitPostfixExpr(CParser.PostfixExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary(CParser.PrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary(CParser.PrimaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void enterArgumentList(CParser.ArgumentListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void exitArgumentList(CParser.ArgumentListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(CParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(CParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#baseType}.
	 * @param ctx the parse tree
	 */
	void enterBaseType(CParser.BaseTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#baseType}.
	 * @param ctx the parse tree
	 */
	void exitBaseType(CParser.BaseTypeContext ctx);
}