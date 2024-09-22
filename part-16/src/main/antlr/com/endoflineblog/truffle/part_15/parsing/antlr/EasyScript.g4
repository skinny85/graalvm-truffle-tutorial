grammar EasyScript ;

@header{
package com.endoflineblog.truffle.part_15.parsing.antlr;
}

start : stmt+ EOF ;

stmt :                        kind=('var' | 'let' | 'const') binding (',' binding)* ';'? #VarDeclStmt
     |                                                                        expr1 ';'? #ExprStmt
     |                                                   'function' subroutine_decl ';'? #FuncDeclStmt
     |                                                              'return' expr1? ';'? #ReturnStmt
     |                                                                   stmt_block ';'? #BlockStmt
     |                   'if' '(' cond=expr1 ')' then_stmt=stmt ('else' else_stmt=stmt)? #IfStmt
     |                                              'while' '(' cond=expr1 ')' body=stmt #WhileStmt
     |                                   'do' stmt_block 'while' '(' cond=expr1 ')' ';'? #DoWhileStmt
     |                'for' '(' init=stmt? ';' cond=expr1? ';' updt=expr1? ')' body=stmt #ForStmt
     |                                                                      'break' ';'? #BreakStmt
     |                                                                   'continue' ';'? #ContinueStmt
     |                 'class' cls=ID ('extends' spr_cls=ID)? '{' class_member* '}' ';'? #ClassDeclStmt
     |                                                                'throw' expr1 ';'? #ThrowStmt
     | 'try' t=stmt_block 'catch' '(' ID ')' c=stmt_block ('finally' f=stmt_block)? ';'? #TryCatchStmt
     |                                    'try' t=stmt_block 'finally' f=stmt_block ';'? #TryFinallyStmt
     ;
binding : ID ('=' expr1)? ;
class_member : subroutine_decl ;
subroutine_decl : name=ID '(' args=func_args ')' stmt_block ;
func_args : (ID (',' ID)* )? ;
stmt_block : '{' stmt* '}' ;

expr1 : ID '=' expr1                                       #AssignmentExpr1
      | object=expr5 '.' ID '=' rvalue=expr1               #PropertyWriteExpr1
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
expr5 : expr5 '.' ID                                       #PropertyReadExpr5
      | arr=expr5 '[' index=expr1 ']'                      #ArrayIndexReadExpr5
      | expr5 '(' (expr1 (',' expr1)*)? ')'                #CallExpr5
      | expr6                                              #PrecedenceSixExpr5
      ;
expr6 : literal                                            #LiteralExpr6
      | 'this'                                             #ThisExpr6
      | 'super'                                            #SuperExpr6
      | ID                                                 #ReferenceExpr6
      | '[' (expr1 (',' expr1)*)? ']'                      #ArrayLiteralExpr6
      | 'new' constr=expr6 ('('(expr1 (',' expr1)*)?')')?  #NewExpr6
      | '(' expr1 ')'                                      #PrecedenceOneExpr6
      ;

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
