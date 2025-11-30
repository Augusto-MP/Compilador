package compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import gen.CBaseVisitor;
import gen.CParser;

public class MyCVisitor extends CBaseVisitor<Type> {

    private SymbolTable currentScope;
    private boolean isProcessingLHS = false;
    private StringBuilder llvmCode = new StringBuilder(); 
    private int tempCounter = 0;
    private int globalCounter = 0; 
    private List<String> globalDefs = new ArrayList<>();
    private Symbol currentFunction;
    private Stack<String> breakStack = new Stack<>();
    private Map<String, Integer> globalStringLengths = new HashMap<>();

    public MyCVisitor() {
        this.currentScope = new SymbolTable(null);
        this.currentFunction = null;
    }

    private String createGlobalString(String content) {
        String name = "@.str" + (globalCounter++);
        String fmt = content.replace("\\n", "\\0A");
        
        // Cálculo preciso do tamanho considerando escapes do LLVM (\0A conta como 1 char)
        int realLen = 0;
        for (int i = 0; i < fmt.length(); i++) {
            if (fmt.charAt(i) == '\\' && i + 2 < fmt.length() && fmt.charAt(i+1) == '0' && fmt.charAt(i+2) == 'A') {
                realLen++; 
                i += 2; // Pula os caracteres do escape
            } else {
                realLen++;
            }
        }
        realLen += 1; // +1 para o terminador nulo \00
        
        // Armazena o tamanho correto para uso posterior
        globalStringLengths.put(name, realLen);
        
        String def = name + " = private unnamed_addr constant [" + realLen + " x i8] c\"" + fmt + "\\00\"";
        globalDefs.add(def);
        return name;
    }
    
    private String nextTemp() {
        return "%t" + (tempCounter++);
    }

    private void emit(String code) {
        llvmCode.append(code + "\n");
    }
    
    private String toLLVMType(Type t) {
        if (t == null) return "void";
        
        if (t.name.endsWith("*")) {
            String baseTypeName = t.name.substring(0, t.name.length() - 1);
            return toLLVMType(new Type(baseTypeName)) + "*";
        }

        if (t.name.equals("int")) return "i32";
        if (t.name.equals("float")) return "float";
        if (t.name.equals("void")) return "void";
        if (t.name.equals("char")) return "i8"; 
        if (t.name.equals("string") || t.name.equals("char*")) return "i8*";
        
        if (t.name.startsWith("struct ")) {
            String structName = t.name.replace("struct ", "");
            return "%struct." + structName;
        }
        if (t.name.startsWith("union ")) {
            String unionName = t.name.replace("union ", "");
            return "%union." + unionName;
        }
        
        return "i32"; 
    }

    private String getTypeName(CParser.TypeContext ctx) {
        String text = ctx.baseType().getText();
        
        if (ctx.baseType().getChild(0).getText().equals("struct")) {
            text = "struct " + ctx.baseType().ID().getText();
        }
        else if (ctx.baseType().getChild(0).getText().equals("union")) {
            text = "union " + ctx.baseType().ID().getText();
        }
        
        for (int i = 1; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i).getText().equals("*")) {
                text += "*";
            }
        }
        return text;
    }
    
    public String getLLVMCode() {
        StringBuilder sb = new StringBuilder();
        for (String s : globalDefs) {
            sb.append(s).append("\n");
        }
        sb.append(llvmCode);
        return sb.toString();
    }

    // --- VISITOR METHODS ---

    @Override
    public Type visitFunctionDeclaration(CParser.FunctionDeclarationContext ctx) {
        String funcName = ctx.ID().getText();
        String returnTypeStr = ctx.type().getText();
        String returnTypeLLVM = toLLVMType(new Type(returnTypeStr));
        
        StringBuilder paramsLLVM = new StringBuilder();
        List<String> paramNames = new ArrayList<>();
        List<String> paramTypesLLVM = new ArrayList<>();
        
        SymbolTable functionScope = new SymbolTable(this.currentScope);
        
        int argCount = 0;
        if (ctx.parameterList() != null) {
            for (CParser.ParameterContext param : ctx.parameterList().parameter()) {
                String type = toLLVMType(new Type(param.type().getText()));
                String name = param.ID().getText();
                
                if (argCount > 0) paramsLLVM.append(", ");
                paramsLLVM.append(type).append(" %arg").append(argCount); // Nome seguro para argumento
                
                paramNames.add(name);
                paramTypesLLVM.add(type);
                
                functionScope.put(name, new Symbol(name, new Type(param.type().getText())));
                argCount++;
            }
        }
        this.tempCounter = argCount;
        
        Symbol functionSymbol = new Symbol(funcName, new Type(returnTypeStr));
        this.currentScope.put(funcName, functionSymbol);
        Symbol oldFunction = this.currentFunction;
        this.currentFunction = functionSymbol;

        emit("");
        emit("define " + returnTypeLLVM + " @" + funcName + "(" + paramsLLVM + ") {");
        emit("entry:"); 

        this.currentScope = functionScope; 
        
        for (int i = 0; i < paramNames.size(); i++) {
            String name = paramNames.get(i);
            String type = paramTypesLLVM.get(i);
            String valArg = "%arg" + i;
            String ptrVar = "%" + name + "_ptr";
            
            emit("  " + ptrVar + " = alloca " + type);
            emit("  store " + type + " " + valArg + ", " + type + "* " + ptrVar);
            
            Symbol s = this.currentScope.get(name);
            s.value = ptrVar; 
        }

        visit(ctx.block());

        if (returnTypeLLVM.equals("void")) {
            emit("  ret void");
        } else if (returnTypeLLVM.equals("i32") && funcName.equals("main")) {
            emit("  ret i32 0");
        }
        
        emit("}"); 
        
        this.currentScope = this.currentScope.getParent();
        this.currentFunction = oldFunction;
        return null;
    }

    @Override
    public Type visitStructDeclaration(CParser.StructDeclarationContext ctx) {
        String structName = ctx.ID().getText();
        String typeName = "struct " + structName;
        processCompositeType(typeName, ctx.declaration(), "%struct." + structName);
        return null;
    }
    
    @Override
    public Type visitUnionDeclaration(CParser.UnionDeclarationContext ctx) {
        String unionName = ctx.ID().getText();
        String typeName = "union " + unionName;
        processCompositeType(typeName, ctx.declaration(), "%union." + unionName);
        return null;
    }

    private void processCompositeType(String typeName, List<CParser.DeclarationContext> declarations, String llvmTypeName) {
        Type type = new Type(typeName);
        type.members = new SymbolTable(null); 
        SymbolTable previousScope = this.currentScope;
        this.currentScope = type.members;

        StringBuilder llvmBody = new StringBuilder();
        int index = 0;

        for (CParser.DeclarationContext decl : declarations) {
            String fieldTypeName = decl.type().getText();
            Type fieldType = new Type(fieldTypeName);
            String llvmType = toLLVMType(fieldType);
            
            if (index > 0) llvmBody.append(", ");
            llvmBody.append(llvmType);

            String fieldName = decl.ID().getText();
            Symbol fieldSymbol = new Symbol(fieldName, fieldType);
            fieldSymbol.memoryIndex = index++;
            
            this.currentScope.put(fieldName, fieldSymbol);
        }

        this.currentScope = previousScope;
        String definition = llvmTypeName + " = type { " + llvmBody + " }";
        globalDefs.add(definition);

        this.currentScope.put(typeName, new Symbol(typeName, type));
    }
    
    @Override
    public Type visitDeclaration(CParser.DeclarationContext ctx) {
        String varName = ctx.ID().getText();
        String cTypeName = getTypeName(ctx.type());
        
        Type typeObj = new Type(cTypeName); 
        String llvmType = toLLVMType(typeObj);
        
        boolean isArray = !ctx.INT().isEmpty();
        int sizeInt = 0;

        if (isArray) {
            String sizeStr = ctx.INT(0).getText();
            sizeInt = Integer.parseInt(sizeStr);
            llvmType = "[" + sizeStr + " x " + llvmType + "]";
        }

        Type varType = new Type(cTypeName + (isArray ? "[]" : ""));
        if (isArray) varType.arraySize = sizeInt;

        Symbol typeSymbol = this.currentScope.get(cTypeName);
        if (typeSymbol != null && typeSymbol.type.members != null) {
            varType.members = typeSymbol.type.members;
        }

        Symbol s = new Symbol(varName, varType);

        if (currentFunction == null) {
            String globalName = "@" + varName;
            String initVal = "zeroinitializer";
            if (!isArray && !llvmType.startsWith("%struct") && !llvmType.startsWith("%union")) {
                initVal = "0"; 
            }
            globalDefs.add(globalName + " = global " + llvmType + " " + initVal);
            s.value = globalName; 
        } else {
            String ptrVar = "%" + varName + "_ptr";
            emit("  " + ptrVar + " = alloca " + llvmType);
            s.value = ptrVar;
            
            if (ctx.expr() != null) {
                if (isArray) {
                    System.err.println("WARNING: Array initialization unsupported.");
                } else {
                    Type valType = visit(ctx.expr());
                    String valReg = valType.value.toString();
                    emit("  store " + llvmType + " " + valReg + ", " + llvmType + "* " + ptrVar);
                }
            }
        }
        
        s.initialized = (ctx.expr() != null); 
        this.currentScope.put(varName, s);
        
        return null;
    }
    
    @Override
    public Type visitAssignment(CParser.AssignmentContext ctx) {
        this.isProcessingLHS = true;
        Type lhsType = visit(ctx.unaryExpr());
        this.isProcessingLHS = false;
        
        if (lhsType == null || lhsType.name.equals("error")) return new Type("error");

        String ptrVar = lhsType.value.toString(); 
        String llvmType = toLLVMType(lhsType);

        Type rhsType = visit(ctx.expr());
        String valReg = rhsType.value.toString();

        emit("  store " + llvmType + " " + valReg + ", " + llvmType + "* " + ptrVar);
        return rhsType;
    }
    
    @Override
    public Type visitAssignmentNoSemi(CParser.AssignmentNoSemiContext ctx) {
        this.isProcessingLHS = true;
        Type lhsType = visit(ctx.unaryExpr());
        this.isProcessingLHS = false;
        
        if (lhsType == null || lhsType.name.equals("error")) return new Type("error");

        String ptrVar = lhsType.value.toString(); 
        String llvmType = toLLVMType(lhsType);

        Type rhsType = visit(ctx.expr());
        String valReg = rhsType.value.toString();

        emit("  store " + llvmType + " " + valReg + ", " + llvmType + "* " + ptrVar);
        return rhsType;
    }
    
    @Override
    public Type visitPostfixExpr(CParser.PostfixExprContext ctx) {
        // 1. Acesso a Array
        if (ctx.expr() != null && !ctx.expr().isEmpty()) {
            Type indexType = visit(ctx.expr(0));
            String indexVal = indexType.value.toString();

            boolean oldLhs = this.isProcessingLHS;
            this.isProcessingLHS = true;
            Type arrayType = visit(ctx.primary());
            this.isProcessingLHS = oldLhs;

            String arrayPtr = arrayType.value.toString();
            String elemPtr = nextTemp();

            int size = arrayType.arraySize;
            String baseTypeName = arrayType.name.replace("[]", "");
            String llvmBaseType = toLLVMType(new Type(baseTypeName)); 
            String llvmArrayType = "[" + size + " x " + llvmBaseType + "]";

            emit("  " + elemPtr + " = getelementptr inbounds " + llvmArrayType + ", " + llvmArrayType + "* " + arrayPtr + ", i32 0, i32 " + indexVal);

            if (oldLhs) {
                return new Type(baseTypeName, elemPtr);
            } else {
                String val = nextTemp();
                emit("  " + val + " = load " + llvmBaseType + ", " + llvmBaseType + "* " + elemPtr);
                return new Type(baseTypeName, val);
            }
        }
        
        // 2. Chamada de Função
        boolean isFunctionCall = false;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i).getText().equals("(")) {
                isFunctionCall = true;
                break;
            }
        }

        if (isFunctionCall) {
            String funcName = ctx.primary().getText();
            Symbol funcSymbol = this.currentScope.get(funcName);
            
            if (funcSymbol == null) {
                System.err.println("ERROR: Function '" + funcName + "' not declared.");
                return new Type("error");
            }

            StringBuilder argsLLVM = new StringBuilder();
            List<CParser.ExprContext> argsCtx = new ArrayList<>();
            if (ctx.argumentList() != null && !ctx.argumentList().isEmpty() && ctx.argumentList(0).expr() != null) {
                argsCtx = ctx.argumentList(0).expr();
            }
            
            for (int i = 0; i < argsCtx.size(); i++) {
                Type argType = visit(argsCtx.get(i));
                String argVal = argType.value.toString();
                String llvmType = toLLVMType(argType);

                if (i > 0) argsLLVM.append(", ");

                if (argType.name.equals("string")) {
                    String globalVar = createGlobalString(argVal);
                    // --- CORREÇÃO: Usar o tamanho armazenado no mapa ---
                    int size = globalStringLengths.get(globalVar); 
                    String strPtr = "getelementptr inbounds ([" + size + " x i8], [" + size + " x i8]* " + globalVar + ", i64 0, i64 0)";
                    argsLLVM.append("i8* ").append(strPtr);
                } else {
                    argsLLVM.append(llvmType).append(" ").append(argVal);
                }
            }

            String returnTypeLLVM = toLLVMType(funcSymbol.type);
            String callReg = "";
            
            if (!returnTypeLLVM.equals("void")) {
                callReg = nextTemp(); 
                // Suporte a funções variadic (printf/scanf)
                String varArgs = (funcName.equals("printf") || funcName.equals("scanf")) ? "i8*, ..." : "";
                
                if (!varArgs.isEmpty()) {
                     emit("  " + callReg + " = call " + returnTypeLLVM + " (" + varArgs + ") @" + funcName + "(" + argsLLVM + ")");
                } else {
                     emit("  " + callReg + " = call " + returnTypeLLVM + " @" + funcName + "(" + argsLLVM + ")");
                }
            } else {
                emit("  call " + returnTypeLLVM + " @" + funcName + "(" + argsLLVM + ")");
            }
            
            return new Type(funcSymbol.type.name, callReg);
        }

        // 3. Acesso a Membro de Struct / Union
        if (!ctx.ID().isEmpty()) {
            String memberName = ctx.ID(0).getText();
            boolean originalLHS = this.isProcessingLHS;
            this.isProcessingLHS = true;
            Type primaryType = visit(ctx.primary());
            this.isProcessingLHS = originalLHS; 
            
            String primaryName = ctx.primary().getText();

            if (primaryType.members == null) {
                System.err.println("ERROR: '" + primaryName + "' is not a struct/union.");
                return new Type("error");
            }
            
            Symbol member = primaryType.members.get(memberName);
            if (member == null) {
                System.err.println("ERROR: Field '" + memberName + "' does not exist.");
                return new Type("error");
            }

            String structPtr = primaryType.value.toString();
            String structLLVMType = toLLVMType(primaryType);
            String fieldLLVMType = toLLVMType(member.type);
            String ptrMember = nextTemp();
            
            if (primaryType.name.startsWith("union")) {
                emit("  " + ptrMember + " = bitcast " + structLLVMType + "* " + structPtr + " to " + fieldLLVMType + "*");
            } else {
                int fieldIndex = member.memoryIndex;
                emit("  " + ptrMember + " = getelementptr inbounds " + structLLVMType + ", " + structLLVMType + "* " + structPtr + ", i32 0, i32 " + fieldIndex);
            }

            if (originalLHS) {
                return new Type(member.type.name, ptrMember);
            } 
            else {
                String valReg = nextTemp();
                emit("  " + valReg + " = load " + fieldLLVMType + ", " + fieldLLVMType + "* " + ptrMember);
                return new Type(member.type.name, valReg);
            }
        }

        return visitChildren(ctx);
    }
    
    @Override
    public Type visitPrimary(CParser.PrimaryContext ctx) {
        if (ctx.ID() != null) {
            String varName = ctx.ID().getText();
            Symbol symbol = this.currentScope.get(varName);
            
            if (symbol == null) {
                System.err.println("SEMANTIC ERROR: Variable '" + varName + "' not declared.");
                return new Type("error");
            }

            if (symbol.isConstant) {
                return new Type("int", symbol.value);
            }
            
            if (symbol.value == null) {
                 System.err.println("ERROR: Symbol '" + varName + "' has no value.");
                 return new Type("error");
            }

            String ptrVar = symbol.value.toString();
            String llvmType = toLLVMType(symbol.type);
            
            Type t; 

            if (isProcessingLHS) {
                t = new Type(symbol.type.name, ptrVar);
            } 
            else {
                String tempReg = nextTemp(); 
                emit("  " + tempReg + " = load " + llvmType + ", " + llvmType + "* " + ptrVar);
                t = new Type(symbol.type.name, tempReg);
            }

            t.members = symbol.type.members; 
            t.arraySize = symbol.type.arraySize;

            return t;
            
        } else if (ctx.INT() != null) {
            return new Type("int", ctx.INT().getText());
        } else if (ctx.FLOAT() != null) {
            // --- CORREÇÃO: Converter float para Hexadecimal do LLVM ---
            float val = Float.parseFloat(ctx.FLOAT().getText());
            // LLVM espera a representação hex do double para garantir precisão
            double d = (double) val;
            String hex = "0x" + Long.toHexString(Double.doubleToLongBits(d)).toUpperCase();
            return new Type("float", hex);
            // ----------------------------------------------------------
        } else if (ctx.CHAR() != null) {
            String text = ctx.CHAR().getText(); 
            char c = text.charAt(1);            
            int ascii = (int) c;                
            return new Type("char", String.valueOf(ascii));
        } else if (ctx.STRING() != null) {
            String textoOriginal = ctx.STRING().getText();
            String textoLimpo = textoOriginal.substring(1, textoOriginal.length() - 1);
            return new Type("string", textoLimpo);
        } else if (ctx.expr() != null) {
            return visit(ctx.expr());
        }
        
        return visitChildren(ctx);
    }

    @Override
    public Type visitAdditiveExpr(CParser.AdditiveExprContext ctx) {
        Type result = visit(ctx.multiplicativeExpr(0));
        for (int i = 1; i < ctx.multiplicativeExpr().size(); i++) {
            String op = ctx.getChild(2 * i - 1).getText();
            Type next = visit(ctx.multiplicativeExpr(i));
            String llvmOp = "";
            String typeCode = toLLVMType(result);
            if (result.name.equals("int") || result.name.equals("char")) {
                llvmOp = op.equals("+") ? "add" : "sub";
            } else if (result.name.equals("float")) {
                llvmOp = op.equals("+") ? "fadd" : "fsub";
            } else return result;
            String tempReg = nextTemp();
            emit("  " + tempReg + " = " + llvmOp + " " + typeCode + " " + result.value + ", " + next.value);
            result = new Type(result.name, tempReg);
        }
        return result;
    }

    @Override
    public Type visitMultiplicativeExpr(CParser.MultiplicativeExprContext ctx) {
        Type result = visit(ctx.unaryExpr(0));
        for (int i = 1; i < ctx.unaryExpr().size(); i++) {
            String op = ctx.getChild(2 * i - 1).getText();
            Type next = visit(ctx.unaryExpr(i));
            String llvmOp = "";
            String typeCode = toLLVMType(result);
            if (result.name.equals("int") || result.name.equals("char")) {
                if (op.equals("*")) llvmOp = "mul";
                else if (op.equals("/")) llvmOp = "sdiv"; 
                else if (op.equals("%")) llvmOp = "srem";
            } else if (result.name.equals("float")) {
                if (op.equals("*")) llvmOp = "fmul";
                else if (op.equals("/")) llvmOp = "fdiv";
                else llvmOp = "frem";
            }
            String tempReg = nextTemp();
            emit("  " + tempReg + " = " + llvmOp + " " + typeCode + " " + result.value + ", " + next.value);
            result = new Type(result.name, tempReg);
        }
        return result;
    }
    
    @Override
    public Type visitRelationalExpr(CParser.RelationalExprContext ctx) {
        Type lhs = visit(ctx.additiveExpr(0));
        for (int i = 1; i < ctx.additiveExpr().size(); i++) {
            String op = ctx.getChild(2 * i - 1).getText(); 
            Type rhs = visit(ctx.additiveExpr(i));
            String tempReg = nextTemp();
            String instr = "";
            if (op.equals("<")) instr = "icmp slt";
            else if (op.equals(">")) instr = "icmp sgt";
            else if (op.equals("<=")) instr = "icmp sle";
            else if (op.equals(">=")) instr = "icmp sge";
            emit("  " + tempReg + " = " + instr + " i32 " + lhs.value + ", " + rhs.value);
            String zextReg = nextTemp();
            emit("  " + zextReg + " = zext i1 " + tempReg + " to i32");
            lhs = new Type("int", zextReg);
        }
        return lhs;
    }

    @Override
    public Type visitEqualityExpr(CParser.EqualityExprContext ctx) {
        Type lhs = visit(ctx.relationalExpr(0));
        for (int i = 1; i < ctx.relationalExpr().size(); i++) {
            String op = ctx.getChild(2 * i - 1).getText();
            Type rhs = visit(ctx.relationalExpr(i));
            String tempReg = nextTemp();
            String instr = op.equals("==") ? "icmp eq" : "icmp ne";
            emit("  " + tempReg + " = " + instr + " i32 " + lhs.value + ", " + rhs.value);
            String zextReg = nextTemp();
            emit("  " + zextReg + " = zext i1 " + tempReg + " to i32");
            lhs = new Type("int", zextReg);
        }
        return lhs;
    }

    @Override
    public Type visitLogicalAndExpr(CParser.LogicalAndExprContext ctx) {
        Type lhs = visit(ctx.equalityExpr(0));
        for (int i = 1; i < ctx.equalityExpr().size(); i++) {
            Type rhs = visit(ctx.equalityExpr(i));
            String t1 = nextTemp();
            emit("  " + t1 + " = icmp ne i32 " + lhs.value + ", 0");
            String t2 = nextTemp();
            emit("  " + t2 + " = icmp ne i32 " + rhs.value + ", 0");
            String tAnd = nextTemp();
            emit("  " + tAnd + " = and i1 " + t1 + ", " + t2);
            String tFinal = nextTemp();
            emit("  " + tFinal + " = zext i1 " + tAnd + " to i32");
            lhs = new Type("int", tFinal);
        }
        return lhs;
    }

    @Override
    public Type visitLogicalOrExpr(CParser.LogicalOrExprContext ctx) {
        Type lhs = visit(ctx.logicalAndExpr(0));
        for (int i = 1; i < ctx.logicalAndExpr().size(); i++) {
            Type rhs = visit(ctx.logicalAndExpr(i));
            String t1 = nextTemp();
            emit("  " + t1 + " = icmp ne i32 " + lhs.value + ", 0");
            String t2 = nextTemp();
            emit("  " + t2 + " = icmp ne i32 " + rhs.value + ", 0");
            String tOr = nextTemp();
            emit("  " + tOr + " = or i1 " + t1 + ", " + t2);
            String tFinal = nextTemp();
            emit("  " + tFinal + " = zext i1 " + tOr + " to i32");
            lhs = new Type("int", tFinal);
        }
        return lhs;
    }

    @Override
    public Type visitIfStatement(CParser.IfStatementContext ctx) {
        Type condType = visit(ctx.expr());
        String condReg = condType.value.toString();
        String boolReg = nextTemp();
        emit("  " + boolReg + " = icmp ne i32 " + condReg + ", 0");
        String labelThen = "L" + (tempCounter++);
        String labelElse = "L" + (tempCounter++);
        String labelMerge = "L" + (tempCounter++); 
        boolean hasElse = ctx.statement().size() > 1;
        String labelFalse = hasElse ? labelElse : labelMerge;
        emit("  br i1 " + boolReg + ", label %" + labelThen + ", label %" + labelFalse);
        emit(labelThen + ":");
        visit(ctx.statement(0));
        emit("  br label %" + labelMerge); 
        if (hasElse) {
            emit(labelElse + ":");
            visit(ctx.statement(1));
            emit("  br label %" + labelMerge);
        }
        emit(labelMerge + ":");
        return null;
    }

    @Override
    public Type visitWhileStatement(CParser.WhileStatementContext ctx) {
        String labelStart = "L" + (tempCounter++);
        String labelBody = "L" + (tempCounter++);
        String labelEnd = "L" + (tempCounter++);
        emit("  br label %" + labelStart);
        emit(labelStart + ":");
        Type condType = visit(ctx.expr());
        String condReg = condType.value.toString();
        String boolReg = nextTemp();
        emit("  " + boolReg + " = icmp ne i32 " + condReg + ", 0");
        emit("  br i1 " + boolReg + ", label %" + labelBody + ", label %" + labelEnd);
        emit(labelBody + ":");
        visit(ctx.statement());
        emit("  br label %" + labelStart); 
        emit(labelEnd + ":");
        return null;
    }

    @Override
    public Type visitDoWhileStatement(CParser.DoWhileStatementContext ctx) {
        String labelBody = "L" + (tempCounter++);
        String labelCond = "L" + (tempCounter++);
        String labelEnd = "L" + (tempCounter++);
        emit("  br label %" + labelBody);
        emit(labelBody + ":");
        visit(ctx.statement()); 
        emit("  br label %" + labelCond); 
        emit(labelCond + ":");
        Type condType = visit(ctx.expr());
        String condReg = condType.value.toString();
        String boolReg = nextTemp();
        emit("  " + boolReg + " = icmp ne i32 " + condReg + ", 0");
        emit("  br i1 " + boolReg + ", label %" + labelBody + ", label %" + labelEnd);
        emit(labelEnd + ":");
        return null;
    }

    @Override
    public Type visitForStatement(CParser.ForStatementContext ctx) {
        if (ctx.forInit() != null) visit(ctx.forInit());
        String labelCond = "L" + (tempCounter++);
        String labelBody = "L" + (tempCounter++);
        String labelUpdate = "L" + (tempCounter++);
        String labelEnd = "L" + (tempCounter++);
        emit("  br label %" + labelCond);
        emit(labelCond + ":");
        if (ctx.forCond() != null) {
            Type condType = visit(ctx.forCond());
            String condReg = condType.value.toString();
            String boolReg = nextTemp();
            emit("  " + boolReg + " = icmp ne i32 " + condReg + ", 0");
            emit("  br i1 " + boolReg + ", label %" + labelBody + ", label %" + labelEnd);
        } else {
            emit("  br label %" + labelBody);
        }
        emit(labelBody + ":");
        visit(ctx.statement());
        emit("  br label %" + labelUpdate); 
        emit(labelUpdate + ":");
        if (ctx.forUpdate() != null) visit(ctx.forUpdate());
        emit("  br label %" + labelCond); 
        emit(labelEnd + ":");
        return null;
    }
    
    @Override
    public Type visitSwitchStatement(CParser.SwitchStatementContext ctx) {
        Type condType = visit(ctx.expr());
        String condReg = condType.value.toString();
        String labelEnd = "L" + (tempCounter++);
        String labelDefault = "L" + (tempCounter++); 
        breakStack.push(labelEnd);
        List<String> caseValues = new ArrayList<>();
        List<String> caseLabels = new ArrayList<>();
        String actualDefaultLabel = labelDefault; 
        for (CParser.CaseBlockContext caseCtx : ctx.caseBlock()) {
            String labelCase = "L" + (tempCounter++);
            if (caseCtx.expr() != null) { 
                Type valType = visit(caseCtx.expr());
                caseValues.add(valType.value.toString()); 
                caseLabels.add(labelCase);
            } else { 
                actualDefaultLabel = labelCase;
            }
        }
        emit("  switch i32 " + condReg + ", label %" + actualDefaultLabel + " [");
        for (int i = 0; i < caseValues.size(); i++) {
            emit("    i32 " + caseValues.get(i) + ", label %" + caseLabels.get(i));
        }
        emit("  ]");
        int caseIndex = 0;
        for (CParser.CaseBlockContext caseCtx : ctx.caseBlock()) {
            String thisLabel;
            if (caseCtx.expr() != null) thisLabel = caseLabels.get(caseIndex++);
            else thisLabel = actualDefaultLabel;
            emit(thisLabel + ":");
            for (CParser.StatementContext stmt : caseCtx.statement()) visit(stmt);
            emit("  br label %" + labelEnd);
        }
        if (actualDefaultLabel.equals(labelDefault)) {
            emit(labelDefault + ":");
            emit("  br label %" + labelEnd);
        }
        emit(labelEnd + ":");
        breakStack.pop(); 
        return null;
    }

    @Override
    public Type visitCaseBlock(CParser.CaseBlockContext ctx) {
        if (ctx.expr() != null) visit(ctx.expr());
        for (CParser.StatementContext stmt : ctx.statement()) visit(stmt);
        return null;
    }
    
    @Override
    public Type visitBreakStatement(CParser.BreakStatementContext ctx) {
        if (!breakStack.isEmpty()) {
            String label = breakStack.peek();
            emit("  br label %" + label);
        }
        return null;
    }
    
    @Override
    public Type visitUnaryExpr(CParser.UnaryExprContext ctx) {
        Type currentType = visit(ctx.postfixExpr());

        for (int i = ctx.getChildCount() - 2; i >= 0; i--) {
            String operator = ctx.getChild(i).getText();
            
            if (operator.equals("&")) {
                if (ctx.postfixExpr().primary() != null && ctx.postfixExpr().primary().ID() != null) {
                    String varName = ctx.postfixExpr().primary().ID().getText();
                    Symbol symbol = currentScope.get(varName);
                    
                    if (symbol != null) {
                        String ptrVar = symbol.value.toString();
                        currentType = new Type(currentType.name + "*", ptrVar);
                    }
                } else {
                     System.err.println("ERROR: '&' operator supported only for variables.");
                }
            } 
            else if (operator.equals("*")) {
                if (currentType.name.endsWith("*")) {
                    String typeName = currentType.name.substring(0, currentType.name.length() - 1);
                    
                    // --- CORREÇÃO: Lógica de Dereferência (LHS vs RHS) ---
                    String llvmType;
                    if (isProcessingLHS) {
                        // LHS (*ptr = ...): Temos o endereço da variável ponteiro (i32**).
                        // Precisamos carregar o endereço apontado (i32*).
                        // Usamos o tipo ATUAL (int*) para o load.
                        llvmType = toLLVMType(currentType); 
                    } else {
                        // RHS (x = *ptr): Temos o valor do ponteiro (i32*).
                        // Precisamos carregar o valor final (i32).
                        // Usamos o tipo APONTADO (int) para o load.
                        llvmType = toLLVMType(new Type(typeName));
                    }
                    // -----------------------------------------------------
                    
                    String ptrReg = currentType.value.toString(); 
                    String tempReg = nextTemp();
                    
                    emit("  " + tempReg + " = load " + llvmType + ", " + llvmType + "* " + ptrReg);
                    
                    currentType = new Type(typeName, tempReg);
                } else {
                     System.err.println("ERROR: Attempt to dereference '*' a non-pointer.");
                }
            }
            else if (operator.equals("!")) {
                String valReg = currentType.value.toString();
                
                String tempCmp = nextTemp();
                emit("  " + tempCmp + " = icmp eq i32 " + valReg + ", 0");
                
                String tempZext = nextTemp();
                emit("  " + tempZext + " = zext i1 " + tempCmp + " to i32");
                
                currentType = new Type("int", tempZext);
            }
        }
        return currentType;
    }
    
    @Override
    public Type visitReturnStatement(CParser.ReturnStatementContext ctx) {
        if (ctx.expr() != null) {
            Type result = visit(ctx.expr());
            String valReg = result.value.toString();
            String typeCode = toLLVMType(result);
            emit("  ret " + typeCode + " " + valReg);
        } else {
            emit("  ret void");
        }
        return null;
    }
    
    @Override
    public Type visitDefineDirective(CParser.DefineDirectiveContext ctx) {
        String name = ctx.ID().getText();
        Type valueType = visit(ctx.expr()); 
        if (valueType == null) valueType = new Type("error");
        Symbol s = new Symbol(name, valueType);
        s.isConstant = true;
        s.value = valueType.value; 
        this.currentScope.put(name, s);
        return null;
    }

    @Override
    public Type visitIncludeDirective(CParser.IncludeDirectiveContext ctx) {
        if (ctx.libraryPath() != null && ctx.libraryPath().getText().equals("<stdio.h>")) {
            Type intType = new Type("int");
            Type stringType = new Type("string");

            List<Type> printfParams = new ArrayList<>();
            printfParams.add(stringType); 
            this.currentScope.put("printf", new Symbol("printf", intType, printfParams));
            
            List<Type> scanfParams = new ArrayList<>();
            scanfParams.add(stringType);
            this.currentScope.put("scanf", new Symbol("scanf", intType, scanfParams));
            this.currentScope.put("gets", new Symbol("gets", stringType, new ArrayList<>()));
            
            List<Type> putsParams = new ArrayList<>();
            putsParams.add(stringType);
            this.currentScope.put("puts", new Symbol("puts", intType, putsParams));
            
            emit("; --- External Declarations (stdio.h) ---");
            emit("declare i32 @printf(i8*, ...)");
            emit("declare i32 @scanf(i8*, ...)");
            emit("declare i32 @puts(i8*)");
            emit("declare i8* @gets(i8*)");
            emit(""); 
        }
        return null;
    }    
}