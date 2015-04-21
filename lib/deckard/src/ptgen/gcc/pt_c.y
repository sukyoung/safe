
%pure-parser

%{
#include<ptree.h>

using namespace std;
%}

%union{
Tree *t;
}

%{
void yyerror(char*s);
int yylex(YYSTYPE *yylvalp);

Tree *root;
%}


%type <t> decl
%type <t> declspecs_sc_nots_sa_noea
%type <t> declspecs_sc_ts_nosa_noea
%type <t> extdefs
%type <t> declspecs_sc_nots_sa_ea
%type <t> setspecs_fp
%type <t> stmts_and_decls
%type <t> absdcl
%type <t> attributes
%type <t> component_decl_list
%type <t> declspecs_nosc_nots_nosa_ea
%type <t> declspecs_nosc_ts_sa_ea
%type <t> firstparm
%type <t> init
%type <t> program
%type <t> array_declarator
%type <t> nonnull_exprlist
%type <t> lineno_stmt_decl_or_labels_ending_error
%type <t> nonnull_asm_operands
%type <t> initlist_maybe_comma
%type <t> declspecs_ts
%type <t> parmlist_or_identifiers
%type <t> datadef
%type <t> component_decl_list2
%type <t> enumerator
%type <t> lineno_decl
%type <t> maybe_resetattrs
%type <t> declspecs_sc_ts_sa_ea
%type <t> typespec_nonattr
%type <t> asm_clobbers
%type <t> select_or_iter_stmt
%type <t> typespec_attr
%type <t> declspecs_nosc_nots_nosa_noea
%type <t> c99_block_lineno_labeled_stmt
%type <t> notype_declarator
%type <t> if_prefix
%type <t> declarator
%type <t> declspecs_sc_nots_nosa_ea
%type <t> designator_list
%type <t> identifier
%type <t> initval
%type <t> compstmt_primary_start
%type <t> old_style_parm_decls
%type <t> absdcl1_noea
%type <t> typespec_reserved_attr
%type <t> compstmt_contents_nonempty
%type <t> structsp_nonattr
%type <t> sizeof
%type <t> absdcl1
%type <t> after_type_declarator
%type <t> datadecl
%type <t> lineno_stmt_decl_or_labels_ending_decl
%type <t> typename
%type <t> typespec_nonreserved_nonattr
%type <t> parms
%type <t> pushlevel
%type <t> component_notype_declarator
%type <t> label
%type <t> lineno_stmt_decl_or_labels_ending_label
%type <t> initdcl
%type <t> component_declarator
%type <t> absdcl_maybe_attribute
%type <t> typeof
%type <t> identifiers_or_typenames
%type <t> parmlist_or_identifiers_1
%type <t> stmt
%type <t> lineno_labeled_stmt
%type <t> declspecs
%type <t> maybe_type_quals_attrs
%type <t> compstmt_or_error
%type <t> declspecs_ts_nosa
%type <t> lineno_stmt_decl_or_labels_ending_stmt
%type <t> compstmt
%type <t> parm_declarator_nostarttypename
%type <t> maybe_attribute
%type <t> declspecs_nots
%type <t> declspecs_nosc_ts_sa_noea
%type <t> direct_absdcl1
%type <t> declspecs_nosc_ts_nosa_ea
%type <t> notype_initdcl
%type <t> attrib
%type <t> maybe_type_qual
%type <t> initdecls
%type <t> unary_expr
%type <t> primary
%type <t> errstmt
%type <t> declspecs_nosc_nots
%type <t> maybe_label_decls
%type <t> component_decl
%type <t> do_stmt_start
%type <t> asm_operand
%type <t> absdcl1_ea
%type <t> components_notype
%type <t> declspecs_nosc
%type <t> declspecs_sc_ts_nosa_ea
%type <t> initlist1
%type <t> fndef
%type <t> nested_function
%type <t> declspecs_sc_ts_sa_noea
%type <t> lineno_stmt
%type <t> declspecs_nosc_nots_sa_ea
%type <t> parm
%type <t> old_style_parm_decls_1
%type <t> simple_if
%type <t> lineno_datadecl
%type <t> save_location
%type <t> typespec_reserved_nonattr
%type <t> scspec
%type <t> initelt
%type <t> exprlist
%type <t> lineno_stmt_decl_or_labels
%type <t> extension
%type <t> expr
%type <t> maybecomma
%type <t> maybeasm
%type <t> structsp_attr
%type <t> compstmt_start
%type <t> asm_operands
%type <t> label_decl
%type <t> declspecs_nosc_ts
%type <t> datadecls
%type <t> unop
%type <t> exprstmt
%type <t> poplevel
%type <t> attribute
%type <t> setspecs
%type <t> notype_nested_function
%type <t> lineno_label
%type <t> expr_no_commas
%type <t> any_word
%type <t> struct_head
%type <t> extdef
%type <t> xexpr
%type <t> parmlist_2
%type <t> cast_expr
%type <t> label_decls
%type <t> parm_declarator_starttypename
%type <t> attribute_list
%type <t> parmlist
%type <t> parmlist_1
%type <t> alignof
%type <t> compstmt_nostart
%type <t> designator
%type <t> for_init_stmt
%type <t> maybecomma_warn
%type <t> declspecs_nots_nosa
%type <t> declspecs_sc_nots_nosa_noea
%type <t> enum_head
%type <t> enumlist
%type <t> union_head
%type <t> declspecs_nosc_nots_sa_noea
%type <t> declspecs_nosc_ts_nosa_noea
%type <t> identifiers
%type <t> notype_initdecls
%type <t> components
%type <t> extdef_1
%type <t> parm_declarator
%{
/*
#include "config.h"
#include "system.h"
#include "coretypes.h"
#include "tm.h"
#include "tree.h"
#include "input.h"
#include "cpplib.h"
#include "intl.h"
#include "timevar.h"
#include "c-pragma.h"
#include "c-tree.h"
#include "flags.h"
#include "varray.h"
#include "output.h"
#include "toplev.h"
#include "ggc.h"
*/


%}


%start program

%token IDENTIFIER
%token TYPENAME
%token SCSPEC			/* Storage class other than static.  */
%token STATIC			/* Static storage class.  */
%token TYPESPEC
%token TYPE_QUAL
%token CONSTANT
%token STRING
%token ELLIPSIS
%token SIZEOF ENUM STRUCT UNION IF ELSE WHILE DO FOR SWITCH CASE DEFAULT
%token BREAK CONTINUE RETURN GOTO ASM_KEYWORD TYPEOF ALIGNOF
%token ATTRIBUTE EXTENSION LABEL
%token REALPART IMAGPART VA_ARG CHOOSE_EXPR TYPES_COMPATIBLE_P
%token PTR_VALUE PTR_BASE PTR_EXTENT
%token FUNC_NAME

%type <t> IDENTIFIER
%type <t> TYPENAME
%type <t> SCSPEC			/* Storage class other than static.  */
%type <t> STATIC			/* Static storage class.  */
%type <t> TYPESPEC
%type <t> TYPE_QUAL
%type <t> CONSTANT
%type <t> STRING
%type <t> ELLIPSIS
%type <t> SIZEOF ENUM STRUCT UNION IF ELSE WHILE DO FOR SWITCH CASE DEFAULT
%type <t> BREAK CONTINUE RETURN GOTO ASM_KEYWORD TYPEOF ALIGNOF
%type <t> ATTRIBUTE EXTENSION LABEL
%type <t> REALPART IMAGPART VA_ARG CHOOSE_EXPR TYPES_COMPATIBLE_P
%type <t> PTR_VALUE PTR_BASE PTR_EXTENT
%type <t> FUNC_NAME

%type <t> ',' ')' ']' ';' '}' '{'  '~' '!'

%nonassoc IF
%nonassoc ELSE

%right <t> ASSIGN '='
%right <t> '?' ':'
%left <t> OROR
%left <t> ANDAND
%left <t> '|'
%left <t> '^'
%left <t> '&'
%left <t> EQCOMPARE
%left <t> ARITHCOMPARE
%left <t> LSHIFT RSHIFT
%left <t> '+' '-'
%left <t> '*' '/' '%'
%right <t> UNARY PLUSPLUS MINUSMINUS
%left HYPERUNARY
%left <t> POINTSAT '.' '(' '['


%token INTERFACE IMPLEMENTATION END SELECTOR DEFS ENCODE
%token CLASSNAME PUBLIC PRIVATE PROTECTED PROTOCOL OBJECTNAME CLASS ALIAS
%token AT_THROW AT_TRY AT_CATCH AT_FINALLY AT_SYNCHRONIZED
%token OBJC_STRING

/*
%type <t> INTERFACE IMPLEMENTATION END SELECTOR DEFS ENCODE
%type <t> CLASSNAME PUBLIC PRIVATE PROTECTED PROTOCOL OBJECTNAME CLASS ALIAS
%type <t> AT_THROW AT_TRY AT_CATCH AT_FINALLY AT_SYNCHRONIZED
%type <t> OBJC_STRING

%type <t> ENUM STRUCT UNION IF ELSE WHILE DO FOR SWITCH CASE DEFAULT
%type <t> BREAK CONTINUE RETURN GOTO ASM_KEYWORD SIZEOF TYPEOF ALIGNOF
%type <t> IDENTIFIER TYPENAME CONSTANT STRING
%type <t> SCSPEC STATIC TYPESPEC TYPE_QUAL
*/



%%


program : 
    {
        $$= new NonTerminal( 14 );
root= $$;

    }
    ;

program : extdefs 
    {
        $$= new NonTerminal( 14 );

        $$->addChild($1);

        $1->parent= $$;
root= $$;

    }
    ;

extdefs : extdef 
    {
        $$= new NonTerminal( 3 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

extdefs : extdefs extdef 
    {
        $$= new NonTerminal( 3 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

extdef : extdef_1 
    {
        $$= new NonTerminal( 131 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

extdef_1 : fndef 
    {
        $$= new NonTerminal( 155 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

extdef_1 : datadef 
    {
        $$= new NonTerminal( 155 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

extdef_1 : ASM_KEYWORD '(' expr ')' ';' 
    {
        $$= new NonTerminal( 155 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

    }
    ;

extdef_1 : extension extdef 
    {
        $$= new NonTerminal( 155 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

datadef : setspecs notype_initdecls ';' 
    {
        $$= new NonTerminal( 22 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

datadef : declspecs_nots setspecs notype_initdecls ';' 
    {
        $$= new NonTerminal( 22 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

datadef : declspecs_ts setspecs initdecls ';' 
    {
        $$= new NonTerminal( 22 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

datadef : declspecs ';' 
    {
        $$= new NonTerminal( 22 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

datadef : error ';' 
    {
        $$= new NonTerminal( 22 );

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

datadef : error '}' 
    {
        $$= new NonTerminal( 22 );

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

datadef : ';' 
    {
        $$= new NonTerminal( 22 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

fndef : declspecs_ts setspecs declarator old_style_parm_decls save_location compstmt_or_error 
    {
        $$= new NonTerminal( 96 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

        $5->nextSibbling= $6;

        $$->addChild($6);

        $6->parent= $$;

    }
    ;

fndef : declspecs_ts setspecs declarator error 
    {
        $$= new NonTerminal( 96 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

fndef : declspecs_nots setspecs notype_declarator old_style_parm_decls save_location compstmt_or_error 
    {
        $$= new NonTerminal( 96 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

        $5->nextSibbling= $6;

        $$->addChild($6);

        $6->parent= $$;

    }
    ;

fndef : declspecs_nots setspecs notype_declarator error 
    {
        $$= new NonTerminal( 96 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

fndef : setspecs notype_declarator old_style_parm_decls save_location compstmt_or_error 
    {
        $$= new NonTerminal( 96 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

    }
    ;

fndef : setspecs notype_declarator error 
    {
        $$= new NonTerminal( 96 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

identifier : IDENTIFIER 
    {
        $$= new NonTerminal( 39 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

identifier : TYPENAME 
    {
        $$= new NonTerminal( 39 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

unop : '&' 
    {
        $$= new NonTerminal( 121 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

unop : '-' 
    {
        $$= new NonTerminal( 121 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

unop : '+' 
    {
        $$= new NonTerminal( 121 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

unop : PLUSPLUS 
    {
        $$= new NonTerminal( 121 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

unop : MINUSMINUS 
    {
        $$= new NonTerminal( 121 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

unop : '~' 
    {
        $$= new NonTerminal( 121 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

unop : '!' 
    {
        $$= new NonTerminal( 121 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

expr : nonnull_exprlist 
    {
        $$= new NonTerminal( 112 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

exprlist : 
    {
        $$= new NonTerminal( 109 );

    }
    ;

exprlist : nonnull_exprlist 
    {
        $$= new NonTerminal( 109 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

nonnull_exprlist : expr_no_commas 
    {
        $$= new NonTerminal( 16 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

nonnull_exprlist : nonnull_exprlist ',' expr_no_commas 
    {
        $$= new NonTerminal( 16 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

unary_expr : primary 
    {
        $$= new NonTerminal( 83 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

unary_expr : '*' cast_expr %prec UNARY 
    {
        $$= new NonTerminal( 83 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

unary_expr : extension cast_expr %prec UNARY 
    {
        $$= new NonTerminal( 83 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

unary_expr : unop cast_expr %prec UNARY 
    {
        $$= new NonTerminal( 83 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

unary_expr : ANDAND identifier 
    {
        $$= new NonTerminal( 83 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

unary_expr : sizeof unary_expr %prec UNARY 
    {
        $$= new NonTerminal( 83 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

unary_expr : sizeof '(' typename ')' %prec HYPERUNARY 
    {
        $$= new NonTerminal( 83 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

unary_expr : alignof unary_expr %prec UNARY 
    {
        $$= new NonTerminal( 83 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

unary_expr : alignof '(' typename ')' %prec HYPERUNARY 
    {
        $$= new NonTerminal( 83 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

unary_expr : REALPART cast_expr %prec UNARY 
    {
        $$= new NonTerminal( 83 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

unary_expr : IMAGPART cast_expr %prec UNARY 
    {
        $$= new NonTerminal( 83 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

sizeof : SIZEOF 
    {
        $$= new NonTerminal( 47 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

alignof : ALIGNOF 
    {
        $$= new NonTerminal( 140 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

typeof : TYPEOF 
    {
        $$= new NonTerminal( 62 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

cast_expr : unary_expr 
    {
        $$= new NonTerminal( 134 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

cast_expr : '(' typename ')' cast_expr %prec UNARY 
    {
        $$= new NonTerminal( 134 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

expr_no_commas : cast_expr 
    {
        $$= new NonTerminal( 128 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

expr_no_commas : expr_no_commas '+' expr_no_commas 
    {
        $$= new NonTerminal( 128 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

expr_no_commas : expr_no_commas '-' expr_no_commas 
    {
        $$= new NonTerminal( 128 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

expr_no_commas : expr_no_commas '*' expr_no_commas 
    {
        $$= new NonTerminal( 128 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

expr_no_commas : expr_no_commas '/' expr_no_commas 
    {
        $$= new NonTerminal( 128 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

expr_no_commas : expr_no_commas '%' expr_no_commas 
    {
        $$= new NonTerminal( 128 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

expr_no_commas : expr_no_commas LSHIFT expr_no_commas 
    {
        $$= new NonTerminal( 128 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

expr_no_commas : expr_no_commas RSHIFT expr_no_commas 
    {
        $$= new NonTerminal( 128 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

expr_no_commas : expr_no_commas ARITHCOMPARE expr_no_commas 
    {
        $$= new NonTerminal( 128 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

expr_no_commas : expr_no_commas EQCOMPARE expr_no_commas 
    {
        $$= new NonTerminal( 128 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

expr_no_commas : expr_no_commas '&' expr_no_commas 
    {
        $$= new NonTerminal( 128 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

expr_no_commas : expr_no_commas '|' expr_no_commas 
    {
        $$= new NonTerminal( 128 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

expr_no_commas : expr_no_commas '^' expr_no_commas 
    {
        $$= new NonTerminal( 128 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

expr_no_commas : expr_no_commas ANDAND expr_no_commas 
    {
        $$= new NonTerminal( 128 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

expr_no_commas : expr_no_commas OROR expr_no_commas 
    {
        $$= new NonTerminal( 128 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

expr_no_commas : expr_no_commas '?' expr ':' expr_no_commas 
    {
        $$= new NonTerminal( 128 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

    }
    ;

expr_no_commas : expr_no_commas '?' ':' expr_no_commas 
    {
        $$= new NonTerminal( 128 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

expr_no_commas : expr_no_commas '=' expr_no_commas 
    {
        $$= new NonTerminal( 128 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

expr_no_commas : expr_no_commas ASSIGN expr_no_commas 
    {
        $$= new NonTerminal( 128 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

primary : IDENTIFIER 
    {
        $$= new NonTerminal( 84 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

primary : IDENTIFIER STRING 
    {
        $$= new NonTerminal( 84 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

primary : IDENTIFIER STRING IDENTIFIER 
    {
        $$= new NonTerminal( 84 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

primary : STRING IDENTIFIER 
    {
        $$= new NonTerminal( 84 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

primary : CONSTANT 
    {
        $$= new NonTerminal( 84 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

primary : STRING 
    {
        $$= new NonTerminal( 84 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

primary : FUNC_NAME 
    {
        $$= new NonTerminal( 84 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

primary : '(' typename ')' '{' initlist_maybe_comma '}' %prec UNARY 
    {
        $$= new NonTerminal( 84 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

        $5->nextSibbling= $6;

        $$->addChild($6);

        $6->parent= $$;

    }
    ;

primary : '(' expr ')' 
    {
        $$= new NonTerminal( 84 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

primary : '(' error ')' 
    {
        $$= new NonTerminal( 84 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

primary : compstmt_primary_start compstmt_nostart ')' 
    {
        $$= new NonTerminal( 84 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

primary : compstmt_primary_start error ')' 
    {
        $$= new NonTerminal( 84 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

primary : primary '(' exprlist ')' %prec '.' 
    {
        $$= new NonTerminal( 84 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

primary : VA_ARG '(' expr_no_commas ',' typename ')' 
    {
        $$= new NonTerminal( 84 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

        $5->nextSibbling= $6;

        $$->addChild($6);

        $6->parent= $$;

    }
    ;

primary : CHOOSE_EXPR '(' expr_no_commas ',' expr_no_commas ',' expr_no_commas ')' 
    {
        $$= new NonTerminal( 84 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

        $5->nextSibbling= $6;

        $$->addChild($6);

        $6->parent= $$;

        $6->nextSibbling= $7;

        $$->addChild($7);

        $7->parent= $$;

        $7->nextSibbling= $8;

        $$->addChild($8);

        $8->parent= $$;

    }
    ;

primary : TYPES_COMPATIBLE_P '(' typename ',' typename ')' 
    {
        $$= new NonTerminal( 84 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

        $5->nextSibbling= $6;

        $$->addChild($6);

        $6->parent= $$;

    }
    ;

primary : primary '[' expr ']' %prec '.' 
    {
        $$= new NonTerminal( 84 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

primary : primary '.' identifier 
    {
        $$= new NonTerminal( 84 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

primary : primary POINTSAT identifier 
    {
        $$= new NonTerminal( 84 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

primary : primary PLUSPLUS 
    {
        $$= new NonTerminal( 84 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

primary : primary MINUSMINUS 
    {
        $$= new NonTerminal( 84 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

old_style_parm_decls : old_style_parm_decls_1 
    {
        $$= new NonTerminal( 42 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

old_style_parm_decls_1 : 
    {
        $$= new NonTerminal( 102 );

    }
    ;

old_style_parm_decls_1 : datadecls 
    {
        $$= new NonTerminal( 102 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

lineno_datadecl : save_location datadecl 
    {
        $$= new NonTerminal( 104 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

datadecls : lineno_datadecl 
    {
        $$= new NonTerminal( 120 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

datadecls : errstmt 
    {
        $$= new NonTerminal( 120 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

datadecls : datadecls lineno_datadecl 
    {
        $$= new NonTerminal( 120 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

datadecls : lineno_datadecl errstmt 
    {
        $$= new NonTerminal( 120 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

datadecl : declspecs_ts_nosa setspecs initdecls ';' 
    {
        $$= new NonTerminal( 50 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

datadecl : declspecs_nots_nosa setspecs notype_initdecls ';' 
    {
        $$= new NonTerminal( 50 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

datadecl : declspecs_ts_nosa ';' 
    {
        $$= new NonTerminal( 50 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

datadecl : declspecs_nots_nosa ';' 
    {
        $$= new NonTerminal( 50 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

lineno_decl : save_location decl 
    {
        $$= new NonTerminal( 25 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

setspecs : 
    {
        $$= new NonTerminal( 125 );

    }
    ;

maybe_resetattrs : maybe_attribute 
    {
        $$= new NonTerminal( 26 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

decl : declspecs_ts setspecs initdecls ';' 
    {
        $$= new NonTerminal( 0 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

decl : declspecs_nots setspecs notype_initdecls ';' 
    {
        $$= new NonTerminal( 0 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

decl : declspecs_ts setspecs nested_function 
    {
        $$= new NonTerminal( 0 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

decl : declspecs_nots setspecs notype_nested_function 
    {
        $$= new NonTerminal( 0 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

decl : declspecs ';' 
    {
        $$= new NonTerminal( 0 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

decl : extension decl 
    {
        $$= new NonTerminal( 0 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_nots_nosa_noea : TYPE_QUAL 
    {
        $$= new NonTerminal( 32 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nosc_nots_nosa_noea : declspecs_nosc_nots_nosa_noea TYPE_QUAL 
    {
        $$= new NonTerminal( 32 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_nots_nosa_noea : declspecs_nosc_nots_nosa_ea TYPE_QUAL 
    {
        $$= new NonTerminal( 32 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_nots_nosa_ea : declspecs_nosc_nots_nosa_noea attributes 
    {
        $$= new NonTerminal( 10 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_nots_sa_noea : declspecs_nosc_nots_sa_noea TYPE_QUAL 
    {
        $$= new NonTerminal( 150 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_nots_sa_noea : declspecs_nosc_nots_sa_ea TYPE_QUAL 
    {
        $$= new NonTerminal( 150 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_nots_sa_ea : attributes 
    {
        $$= new NonTerminal( 100 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nosc_nots_sa_ea : declspecs_nosc_nots_sa_noea attributes 
    {
        $$= new NonTerminal( 100 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_ts_nosa_noea : typespec_nonattr 
    {
        $$= new NonTerminal( 151 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nosc_ts_nosa_noea : declspecs_nosc_ts_nosa_noea TYPE_QUAL 
    {
        $$= new NonTerminal( 151 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_ts_nosa_noea : declspecs_nosc_ts_nosa_ea TYPE_QUAL 
    {
        $$= new NonTerminal( 151 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_ts_nosa_noea : declspecs_nosc_ts_nosa_noea typespec_reserved_nonattr 
    {
        $$= new NonTerminal( 151 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_ts_nosa_noea : declspecs_nosc_ts_nosa_ea typespec_reserved_nonattr 
    {
        $$= new NonTerminal( 151 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_ts_nosa_noea : declspecs_nosc_nots_nosa_noea typespec_nonattr 
    {
        $$= new NonTerminal( 151 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_ts_nosa_noea : declspecs_nosc_nots_nosa_ea typespec_nonattr 
    {
        $$= new NonTerminal( 151 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_ts_nosa_ea : typespec_attr 
    {
        $$= new NonTerminal( 78 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nosc_ts_nosa_ea : declspecs_nosc_ts_nosa_noea attributes 
    {
        $$= new NonTerminal( 78 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_ts_nosa_ea : declspecs_nosc_ts_nosa_noea typespec_reserved_attr 
    {
        $$= new NonTerminal( 78 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_ts_nosa_ea : declspecs_nosc_ts_nosa_ea typespec_reserved_attr 
    {
        $$= new NonTerminal( 78 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_ts_nosa_ea : declspecs_nosc_nots_nosa_noea typespec_attr 
    {
        $$= new NonTerminal( 78 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_ts_nosa_ea : declspecs_nosc_nots_nosa_ea typespec_attr 
    {
        $$= new NonTerminal( 78 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_ts_sa_noea : declspecs_nosc_ts_sa_noea TYPE_QUAL 
    {
        $$= new NonTerminal( 76 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_ts_sa_noea : declspecs_nosc_ts_sa_ea TYPE_QUAL 
    {
        $$= new NonTerminal( 76 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_ts_sa_noea : declspecs_nosc_ts_sa_noea typespec_reserved_nonattr 
    {
        $$= new NonTerminal( 76 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_ts_sa_noea : declspecs_nosc_ts_sa_ea typespec_reserved_nonattr 
    {
        $$= new NonTerminal( 76 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_ts_sa_noea : declspecs_nosc_nots_sa_noea typespec_nonattr 
    {
        $$= new NonTerminal( 76 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_ts_sa_noea : declspecs_nosc_nots_sa_ea typespec_nonattr 
    {
        $$= new NonTerminal( 76 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_ts_sa_ea : declspecs_nosc_ts_sa_noea attributes 
    {
        $$= new NonTerminal( 11 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_ts_sa_ea : declspecs_nosc_ts_sa_noea typespec_reserved_attr 
    {
        $$= new NonTerminal( 11 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_ts_sa_ea : declspecs_nosc_ts_sa_ea typespec_reserved_attr 
    {
        $$= new NonTerminal( 11 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_ts_sa_ea : declspecs_nosc_nots_sa_noea typespec_attr 
    {
        $$= new NonTerminal( 11 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_nosc_ts_sa_ea : declspecs_nosc_nots_sa_ea typespec_attr 
    {
        $$= new NonTerminal( 11 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_nots_nosa_noea : scspec 
    {
        $$= new NonTerminal( 146 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_sc_nots_nosa_noea : declspecs_sc_nots_nosa_noea TYPE_QUAL 
    {
        $$= new NonTerminal( 146 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_nots_nosa_noea : declspecs_sc_nots_nosa_ea TYPE_QUAL 
    {
        $$= new NonTerminal( 146 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_nots_nosa_noea : declspecs_nosc_nots_nosa_noea scspec 
    {
        $$= new NonTerminal( 146 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_nots_nosa_noea : declspecs_nosc_nots_nosa_ea scspec 
    {
        $$= new NonTerminal( 146 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_nots_nosa_noea : declspecs_sc_nots_nosa_noea scspec 
    {
        $$= new NonTerminal( 146 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_nots_nosa_noea : declspecs_sc_nots_nosa_ea scspec 
    {
        $$= new NonTerminal( 146 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_nots_nosa_ea : declspecs_sc_nots_nosa_noea attributes 
    {
        $$= new NonTerminal( 37 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_nots_sa_noea : declspecs_sc_nots_sa_noea TYPE_QUAL 
    {
        $$= new NonTerminal( 1 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_nots_sa_noea : declspecs_sc_nots_sa_ea TYPE_QUAL 
    {
        $$= new NonTerminal( 1 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_nots_sa_noea : declspecs_nosc_nots_sa_noea scspec 
    {
        $$= new NonTerminal( 1 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_nots_sa_noea : declspecs_nosc_nots_sa_ea scspec 
    {
        $$= new NonTerminal( 1 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_nots_sa_noea : declspecs_sc_nots_sa_noea scspec 
    {
        $$= new NonTerminal( 1 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_nots_sa_noea : declspecs_sc_nots_sa_ea scspec 
    {
        $$= new NonTerminal( 1 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_nots_sa_ea : declspecs_sc_nots_sa_noea attributes 
    {
        $$= new NonTerminal( 4 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_nosa_noea : declspecs_sc_ts_nosa_noea TYPE_QUAL 
    {
        $$= new NonTerminal( 2 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_nosa_noea : declspecs_sc_ts_nosa_ea TYPE_QUAL 
    {
        $$= new NonTerminal( 2 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_nosa_noea : declspecs_sc_ts_nosa_noea typespec_reserved_nonattr 
    {
        $$= new NonTerminal( 2 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_nosa_noea : declspecs_sc_ts_nosa_ea typespec_reserved_nonattr 
    {
        $$= new NonTerminal( 2 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_nosa_noea : declspecs_sc_nots_nosa_noea typespec_nonattr 
    {
        $$= new NonTerminal( 2 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_nosa_noea : declspecs_sc_nots_nosa_ea typespec_nonattr 
    {
        $$= new NonTerminal( 2 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_nosa_noea : declspecs_nosc_ts_nosa_noea scspec 
    {
        $$= new NonTerminal( 2 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_nosa_noea : declspecs_nosc_ts_nosa_ea scspec 
    {
        $$= new NonTerminal( 2 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_nosa_noea : declspecs_sc_ts_nosa_noea scspec 
    {
        $$= new NonTerminal( 2 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_nosa_noea : declspecs_sc_ts_nosa_ea scspec 
    {
        $$= new NonTerminal( 2 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_nosa_ea : declspecs_sc_ts_nosa_noea attributes 
    {
        $$= new NonTerminal( 94 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_nosa_ea : declspecs_sc_ts_nosa_noea typespec_reserved_attr 
    {
        $$= new NonTerminal( 94 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_nosa_ea : declspecs_sc_ts_nosa_ea typespec_reserved_attr 
    {
        $$= new NonTerminal( 94 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_nosa_ea : declspecs_sc_nots_nosa_noea typespec_attr 
    {
        $$= new NonTerminal( 94 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_nosa_ea : declspecs_sc_nots_nosa_ea typespec_attr 
    {
        $$= new NonTerminal( 94 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_sa_noea : declspecs_sc_ts_sa_noea TYPE_QUAL 
    {
        $$= new NonTerminal( 98 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_sa_noea : declspecs_sc_ts_sa_ea TYPE_QUAL 
    {
        $$= new NonTerminal( 98 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_sa_noea : declspecs_sc_ts_sa_noea typespec_reserved_nonattr 
    {
        $$= new NonTerminal( 98 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_sa_noea : declspecs_sc_ts_sa_ea typespec_reserved_nonattr 
    {
        $$= new NonTerminal( 98 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_sa_noea : declspecs_sc_nots_sa_noea typespec_nonattr 
    {
        $$= new NonTerminal( 98 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_sa_noea : declspecs_sc_nots_sa_ea typespec_nonattr 
    {
        $$= new NonTerminal( 98 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_sa_noea : declspecs_nosc_ts_sa_noea scspec 
    {
        $$= new NonTerminal( 98 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_sa_noea : declspecs_nosc_ts_sa_ea scspec 
    {
        $$= new NonTerminal( 98 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_sa_noea : declspecs_sc_ts_sa_noea scspec 
    {
        $$= new NonTerminal( 98 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_sa_noea : declspecs_sc_ts_sa_ea scspec 
    {
        $$= new NonTerminal( 98 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_sa_ea : declspecs_sc_ts_sa_noea attributes 
    {
        $$= new NonTerminal( 27 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_sa_ea : declspecs_sc_ts_sa_noea typespec_reserved_attr 
    {
        $$= new NonTerminal( 27 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_sa_ea : declspecs_sc_ts_sa_ea typespec_reserved_attr 
    {
        $$= new NonTerminal( 27 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_sa_ea : declspecs_sc_nots_sa_noea typespec_attr 
    {
        $$= new NonTerminal( 27 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_sc_ts_sa_ea : declspecs_sc_nots_sa_ea typespec_attr 
    {
        $$= new NonTerminal( 27 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

declspecs_ts : declspecs_nosc_ts_nosa_noea 
    {
        $$= new NonTerminal( 20 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_ts : declspecs_nosc_ts_nosa_ea 
    {
        $$= new NonTerminal( 20 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_ts : declspecs_nosc_ts_sa_noea 
    {
        $$= new NonTerminal( 20 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_ts : declspecs_nosc_ts_sa_ea 
    {
        $$= new NonTerminal( 20 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_ts : declspecs_sc_ts_nosa_noea 
    {
        $$= new NonTerminal( 20 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_ts : declspecs_sc_ts_nosa_ea 
    {
        $$= new NonTerminal( 20 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_ts : declspecs_sc_ts_sa_noea 
    {
        $$= new NonTerminal( 20 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_ts : declspecs_sc_ts_sa_ea 
    {
        $$= new NonTerminal( 20 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nots : declspecs_nosc_nots_nosa_noea 
    {
        $$= new NonTerminal( 75 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nots : declspecs_nosc_nots_nosa_ea 
    {
        $$= new NonTerminal( 75 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nots : declspecs_nosc_nots_sa_noea 
    {
        $$= new NonTerminal( 75 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nots : declspecs_nosc_nots_sa_ea 
    {
        $$= new NonTerminal( 75 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nots : declspecs_sc_nots_nosa_noea 
    {
        $$= new NonTerminal( 75 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nots : declspecs_sc_nots_nosa_ea 
    {
        $$= new NonTerminal( 75 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nots : declspecs_sc_nots_sa_noea 
    {
        $$= new NonTerminal( 75 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nots : declspecs_sc_nots_sa_ea 
    {
        $$= new NonTerminal( 75 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_ts_nosa : declspecs_nosc_ts_nosa_noea 
    {
        $$= new NonTerminal( 70 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_ts_nosa : declspecs_nosc_ts_nosa_ea 
    {
        $$= new NonTerminal( 70 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_ts_nosa : declspecs_sc_ts_nosa_noea 
    {
        $$= new NonTerminal( 70 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_ts_nosa : declspecs_sc_ts_nosa_ea 
    {
        $$= new NonTerminal( 70 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nots_nosa : declspecs_nosc_nots_nosa_noea 
    {
        $$= new NonTerminal( 145 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nots_nosa : declspecs_nosc_nots_nosa_ea 
    {
        $$= new NonTerminal( 145 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nots_nosa : declspecs_sc_nots_nosa_noea 
    {
        $$= new NonTerminal( 145 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nots_nosa : declspecs_sc_nots_nosa_ea 
    {
        $$= new NonTerminal( 145 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nosc_ts : declspecs_nosc_ts_nosa_noea 
    {
        $$= new NonTerminal( 119 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nosc_ts : declspecs_nosc_ts_nosa_ea 
    {
        $$= new NonTerminal( 119 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nosc_ts : declspecs_nosc_ts_sa_noea 
    {
        $$= new NonTerminal( 119 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nosc_ts : declspecs_nosc_ts_sa_ea 
    {
        $$= new NonTerminal( 119 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nosc_nots : declspecs_nosc_nots_nosa_noea 
    {
        $$= new NonTerminal( 86 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nosc_nots : declspecs_nosc_nots_nosa_ea 
    {
        $$= new NonTerminal( 86 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nosc_nots : declspecs_nosc_nots_sa_noea 
    {
        $$= new NonTerminal( 86 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nosc_nots : declspecs_nosc_nots_sa_ea 
    {
        $$= new NonTerminal( 86 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nosc : declspecs_nosc_ts_nosa_noea 
    {
        $$= new NonTerminal( 93 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nosc : declspecs_nosc_ts_nosa_ea 
    {
        $$= new NonTerminal( 93 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nosc : declspecs_nosc_ts_sa_noea 
    {
        $$= new NonTerminal( 93 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nosc : declspecs_nosc_ts_sa_ea 
    {
        $$= new NonTerminal( 93 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nosc : declspecs_nosc_nots_nosa_noea 
    {
        $$= new NonTerminal( 93 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nosc : declspecs_nosc_nots_nosa_ea 
    {
        $$= new NonTerminal( 93 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nosc : declspecs_nosc_nots_sa_noea 
    {
        $$= new NonTerminal( 93 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs_nosc : declspecs_nosc_nots_sa_ea 
    {
        $$= new NonTerminal( 93 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs : declspecs_nosc_nots_nosa_noea 
    {
        $$= new NonTerminal( 67 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs : declspecs_nosc_nots_nosa_ea 
    {
        $$= new NonTerminal( 67 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs : declspecs_nosc_nots_sa_noea 
    {
        $$= new NonTerminal( 67 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs : declspecs_nosc_nots_sa_ea 
    {
        $$= new NonTerminal( 67 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs : declspecs_nosc_ts_nosa_noea 
    {
        $$= new NonTerminal( 67 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs : declspecs_nosc_ts_nosa_ea 
    {
        $$= new NonTerminal( 67 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs : declspecs_nosc_ts_sa_noea 
    {
        $$= new NonTerminal( 67 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs : declspecs_nosc_ts_sa_ea 
    {
        $$= new NonTerminal( 67 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs : declspecs_sc_nots_nosa_noea 
    {
        $$= new NonTerminal( 67 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs : declspecs_sc_nots_nosa_ea 
    {
        $$= new NonTerminal( 67 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs : declspecs_sc_nots_sa_noea 
    {
        $$= new NonTerminal( 67 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs : declspecs_sc_nots_sa_ea 
    {
        $$= new NonTerminal( 67 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs : declspecs_sc_ts_nosa_noea 
    {
        $$= new NonTerminal( 67 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs : declspecs_sc_ts_nosa_ea 
    {
        $$= new NonTerminal( 67 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs : declspecs_sc_ts_sa_noea 
    {
        $$= new NonTerminal( 67 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declspecs : declspecs_sc_ts_sa_ea 
    {
        $$= new NonTerminal( 67 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

maybe_type_quals_attrs : 
    {
        $$= new NonTerminal( 68 );

    }
    ;

maybe_type_quals_attrs : declspecs_nosc_nots 
    {
        $$= new NonTerminal( 68 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

typespec_nonattr : typespec_reserved_nonattr 
    {
        $$= new NonTerminal( 28 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

typespec_nonattr : typespec_nonreserved_nonattr 
    {
        $$= new NonTerminal( 28 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

typespec_attr : typespec_reserved_attr 
    {
        $$= new NonTerminal( 31 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

typespec_reserved_nonattr : TYPESPEC 
    {
        $$= new NonTerminal( 106 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

typespec_reserved_nonattr : structsp_nonattr 
    {
        $$= new NonTerminal( 106 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

typespec_reserved_attr : structsp_attr 
    {
        $$= new NonTerminal( 44 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

typespec_nonreserved_nonattr : TYPENAME 
    {
        $$= new NonTerminal( 53 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

typespec_nonreserved_nonattr : typeof '(' expr ')' 
    {
        $$= new NonTerminal( 53 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

typespec_nonreserved_nonattr : typeof '(' typename ')' 
    {
        $$= new NonTerminal( 53 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

initdecls : initdcl 
    {
        $$= new NonTerminal( 82 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

initdecls : initdecls ',' maybe_resetattrs initdcl 
    {
        $$= new NonTerminal( 82 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

notype_initdecls : notype_initdcl 
    {
        $$= new NonTerminal( 153 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

notype_initdecls : notype_initdecls ',' maybe_resetattrs notype_initdcl 
    {
        $$= new NonTerminal( 153 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

maybeasm : 
    {
        $$= new NonTerminal( 114 );

    }
    ;

maybeasm : ASM_KEYWORD '(' STRING ')' 
    {
        $$= new NonTerminal( 114 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

initdcl : declarator maybeasm maybe_attribute '=' init 
    {
        $$= new NonTerminal( 59 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

    }
    ;

initdcl : declarator maybeasm maybe_attribute 
    {
        $$= new NonTerminal( 59 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

notype_initdcl : notype_declarator maybeasm maybe_attribute '=' init 
    {
        $$= new NonTerminal( 79 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

    }
    ;

notype_initdcl : notype_declarator maybeasm maybe_attribute 
    {
        $$= new NonTerminal( 79 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

maybe_attribute : 
    {
        $$= new NonTerminal( 74 );

    }
    ;

maybe_attribute : attributes 
    {
        $$= new NonTerminal( 74 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

attributes : attribute 
    {
        $$= new NonTerminal( 8 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

attributes : attributes attribute 
    {
        $$= new NonTerminal( 8 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

attribute : ATTRIBUTE '(' '(' attribute_list ')' ')' 
    {
        $$= new NonTerminal( 124 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

        $5->nextSibbling= $6;

        $$->addChild($6);

        $6->parent= $$;

    }
    ;

attribute_list : attrib 
    {
        $$= new NonTerminal( 137 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

attribute_list : attribute_list ',' attrib 
    {
        $$= new NonTerminal( 137 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

attrib : 
    {
        $$= new NonTerminal( 80 );

    }
    ;

attrib : any_word 
    {
        $$= new NonTerminal( 80 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

attrib : any_word '(' IDENTIFIER ')' 
    {
        $$= new NonTerminal( 80 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

attrib : any_word '(' IDENTIFIER ',' nonnull_exprlist ')' 
    {
        $$= new NonTerminal( 80 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

        $5->nextSibbling= $6;

        $$->addChild($6);

        $6->parent= $$;

    }
    ;

attrib : any_word '(' exprlist ')' 
    {
        $$= new NonTerminal( 80 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

any_word : identifier 
    {
        $$= new NonTerminal( 129 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

any_word : scspec 
    {
        $$= new NonTerminal( 129 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

any_word : TYPESPEC 
    {
        $$= new NonTerminal( 129 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

any_word : TYPE_QUAL 
    {
        $$= new NonTerminal( 129 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

scspec : STATIC 
    {
        $$= new NonTerminal( 107 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

scspec : SCSPEC 
    {
        $$= new NonTerminal( 107 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

init : expr_no_commas 
    {
        $$= new NonTerminal( 13 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

init : '{' initlist_maybe_comma '}' 
    {
        $$= new NonTerminal( 13 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

init : error 
    {
        $$= new NonTerminal( 13 );

    }
    ;

initlist_maybe_comma : 
    {
        $$= new NonTerminal( 19 );

    }
    ;

initlist_maybe_comma : initlist1 maybecomma 
    {
        $$= new NonTerminal( 19 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

initlist1 : initelt 
    {
        $$= new NonTerminal( 95 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

initlist1 : initlist1 ',' initelt 
    {
        $$= new NonTerminal( 95 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

initelt : designator_list '=' initval 
    {
        $$= new NonTerminal( 108 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

initelt : designator initval 
    {
        $$= new NonTerminal( 108 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

initelt : identifier ':' initval 
    {
        $$= new NonTerminal( 108 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

initelt : initval 
    {
        $$= new NonTerminal( 108 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

initval : '{' initlist_maybe_comma '}' 
    {
        $$= new NonTerminal( 40 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

initval : expr_no_commas 
    {
        $$= new NonTerminal( 40 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

initval : error 
    {
        $$= new NonTerminal( 40 );

    }
    ;

designator_list : designator 
    {
        $$= new NonTerminal( 38 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

designator_list : designator_list designator 
    {
        $$= new NonTerminal( 38 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

designator : '.' identifier 
    {
        $$= new NonTerminal( 142 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

designator : '[' expr_no_commas ELLIPSIS expr_no_commas ']' 
    {
        $$= new NonTerminal( 142 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

    }
    ;

designator : '[' expr_no_commas ']' 
    {
        $$= new NonTerminal( 142 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

nested_function : declarator old_style_parm_decls save_location compstmt 
    {
        $$= new NonTerminal( 97 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

notype_nested_function : notype_declarator old_style_parm_decls save_location compstmt 
    {
        $$= new NonTerminal( 126 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

declarator : after_type_declarator 
    {
        $$= new NonTerminal( 36 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

declarator : notype_declarator 
    {
        $$= new NonTerminal( 36 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

after_type_declarator : '(' maybe_attribute after_type_declarator ')' 
    {
        $$= new NonTerminal( 49 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

after_type_declarator : after_type_declarator '(' parmlist_or_identifiers %prec '.' 
    {
        $$= new NonTerminal( 49 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

after_type_declarator : after_type_declarator array_declarator %prec '.' 
    {
        $$= new NonTerminal( 49 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

after_type_declarator : '*' maybe_type_quals_attrs after_type_declarator %prec UNARY 
    {
        $$= new NonTerminal( 49 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

after_type_declarator : TYPENAME 
    {
        $$= new NonTerminal( 49 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

parm_declarator : parm_declarator_starttypename 
    {
        $$= new NonTerminal( 156 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

parm_declarator : parm_declarator_nostarttypename 
    {
        $$= new NonTerminal( 156 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

parm_declarator_starttypename : parm_declarator_starttypename '(' parmlist_or_identifiers %prec '.' 
    {
        $$= new NonTerminal( 136 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

parm_declarator_starttypename : parm_declarator_starttypename array_declarator %prec '.' 
    {
        $$= new NonTerminal( 136 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

parm_declarator_starttypename : TYPENAME 
    {
        $$= new NonTerminal( 136 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

parm_declarator_nostarttypename : parm_declarator_nostarttypename '(' parmlist_or_identifiers %prec '.' 
    {
        $$= new NonTerminal( 73 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

parm_declarator_nostarttypename : parm_declarator_nostarttypename array_declarator %prec '.' 
    {
        $$= new NonTerminal( 73 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

parm_declarator_nostarttypename : '*' maybe_type_quals_attrs parm_declarator_starttypename %prec UNARY 
    {
        $$= new NonTerminal( 73 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

parm_declarator_nostarttypename : '*' maybe_type_quals_attrs parm_declarator_nostarttypename %prec UNARY 
    {
        $$= new NonTerminal( 73 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

parm_declarator_nostarttypename : '(' maybe_attribute parm_declarator_nostarttypename ')' 
    {
        $$= new NonTerminal( 73 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

notype_declarator : notype_declarator '(' parmlist_or_identifiers %prec '.' 
    {
        $$= new NonTerminal( 34 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

notype_declarator : '(' maybe_attribute notype_declarator ')' 
    {
        $$= new NonTerminal( 34 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

notype_declarator : '*' maybe_type_quals_attrs notype_declarator %prec UNARY 
    {
        $$= new NonTerminal( 34 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

notype_declarator : notype_declarator array_declarator %prec '.' 
    {
        $$= new NonTerminal( 34 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

notype_declarator : IDENTIFIER 
    {
        $$= new NonTerminal( 34 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

struct_head : STRUCT 
    {
        $$= new NonTerminal( 130 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

struct_head : STRUCT attributes 
    {
        $$= new NonTerminal( 130 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

union_head : UNION 
    {
        $$= new NonTerminal( 149 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

union_head : UNION attributes 
    {
        $$= new NonTerminal( 149 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

enum_head : ENUM 
    {
        $$= new NonTerminal( 147 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

enum_head : ENUM attributes 
    {
        $$= new NonTerminal( 147 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

structsp_attr : struct_head identifier '{' component_decl_list '}' maybe_attribute 
    {
        $$= new NonTerminal( 115 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

        $5->nextSibbling= $6;

        $$->addChild($6);

        $6->parent= $$;

    }
    ;

structsp_attr : struct_head '{' component_decl_list '}' maybe_attribute 
    {
        $$= new NonTerminal( 115 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

    }
    ;

structsp_attr : union_head identifier '{' component_decl_list '}' maybe_attribute 
    {
        $$= new NonTerminal( 115 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

        $5->nextSibbling= $6;

        $$->addChild($6);

        $6->parent= $$;

    }
    ;

structsp_attr : union_head '{' component_decl_list '}' maybe_attribute 
    {
        $$= new NonTerminal( 115 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

    }
    ;

structsp_attr : enum_head identifier '{' enumlist maybecomma_warn '}' maybe_attribute 
    {
        $$= new NonTerminal( 115 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

        $5->nextSibbling= $6;

        $$->addChild($6);

        $6->parent= $$;

        $6->nextSibbling= $7;

        $$->addChild($7);

        $7->parent= $$;

    }
    ;

structsp_attr : enum_head '{' enumlist maybecomma_warn '}' maybe_attribute 
    {
        $$= new NonTerminal( 115 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

        $5->nextSibbling= $6;

        $$->addChild($6);

        $6->parent= $$;

    }
    ;

structsp_nonattr : struct_head identifier 
    {
        $$= new NonTerminal( 46 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

structsp_nonattr : union_head identifier 
    {
        $$= new NonTerminal( 46 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

structsp_nonattr : enum_head identifier 
    {
        $$= new NonTerminal( 46 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

maybecomma : 
    {
        $$= new NonTerminal( 113 );

    }
    ;

maybecomma : ',' 
    {
        $$= new NonTerminal( 113 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

maybecomma_warn : 
    {
        $$= new NonTerminal( 144 );

    }
    ;

maybecomma_warn : ',' 
    {
        $$= new NonTerminal( 144 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

component_decl_list : component_decl_list2 
    {
        $$= new NonTerminal( 9 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

component_decl_list : component_decl_list2 component_decl 
    {
        $$= new NonTerminal( 9 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

component_decl_list2 : 
    {
        $$= new NonTerminal( 23 );

    }
    ;

component_decl_list2 : component_decl_list2 component_decl ';' 
    {
        $$= new NonTerminal( 23 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

component_decl_list2 : component_decl_list2 ';' 
    {
        $$= new NonTerminal( 23 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

component_decl : declspecs_nosc_ts setspecs components 
    {
        $$= new NonTerminal( 88 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

component_decl : declspecs_nosc_ts setspecs 
    {
        $$= new NonTerminal( 88 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

component_decl : declspecs_nosc_nots setspecs components_notype 
    {
        $$= new NonTerminal( 88 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

component_decl : declspecs_nosc_nots 
    {
        $$= new NonTerminal( 88 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

component_decl : error 
    {
        $$= new NonTerminal( 88 );

    }
    ;

component_decl : extension component_decl 
    {
        $$= new NonTerminal( 88 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

components : component_declarator 
    {
        $$= new NonTerminal( 154 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

components : components ',' maybe_resetattrs component_declarator 
    {
        $$= new NonTerminal( 154 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

components_notype : component_notype_declarator 
    {
        $$= new NonTerminal( 92 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

components_notype : components_notype ',' maybe_resetattrs component_notype_declarator 
    {
        $$= new NonTerminal( 92 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

component_declarator : declarator maybe_attribute 
    {
        $$= new NonTerminal( 60 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

component_declarator : declarator ':' expr_no_commas maybe_attribute 
    {
        $$= new NonTerminal( 60 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

component_declarator : ':' expr_no_commas maybe_attribute 
    {
        $$= new NonTerminal( 60 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

component_notype_declarator : notype_declarator maybe_attribute 
    {
        $$= new NonTerminal( 56 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

component_notype_declarator : notype_declarator ':' expr_no_commas maybe_attribute 
    {
        $$= new NonTerminal( 56 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

component_notype_declarator : ':' expr_no_commas maybe_attribute 
    {
        $$= new NonTerminal( 56 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

enumlist : enumerator 
    {
        $$= new NonTerminal( 148 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

enumlist : enumlist ',' enumerator 
    {
        $$= new NonTerminal( 148 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

enumlist : error 
    {
        $$= new NonTerminal( 148 );

    }
    ;

enumerator : identifier 
    {
        $$= new NonTerminal( 24 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

enumerator : identifier '=' expr_no_commas 
    {
        $$= new NonTerminal( 24 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

typename : declspecs_nosc absdcl 
    {
        $$= new NonTerminal( 52 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

absdcl : 
    {
        $$= new NonTerminal( 7 );

    }
    ;

absdcl : absdcl1 
    {
        $$= new NonTerminal( 7 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

absdcl_maybe_attribute : 
    {
        $$= new NonTerminal( 61 );

    }
    ;

absdcl_maybe_attribute : absdcl1 
    {
        $$= new NonTerminal( 61 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

absdcl_maybe_attribute : absdcl1_noea attributes 
    {
        $$= new NonTerminal( 61 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

absdcl1 : absdcl1_ea 
    {
        $$= new NonTerminal( 48 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

absdcl1 : absdcl1_noea 
    {
        $$= new NonTerminal( 48 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

absdcl1_noea : direct_absdcl1 
    {
        $$= new NonTerminal( 43 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

absdcl1_noea : '*' maybe_type_quals_attrs absdcl1_noea 
    {
        $$= new NonTerminal( 43 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

absdcl1_ea : '*' maybe_type_quals_attrs 
    {
        $$= new NonTerminal( 91 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

absdcl1_ea : '*' maybe_type_quals_attrs absdcl1_ea 
    {
        $$= new NonTerminal( 91 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

direct_absdcl1 : '(' maybe_attribute absdcl1 ')' 
    {
        $$= new NonTerminal( 77 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

direct_absdcl1 : direct_absdcl1 '(' parmlist 
    {
        $$= new NonTerminal( 77 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

direct_absdcl1 : direct_absdcl1 array_declarator 
    {
        $$= new NonTerminal( 77 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

direct_absdcl1 : '(' parmlist 
    {
        $$= new NonTerminal( 77 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

direct_absdcl1 : array_declarator 
    {
        $$= new NonTerminal( 77 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

array_declarator : '[' maybe_type_quals_attrs expr_no_commas ']' 
    {
        $$= new NonTerminal( 15 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

array_declarator : '[' maybe_type_quals_attrs ']' 
    {
        $$= new NonTerminal( 15 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

array_declarator : '[' maybe_type_quals_attrs '*' ']' 
    {
        $$= new NonTerminal( 15 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

array_declarator : '[' STATIC maybe_type_quals_attrs expr_no_commas ']' 
    {
        $$= new NonTerminal( 15 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

    }
    ;

array_declarator : '[' declspecs_nosc_nots STATIC expr_no_commas ']' 
    {
        $$= new NonTerminal( 15 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

    }
    ;

stmts_and_decls : lineno_stmt_decl_or_labels_ending_stmt 
    {
        $$= new NonTerminal( 6 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

stmts_and_decls : lineno_stmt_decl_or_labels_ending_decl 
    {
        $$= new NonTerminal( 6 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

stmts_and_decls : lineno_stmt_decl_or_labels_ending_label 
    {
        $$= new NonTerminal( 6 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

stmts_and_decls : lineno_stmt_decl_or_labels_ending_error 
    {
        $$= new NonTerminal( 6 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

lineno_stmt_decl_or_labels_ending_stmt : lineno_stmt 
    {
        $$= new NonTerminal( 71 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

lineno_stmt_decl_or_labels_ending_stmt : lineno_stmt_decl_or_labels_ending_stmt lineno_stmt 
    {
        $$= new NonTerminal( 71 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

lineno_stmt_decl_or_labels_ending_stmt : lineno_stmt_decl_or_labels_ending_decl lineno_stmt 
    {
        $$= new NonTerminal( 71 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

lineno_stmt_decl_or_labels_ending_stmt : lineno_stmt_decl_or_labels_ending_label lineno_stmt 
    {
        $$= new NonTerminal( 71 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

lineno_stmt_decl_or_labels_ending_stmt : lineno_stmt_decl_or_labels_ending_error lineno_stmt 
    {
        $$= new NonTerminal( 71 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

lineno_stmt_decl_or_labels_ending_decl : lineno_decl 
    {
        $$= new NonTerminal( 51 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

lineno_stmt_decl_or_labels_ending_decl : lineno_stmt_decl_or_labels_ending_stmt lineno_decl 
    {
        $$= new NonTerminal( 51 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

lineno_stmt_decl_or_labels_ending_decl : lineno_stmt_decl_or_labels_ending_decl lineno_decl 
    {
        $$= new NonTerminal( 51 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

lineno_stmt_decl_or_labels_ending_decl : lineno_stmt_decl_or_labels_ending_error lineno_decl 
    {
        $$= new NonTerminal( 51 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

lineno_stmt_decl_or_labels_ending_label : lineno_label 
    {
        $$= new NonTerminal( 58 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

lineno_stmt_decl_or_labels_ending_label : lineno_stmt_decl_or_labels_ending_stmt lineno_label 
    {
        $$= new NonTerminal( 58 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

lineno_stmt_decl_or_labels_ending_label : lineno_stmt_decl_or_labels_ending_decl lineno_label 
    {
        $$= new NonTerminal( 58 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

lineno_stmt_decl_or_labels_ending_label : lineno_stmt_decl_or_labels_ending_label lineno_label 
    {
        $$= new NonTerminal( 58 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

lineno_stmt_decl_or_labels_ending_label : lineno_stmt_decl_or_labels_ending_error lineno_label 
    {
        $$= new NonTerminal( 58 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

lineno_stmt_decl_or_labels_ending_error : errstmt 
    {
        $$= new NonTerminal( 17 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

lineno_stmt_decl_or_labels_ending_error : lineno_stmt_decl_or_labels errstmt 
    {
        $$= new NonTerminal( 17 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

lineno_stmt_decl_or_labels : lineno_stmt_decl_or_labels_ending_stmt 
    {
        $$= new NonTerminal( 110 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

lineno_stmt_decl_or_labels : lineno_stmt_decl_or_labels_ending_decl 
    {
        $$= new NonTerminal( 110 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

lineno_stmt_decl_or_labels : lineno_stmt_decl_or_labels_ending_label 
    {
        $$= new NonTerminal( 110 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

lineno_stmt_decl_or_labels : lineno_stmt_decl_or_labels_ending_error 
    {
        $$= new NonTerminal( 110 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

errstmt : error ';' 
    {
        $$= new NonTerminal( 85 );

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

pushlevel : 
    {
        $$= new NonTerminal( 55 );

    }
    ;

poplevel : 
    {
        $$= new NonTerminal( 123 );

    }
    ;

maybe_label_decls : 
    {
        $$= new NonTerminal( 87 );

    }
    ;

maybe_label_decls : label_decls 
    {
        $$= new NonTerminal( 87 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

label_decls : label_decl 
    {
        $$= new NonTerminal( 135 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

label_decls : label_decls label_decl 
    {
        $$= new NonTerminal( 135 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

label_decl : LABEL identifiers_or_typenames ';' 
    {
        $$= new NonTerminal( 118 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

compstmt_or_error : compstmt 
    {
        $$= new NonTerminal( 69 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

compstmt_or_error : error compstmt 
    {
        $$= new NonTerminal( 69 );

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

compstmt_start : '{' 
    {
        $$= new NonTerminal( 116 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

compstmt_nostart : '}' 
    {
        $$= new NonTerminal( 141 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

compstmt_nostart : pushlevel maybe_label_decls compstmt_contents_nonempty '}' poplevel 
    {
        $$= new NonTerminal( 141 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

    }
    ;

compstmt_contents_nonempty : stmts_and_decls 
    {
        $$= new NonTerminal( 45 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

compstmt_contents_nonempty : error 
    {
        $$= new NonTerminal( 45 );

    }
    ;

compstmt_primary_start : '(' '{' 
    {
        $$= new NonTerminal( 41 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

compstmt : compstmt_start compstmt_nostart 
    {
        $$= new NonTerminal( 72 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

simple_if : if_prefix c99_block_lineno_labeled_stmt 
    {
        $$= new NonTerminal( 103 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

simple_if : if_prefix error 
    {
        $$= new NonTerminal( 103 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

if_prefix : IF '(' expr ')' 
    {
        $$= new NonTerminal( 35 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

do_stmt_start : DO c99_block_lineno_labeled_stmt WHILE 
    {
        $$= new NonTerminal( 89 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

save_location : 
    {
        $$= new NonTerminal( 105 );

    }
    ;

lineno_labeled_stmt : lineno_stmt 
    {
        $$= new NonTerminal( 66 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

lineno_labeled_stmt : lineno_label lineno_labeled_stmt 
    {
        $$= new NonTerminal( 66 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

c99_block_lineno_labeled_stmt : lineno_labeled_stmt 
    {
        $$= new NonTerminal( 33 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

lineno_stmt : save_location stmt 
    {
        $$= new NonTerminal( 99 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

lineno_label : save_location label 
    {
        $$= new NonTerminal( 127 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

select_or_iter_stmt : simple_if ELSE c99_block_lineno_labeled_stmt 
    {
        $$= new NonTerminal( 30 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

select_or_iter_stmt : simple_if %prec IF 
    {
        $$= new NonTerminal( 30 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

select_or_iter_stmt : simple_if ELSE error 
    {
        $$= new NonTerminal( 30 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

select_or_iter_stmt : WHILE '(' expr ')' c99_block_lineno_labeled_stmt 
    {
        $$= new NonTerminal( 30 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

    }
    ;

select_or_iter_stmt : do_stmt_start '(' expr ')' ';' 
    {
        $$= new NonTerminal( 30 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

    }
    ;

select_or_iter_stmt : do_stmt_start error 
    {
        $$= new NonTerminal( 30 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

select_or_iter_stmt : FOR '(' for_init_stmt xexpr ';' xexpr ')' c99_block_lineno_labeled_stmt 
    {
        $$= new NonTerminal( 30 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

        $5->nextSibbling= $6;

        $$->addChild($6);

        $6->parent= $$;

        $6->nextSibbling= $7;

        $$->addChild($7);

        $7->parent= $$;

        $7->nextSibbling= $8;

        $$->addChild($8);

        $8->parent= $$;

    }
    ;

select_or_iter_stmt : SWITCH '(' expr ')' c99_block_lineno_labeled_stmt 
    {
        $$= new NonTerminal( 30 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

    }
    ;

for_init_stmt : xexpr ';' 
    {
        $$= new NonTerminal( 143 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

for_init_stmt : decl 
    {
        $$= new NonTerminal( 143 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

stmt : compstmt 
    {
        $$= new NonTerminal( 65 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

stmt : select_or_iter_stmt 
    {
        $$= new NonTerminal( 65 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

stmt : BREAK ';' 
    {
        $$= new NonTerminal( 65 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

stmt : CONTINUE ';' 
    {
        $$= new NonTerminal( 65 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

stmt : RETURN ';' 
    {
        $$= new NonTerminal( 65 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

stmt : RETURN expr ';' 
    {
        $$= new NonTerminal( 65 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

stmt : ASM_KEYWORD maybe_type_qual '(' expr ')' ';' 
    {
        $$= new NonTerminal( 65 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

        $5->nextSibbling= $6;

        $$->addChild($6);

        $6->parent= $$;

    }
    ;

stmt : ASM_KEYWORD maybe_type_qual '(' expr ':' asm_operands ')' ';' 
    {
        $$= new NonTerminal( 65 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

        $5->nextSibbling= $6;

        $$->addChild($6);

        $6->parent= $$;

        $6->nextSibbling= $7;

        $$->addChild($7);

        $7->parent= $$;

        $7->nextSibbling= $8;

        $$->addChild($8);

        $8->parent= $$;

    }
    ;

stmt : ASM_KEYWORD maybe_type_qual '(' expr ':' asm_operands ':' asm_operands ')' ';' 
    {
        $$= new NonTerminal( 65 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

        $5->nextSibbling= $6;

        $$->addChild($6);

        $6->parent= $$;

        $6->nextSibbling= $7;

        $$->addChild($7);

        $7->parent= $$;

        $7->nextSibbling= $8;

        $$->addChild($8);

        $8->parent= $$;

        $8->nextSibbling= $9;

        $$->addChild($9);

        $9->parent= $$;

        $9->nextSibbling= $10;

        $$->addChild($10);

        $10->parent= $$;

    }
    ;

stmt : ASM_KEYWORD maybe_type_qual '(' expr ':' asm_operands ':' asm_operands ':' asm_clobbers ')' ';' 
    {
        $$= new NonTerminal( 65 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

        $5->nextSibbling= $6;

        $$->addChild($6);

        $6->parent= $$;

        $6->nextSibbling= $7;

        $$->addChild($7);

        $7->parent= $$;

        $7->nextSibbling= $8;

        $$->addChild($8);

        $8->parent= $$;

        $8->nextSibbling= $9;

        $$->addChild($9);

        $9->parent= $$;

        $9->nextSibbling= $10;

        $$->addChild($10);

        $10->parent= $$;

        $10->nextSibbling= $11;

        $$->addChild($11);

        $11->parent= $$;

        $11->nextSibbling= $12;

        $$->addChild($12);

        $12->parent= $$;

    }
    ;

stmt : GOTO identifier ';' 
    {
        $$= new NonTerminal( 65 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

stmt : GOTO '*' expr ';' 
    {
        $$= new NonTerminal( 65 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

stmt : ';' 
    {
        $$= new NonTerminal( 65 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

stmt : exprstmt 
    {
        $$= new NonTerminal( 65 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

exprstmt : expr ';' 
    {
        $$= new NonTerminal( 122 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

exprstmt : primary '(' exprlist ')' stmt 
    {
        $$= new NonTerminal( 122 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

    }
    ;

label : CASE expr_no_commas ':' 
    {
        $$= new NonTerminal( 57 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

label : CASE expr_no_commas ELLIPSIS expr_no_commas ':' 
    {
        $$= new NonTerminal( 57 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

    }
    ;

label : DEFAULT ':' 
    {
        $$= new NonTerminal( 57 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

label : identifier save_location ':' maybe_attribute 
    {
        $$= new NonTerminal( 57 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

maybe_type_qual : 
    {
        $$= new NonTerminal( 81 );

    }
    ;

maybe_type_qual : TYPE_QUAL 
    {
        $$= new NonTerminal( 81 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

xexpr : 
    {
        $$= new NonTerminal( 132 );

    }
    ;

xexpr : expr 
    {
        $$= new NonTerminal( 132 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

asm_operands : 
    {
        $$= new NonTerminal( 117 );

    }
    ;

asm_operands : nonnull_asm_operands 
    {
        $$= new NonTerminal( 117 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

nonnull_asm_operands : asm_operand 
    {
        $$= new NonTerminal( 18 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

nonnull_asm_operands : nonnull_asm_operands ',' asm_operand 
    {
        $$= new NonTerminal( 18 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

asm_operand : STRING '(' expr ')' 
    {
        $$= new NonTerminal( 90 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

asm_operand : '[' identifier ']' STRING '(' expr ')' 
    {
        $$= new NonTerminal( 90 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

        $4->nextSibbling= $5;

        $$->addChild($5);

        $5->parent= $$;

        $5->nextSibbling= $6;

        $$->addChild($6);

        $6->parent= $$;

        $6->nextSibbling= $7;

        $$->addChild($7);

        $7->parent= $$;

    }
    ;

asm_clobbers : STRING 
    {
        $$= new NonTerminal( 29 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

asm_clobbers : asm_clobbers ',' STRING 
    {
        $$= new NonTerminal( 29 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

parmlist : maybe_attribute parmlist_1 
    {
        $$= new NonTerminal( 138 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

parmlist_1 : parmlist_2 ')' 
    {
        $$= new NonTerminal( 139 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

parmlist_1 : parms ';' maybe_attribute parmlist_1 
    {
        $$= new NonTerminal( 139 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

parmlist_1 : error ')' 
    {
        $$= new NonTerminal( 139 );

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

parmlist_2 : 
    {
        $$= new NonTerminal( 133 );

    }
    ;

parmlist_2 : ELLIPSIS 
    {
        $$= new NonTerminal( 133 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

parmlist_2 : parms 
    {
        $$= new NonTerminal( 133 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

parmlist_2 : parms ',' ELLIPSIS 
    {
        $$= new NonTerminal( 133 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

parms : firstparm 
    {
        $$= new NonTerminal( 54 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

parms : parms ',' parm 
    {
        $$= new NonTerminal( 54 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

parm : declspecs_ts setspecs parm_declarator maybe_attribute 
    {
        $$= new NonTerminal( 101 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

parm : declspecs_ts setspecs notype_declarator maybe_attribute 
    {
        $$= new NonTerminal( 101 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

parm : declspecs_ts setspecs absdcl_maybe_attribute 
    {
        $$= new NonTerminal( 101 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

parm : declspecs_nots setspecs notype_declarator maybe_attribute 
    {
        $$= new NonTerminal( 101 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

parm : declspecs_nots setspecs absdcl_maybe_attribute 
    {
        $$= new NonTerminal( 101 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

firstparm : declspecs_ts_nosa setspecs_fp parm_declarator maybe_attribute 
    {
        $$= new NonTerminal( 12 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

firstparm : declspecs_ts_nosa setspecs_fp notype_declarator maybe_attribute 
    {
        $$= new NonTerminal( 12 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

firstparm : declspecs_ts_nosa setspecs_fp absdcl_maybe_attribute 
    {
        $$= new NonTerminal( 12 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

firstparm : declspecs_nots_nosa setspecs_fp notype_declarator maybe_attribute 
    {
        $$= new NonTerminal( 12 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

        $3->nextSibbling= $4;

        $$->addChild($4);

        $4->parent= $$;

    }
    ;

firstparm : declspecs_nots_nosa setspecs_fp absdcl_maybe_attribute 
    {
        $$= new NonTerminal( 12 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

setspecs_fp : setspecs 
    {
        $$= new NonTerminal( 5 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

parmlist_or_identifiers : maybe_attribute parmlist_or_identifiers_1 
    {
        $$= new NonTerminal( 21 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

parmlist_or_identifiers_1 : parmlist_1 
    {
        $$= new NonTerminal( 64 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

parmlist_or_identifiers_1 : identifiers ')' 
    {
        $$= new NonTerminal( 64 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

    }
    ;

identifiers : IDENTIFIER 
    {
        $$= new NonTerminal( 152 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

identifiers : identifiers ',' IDENTIFIER 
    {
        $$= new NonTerminal( 152 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

identifiers_or_typenames : identifier 
    {
        $$= new NonTerminal( 63 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;

identifiers_or_typenames : identifiers_or_typenames ',' identifier 
    {
        $$= new NonTerminal( 63 );

        $$->addChild($1);

        $1->parent= $$;

        $1->nextSibbling= $2;

        $$->addChild($2);

        $2->parent= $$;

        $2->nextSibbling= $3;

        $$->addChild($3);

        $3->parent= $$;

    }
    ;

extension : EXTENSION 
    {
        $$= new NonTerminal( 111 );

        $$->addChild($1);

        $1->parent= $$;

    }
    ;



%%


#include <stdio.h>

extern char yytext[];
extern int column;
extern int line;

void yyerror( char *s)
{
fflush(stdout);
fprintf(stderr,"%s: %d.%d\n",s,line,column);
}


