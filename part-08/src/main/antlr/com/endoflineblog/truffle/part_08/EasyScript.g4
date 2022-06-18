grammar EasyScript ;

@header{
package com.endoflineblog.truffle.part_08;
}

start : stmt+ EOF ;

stmt :   kind=('var' | 'let' | 'const') binding (',' binding)* ';'? #VarDeclStmt
     |                                                   expr1 ';'? #ExprStmt
     |                                         'return' expr1? ';'? #ReturnStmt
     | 'function' name=ID '(' args=func_args ')' '{' stmt* '}' ';'? #FuncDeclStmt
     ;
binding : ID ('=' expr1)? ;
func_args : (ID (',' ID)* )? ;

expr1 : ID '=' expr1                        #AssignmentExpr1
      | expr2                               #PrecedenceTwoExpr1
      ;
expr2 : left=expr2 '+' right=expr3          #AddExpr2
      | '-' expr3                           #UnaryMinusExpr2
      | expr3                               #PrecedenceThreeExpr2
      ;
expr3 : literal                             #LiteralExpr3
      | ID                                  #SimpleReferenceExpr3
      | ID '.' ID                           #ComplexReferenceExpr3
      | expr3 '(' (expr1 (',' expr1)*)? ')' #CallExpr3
      | '(' expr1 ')'                       #PrecedenceOneExpr3
      ;

literal : INT | DOUBLE | 'undefined' | bool_literal ;
bool_literal : 'true' | 'false' ;

fragment DIGIT : [0-9] ;
INT : DIGIT+ ;
DOUBLE : DIGIT+ '.' DIGIT+ ;

fragment LETTER : [a-zA-Z$_] ;
ID : LETTER (LETTER | DIGIT)* ;

// skip all whitespace
WS : (' ' | '\r' | '\t' | '\n' | '\f')+ -> skip ;
