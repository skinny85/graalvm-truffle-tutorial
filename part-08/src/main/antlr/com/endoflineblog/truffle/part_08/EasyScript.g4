grammar EasyScript ;

@header{
package com.endoflineblog.truffle.part_08;
}

start : stmt+ EOF ;

stmt :      kind=('var' | 'let' | 'const') binding (',' binding)* ';'? #VarDeclStmt
     |                                                      expr1 ';'? #ExprStmt
     |                                            'return' expr1? ';'? #ReturnStmt
     |    'function' name=ID '(' args=func_args ')' '{' stmt* '}' ';'? #FuncDeclStmt
     |                                              '{' stmt* '}' ';'? #BlockStmt
     | 'if' '(' cond=expr1 ')' then_stmt=stmt ('else' else_stmt=stmt)? #IfStmt
     ;
binding : ID ('=' expr1)? ;
func_args : (ID (',' ID)* )? ;

expr1 : ID '=' expr1                                       #AssignmentExpr1
      | expr2                                              #PrecedenceTwoExpr1
      ;
expr2 : left=expr2 c=('===' | '!==') right=expr3           #EqNotEqExpr2
      | expr3                                              #PrecedenceThreeExpr2
      ;
expr3 : left=expr3 c=('<' | '<=' | '>' | '>=') right=expr4 #ComparisonExpr3
      | expr4                                              #PrecedenceFourExpr3
      ;
expr4 : left=expr4 '+' right=expr5                         #AddExpr4
      | '-' expr5                                          #UnaryMinusExpr4
      | expr5                                              #PrecedenceFiveExpr4
      ;
expr5 : literal                                            #LiteralExpr5
      | ID                                                 #SimpleReferenceExpr5
      | ID '.' ID                                          #ComplexReferenceExpr5
      | expr5 '(' (expr1 (',' expr1)*)? ')'                #CallExpr5
      | '(' expr1 ')'                                      #PrecedenceOneExpr5
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
