/* A Bison parser, made by GNU Bison 2.3.  */

/* Skeleton interface for Bison's Yacc-like parsers in C

   Copyright (C) 1984, 1989, 1990, 2000, 2001, 2002, 2003, 2004, 2005, 2006
   Free Software Foundation, Inc.

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2, or (at your option)
   any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor,
   Boston, MA 02110-1301, USA.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.

   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

/* Tokens.  */
#ifndef YYTOKENTYPE
# define YYTOKENTYPE
   /* Put the tokens into the symbol table, so that GDB and other debuggers
      know about them.  */
   enum yytokentype {
     IDENTIFIER = 258,
     TYPENAME = 259,
     SCSPEC = 260,
     STATIC = 261,
     TYPESPEC = 262,
     TYPE_QUAL = 263,
     CONSTANT = 264,
     STRING = 265,
     ELLIPSIS = 266,
     SIZEOF = 267,
     ENUM = 268,
     STRUCT = 269,
     UNION = 270,
     IF = 271,
     ELSE = 272,
     WHILE = 273,
     DO = 274,
     FOR = 275,
     SWITCH = 276,
     CASE = 277,
     DEFAULT = 278,
     BREAK = 279,
     CONTINUE = 280,
     RETURN = 281,
     GOTO = 282,
     ASM_KEYWORD = 283,
     TYPEOF = 284,
     ALIGNOF = 285,
     ATTRIBUTE = 286,
     EXTENSION = 287,
     LABEL = 288,
     REALPART = 289,
     IMAGPART = 290,
     VA_ARG = 291,
     CHOOSE_EXPR = 292,
     TYPES_COMPATIBLE_P = 293,
     PTR_VALUE = 294,
     PTR_BASE = 295,
     PTR_EXTENT = 296,
     FUNC_NAME = 297,
     ASSIGN = 298,
     OROR = 299,
     ANDAND = 300,
     EQCOMPARE = 301,
     ARITHCOMPARE = 302,
     RSHIFT = 303,
     LSHIFT = 304,
     MINUSMINUS = 305,
     PLUSPLUS = 306,
     UNARY = 307,
     HYPERUNARY = 308,
     POINTSAT = 309,
     INTERFACE = 310,
     IMPLEMENTATION = 311,
     END = 312,
     SELECTOR = 313,
     DEFS = 314,
     ENCODE = 315,
     CLASSNAME = 316,
     PUBLIC = 317,
     PRIVATE = 318,
     PROTECTED = 319,
     PROTOCOL = 320,
     OBJECTNAME = 321,
     CLASS = 322,
     ALIAS = 323,
     AT_THROW = 324,
     AT_TRY = 325,
     AT_CATCH = 326,
     AT_FINALLY = 327,
     AT_SYNCHRONIZED = 328,
     OBJC_STRING = 329
   };
#endif
/* Tokens.  */
#define IDENTIFIER 258
#define TYPENAME 259
#define SCSPEC 260
#define STATIC 261
#define TYPESPEC 262
#define TYPE_QUAL 263
#define CONSTANT 264
#define STRING 265
#define ELLIPSIS 266
#define SIZEOF 267
#define ENUM 268
#define STRUCT 269
#define UNION 270
#define IF 271
#define ELSE 272
#define WHILE 273
#define DO 274
#define FOR 275
#define SWITCH 276
#define CASE 277
#define DEFAULT 278
#define BREAK 279
#define CONTINUE 280
#define RETURN 281
#define GOTO 282
#define ASM_KEYWORD 283
#define TYPEOF 284
#define ALIGNOF 285
#define ATTRIBUTE 286
#define EXTENSION 287
#define LABEL 288
#define REALPART 289
#define IMAGPART 290
#define VA_ARG 291
#define CHOOSE_EXPR 292
#define TYPES_COMPATIBLE_P 293
#define PTR_VALUE 294
#define PTR_BASE 295
#define PTR_EXTENT 296
#define FUNC_NAME 297
#define ASSIGN 298
#define OROR 299
#define ANDAND 300
#define EQCOMPARE 301
#define ARITHCOMPARE 302
#define RSHIFT 303
#define LSHIFT 304
#define MINUSMINUS 305
#define PLUSPLUS 306
#define UNARY 307
#define HYPERUNARY 308
#define POINTSAT 309
#define INTERFACE 310
#define IMPLEMENTATION 311
#define END 312
#define SELECTOR 313
#define DEFS 314
#define ENCODE 315
#define CLASSNAME 316
#define PUBLIC 317
#define PRIVATE 318
#define PROTECTED 319
#define PROTOCOL 320
#define OBJECTNAME 321
#define CLASS 322
#define ALIAS 323
#define AT_THROW 324
#define AT_TRY 325
#define AT_CATCH 326
#define AT_FINALLY 327
#define AT_SYNCHRONIZED 328
#define OBJC_STRING 329




#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
typedef union YYSTYPE
#line 10 "pt_c.y"
{
Tree *t;
}
/* Line 1529 of yacc.c.  */
#line 201 "pt_c.tab.hh"
	YYSTYPE;
# define yystype YYSTYPE /* obsolescent; will be withdrawn */
# define YYSTYPE_IS_DECLARED 1
# define YYSTYPE_IS_TRIVIAL 1
#endif



