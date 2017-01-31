import java_cup.runtime.*;
%%

%class Lexer
%unicode
%cup
%line
%column

%{
	private Symbol symbol(int type) {
		return new Symbol(type, yyline, yycolumn);
	}
	
	private Symbol symbol(int type, Object value) {
		return new Symbol(type, yyline, yycolumn, value);
	}
%}

LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | \t\f

Identifier = [a-zA-Z][a-zA-Z0-9_]*
Integer_Literal = 0 | -?[1-9][0-9]*		
Block_Comment = "/*" ~"*/"
Line_Comment = "//"~\n

%state MINUS

%%

/* Comments */
{Block_Comment}			{}
{Line_Comment}			{}

String					{return symbol(sym.STRING);}
public					{return symbol(sym.PUBLIC);}
class					{return symbol(sym.CLASS);}
static					{return symbol(sym.STATIC);}
void					{return symbol(sym.VOID);}
main					{return symbol(sym.MAIN);}
extends 				{return symbol(sym.EXTENDS);}
return 					{return symbol(sym.RETURN);}
if 						{return symbol(sym.IF);}
else					{return symbol(sym.ELSE);}
while					{return symbol(sym.WHILE);}
boolean 				{return symbol(sym.BOOLEAN);}
int 					{return symbol(sym.INTEGER);}
System\.out\.println	{return symbol(sym.PRINT);}
length					{return symbol(sym.LENGTH);}
true 					{return symbol(sym.TRUE);}
false 					{return symbol(sym.FALSE);}
new 					{return symbol(sym.NEW);}
this 					{return symbol(sym.THIS);}
\{						{return symbol(sym.LBRACE);}
\}						{return symbol(sym.RBRACE);}
\[						{return symbol(sym.LBRACKET);}
\]						{return symbol(sym.RBRACKET);}
\(						{return symbol(sym.LPAREN);}
\)						{return symbol(sym.RPAREN);}
,						{return symbol(sym.COMMA);}
;						{return symbol(sym.SEMICOLON);}
\!						{return symbol(sym.EXCLAMATION);}
\.						{return symbol(sym.PERIOD);}
\<						{return symbol(sym.LESSTHAN);}
&&						{return symbol(sym.AND);}
\+						{return symbol(sym.PLUS);}
\*						{return symbol(sym.TIMES);}
"="						{return symbol(sym.EQUALS);}


{Identifier}/\-			{yybegin(MINUS); return symbol(sym.IDENTIFIER, yytext());}
{Identifier}			{return symbol(sym.IDENTIFIER, yytext());}

{Integer_Literal}/\-    {yybegin(MINUS); return symbol(sym.INTEGER_LITERAL, Integer.parseInt(yytext()));}

<YYINITIAL> {
	{Integer_Literal}	{return symbol(sym.INTEGER_LITERAL, Integer.parseInt(yytext()));}
	\-					{return symbol(sym.MINUS);}
}

<MINUS> \-				{yybegin(YYINITIAL); return symbol(sym.MINUS);}



{WhiteSpace}			{}	
[^]                     {}	