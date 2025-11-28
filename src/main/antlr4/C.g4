grammar C;

//@header {
//package gen;
//}

// --- Regras do Parser (Sintaxe) ---

program: (preprocessorDirective | structDeclaration | unionDeclaration | functionDeclaration | statement)+ ;

statement: declaration
         | assignment
         | ifStatement
         | whileStatement
         | doWhileStatement
         | forStatement
         | switchStatement
         | breakStatement
         | returnStatement
         | postfixExpr ';' 
         | block
         ;
// Novas regras para o pré-processador
preprocessorDirective: includeDirective | defineDirective ;

includeDirective: '#' 'include' (STRING | libraryPath) ;
libraryPath: '<' ID ('.' ID)? '>' ; // Ex: <stdio.h>

defineDirective: '#' 'define' ID expr ; // Ex: #define PI 3.14

structDeclaration: 'struct' ID '{' declaration+ '}' ';' ;
unionDeclaration: 'union' ID '{' declaration+ '}' ';' ;

functionDeclaration: type ID '(' parameterList? ')' block ;
parameterList: parameter (',' parameter)* ;
parameter: type ID ;

declaration: type ID ('[' INT ']')* ('=' expr)? ';' ;

assignment: unaryExpr '=' expr ';' ;

block: '{' statement* '}' ;

ifStatement: 'if' '(' expr ')' statement ('else' statement)? ;

whileStatement: 'while' '(' expr ')' statement ;

doWhileStatement: 'do' statement 'while' '(' expr ')' ';' ;

forStatement: 'for' '(' forInit? ';' forCond? ';' forUpdate? ')' statement ;

forInit: declarationNoSemi | assignmentNoSemi ;
forCond: expr ;
forUpdate: assignmentNoSemi ;

declarationNoSemi: type ID ('=' expr)? ;
assignmentNoSemi: unaryExpr '=' expr ;

switchStatement: 'switch' '(' expr ')' '{' caseBlock* '}' ;
caseBlock: ('case' expr ':' | 'default' ':') statement* ;
breakStatement: 'break' ';' ;
returnStatement: 'return' expr? ';' ;

// --- Hierarquia de Expressões ---

expr: logicalOrExpr ;
logicalOrExpr: logicalAndExpr ('||' logicalAndExpr)* ;
logicalAndExpr: equalityExpr ('&&' equalityExpr)* ;
equalityExpr: relationalExpr (('==' | '!=') relationalExpr)* ;
relationalExpr: additiveExpr (('<' | '>' | '<=' | '>=') additiveExpr)* ;
additiveExpr: multiplicativeExpr (('+' | '-') multiplicativeExpr)* ;
multiplicativeExpr: unaryExpr (('*' | '/' | '%') unaryExpr)* ;
unaryExpr: ('&' | '*' | '!')* postfixExpr ;
postfixExpr: primary ('.' ID | '[' expr ']' | '(' argumentList? ')')* ;
primary: ID
       | INT
       | FLOAT
       | CHAR
       | STRING
       | '(' expr ')'
       ;

argumentList: expr (',' expr)* ;

type: baseType '*'* ;
baseType: 'int'
        | 'float'
        | 'char'
        | 'void'
        | 'struct' ID
        | 'union' ID
        ;

// --- Regras do Lexer (Tokens) ---

ID: [a-zA-Z_] [a-zA-Z_0-9]*;
INT: [0-9]+;
FLOAT: [0-9]+ '.' [0-9]+;
STRING: '"' ( ~'"' )* '"';
CHAR: '\'' . '\'';

WS: [ \t\r\n]+ -> skip;

LINE_COMMENT: '//' .*? '\r'? '\n' -> skip;
BLOCK_COMMENT: '/*' .*? '*/' -> skip;