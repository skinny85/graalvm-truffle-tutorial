grammar EasyScript ;

@header{
package com.endoflineblog.truffle.part_04;
}

start : expr EOF ;

expr : left=expr '+' right=expr #AddExpr
     | literal                  #LiteralExpr
     ;

literal : INT | DOUBLE ;

fragment DIGIT : [0-9] ;
INT : DIGIT+ ;
DOUBLE : DIGIT+ '.' DIGIT+ ;

// skip all whitespace
WS : (' ' | '\r' | '\t' | '\n' | '\f')+ -> skip ;
