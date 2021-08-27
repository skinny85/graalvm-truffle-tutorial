grammar EasyScript ;

@header{
package com.endoflineblog.truffle.part_05;
}

start : stmt+ EOF ;

stmt : ('var' | 'let' | 'const') binding (',' binding)* ';'?     #DeclStmt
     |                                            expr1 ';'?     #ExprStmt
     ;

binding : ID ('=' expr1)? ;

expr1 : ID '=' expr1               #AssignmentExpr1
      | expr2                      #PrecedenceTwoExpr1
      ;
expr2 : left=expr2 '+' right=expr3 #AddExpr2
      | expr3                      #PrecedenceThreeExpr2
      ;
expr3 : literal                    #LiteralExpr3
      | ID                         #ReferenceExpr3
      | '(' expr1 ')'              #PrecedenceOneExpr3
      ;

literal : INT | DOUBLE ;

fragment DIGIT : [0-9] ;
INT : DIGIT+ ;
DOUBLE : DIGIT+ '.' DIGIT+ ;

fragment LETTER : [a-zA-Z$] ;
ID : (LETTER | '_') (LETTER | '_' | DIGIT)* ;

// skip all whitespace
WS : (' ' | '\r' | '\t' | '\n' | '\f')+ -> skip ;
