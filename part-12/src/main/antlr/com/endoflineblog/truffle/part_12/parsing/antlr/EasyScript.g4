grammar EasyScript ;

@header{
package com.endoflineblog.truffle.part_12.parsing.antlr;
}

start : stmt+ EOF ;

stmt :         kind=('var' | 'let' | 'const') binding (',' binding)* ';'? #VarDeclStmt
     |                                                         expr1 ';'? #ExprStmt
     |       'function' name=ID '(' args=func_args ')' '{' stmt* '}' ';'? #FuncDeclStmt
     |                                               'return' expr1? ';'? #ReturnStmt
     |                                                 '{' stmt* '}' ';'? #BlockStmt
     |    'if' '(' cond=expr1 ')' then_stmt=stmt ('else' else_stmt=stmt)? #IfStmt
     |                               'while' '(' cond=expr1 ')' body=stmt #WhileStmt
     |                 'do' '{' stmt* '}' 'while' '(' cond=expr1 ')' ';'? #DoWhileStmt
     | 'for' '(' init=stmt? ';' cond=expr1? ';' updt=expr1? ')' body=stmt #ForStmt
     |                                                       'break' ';'? #BreakStmt
     |                                                    'continue' ';'? #ContinueStmt
     ;
binding : ID ('=' expr1)? ;
func_args : (ID (',' ID)* )? ;

expr1 : lvalue op=('=' | '+=') expr1                       #AssignmentExpr1
      | arr=expr5 '[' index=expr1 ']' '=' rvalue=expr1     #ArrayIndexWriteExpr1
      | expr2                                              #PrecedenceTwoExpr1
      ;
expr2 : left=expr2 c=('===' | '!==') right=expr3           #EqNotEqExpr2
      | expr3                                              #PrecedenceThreeExpr2
      ;
expr3 : left=expr3 c=('<' | '<=' | '>' | '>=') right=expr4 #ComparisonExpr3
      | expr4                                              #PrecedenceFourExpr3
      ;
expr4 : left=expr4 o=('+' | '-') right=expr5               #AddSubtractExpr4
      | '-' expr5                                          #UnaryMinusExpr4
      | expr5                                              #PrecedenceFiveExpr4
      ;
expr5 : literal                                            #LiteralExpr5
      | ID                                                 #ReferenceExpr5
      | '++' lvalue                                        #PreIncrExpr5
      | '--' lvalue                                        #PreDecrExpr5
      | lvalue '++'                                        #PostIncrExpr5
      | lvalue '--'                                        #PostDecrExpr5
      | expr5 '.' ID                                       #PropertyReadExpr5
      | '[' (expr1 (',' expr1)*)? ']'                      #ArrayLiteralExpr5
      | arr=expr5 '[' index=expr1 ']'                      #ArrayIndexReadExpr5
      | '{' (object_kv (',' object_kv)*)? '}'              #ObjectLiteralExpr5
      | expr5 '(' (expr1 (',' expr1)*)? ')'                #CallExpr5
      | '(' expr1 ')'                                      #PrecedenceOneExpr5
      ;

lvalue : ID                                           #IdLValue
       ;

object_kv : ID ':' expr1 ;

literal : INT | DOUBLE | 'undefined' | bool_literal | string_literal ;
bool_literal : 'true' | 'false' ;

fragment DIGIT : [0-9] ;
INT : DIGIT+ ;
DOUBLE : DIGIT+ '.' DIGIT+ ;

fragment LETTER : [a-zA-Z$_] ;
ID : LETTER (LETTER | DIGIT)* ;

string_literal: SINGLE_QUOTE_STRING | DOUBLE_QUOTE_STRING ;
// see https://stackoverflow.com/questions/24557953/handling-string-literals-which-end-in-an-escaped-quote-in-antlr4
// for details
SINGLE_QUOTE_STRING : '\'' (~[\\'\r\n] | '\\' ~[\r\n])* '\'' ;
DOUBLE_QUOTE_STRING : '"'  (~[\\"\r\n] | '\\' ~[\r\n])* '"' ;

// skip all whitespace
WS : (' ' | '\r' | '\t' | '\n' | '\f')+ -> skip ;
