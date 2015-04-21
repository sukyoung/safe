/* A Bison parser, made by GNU Bison 2.3.  */

/* Skeleton implementation for Bison's Yacc-like parsers in C

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

/* C LALR(1) parser skeleton written by Richard Stallman, by
   simplifying the original so-called "semantic" parser.  */

/* All symbols defined below should begin with yy or YY, to avoid
   infringing on user name space.  This should be done even for local
   variables, as they might otherwise be expanded by user macros.
   There are some unavoidable exceptions within include files to
   define necessary library symbols; they are noted "INFRINGES ON
   USER NAME SPACE" below.  */

/* Identify Bison output.  */
#define YYBISON 1

/* Bison version.  */
#define YYBISON_VERSION "2.3"

/* Skeleton name.  */
#define YYSKELETON_NAME "yacc.c"

/* Pure parsers.  */
#define YYPURE 1

/* Using locations.  */
#define YYLSP_NEEDED 0



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




/* Copy the first part of user declarations.  */
#line 4 "pt_c.y"

#include<ptree.h>

using namespace std;


/* Enabling traces.  */
#ifndef YYDEBUG
# define YYDEBUG 0
#endif

/* Enabling verbose error messages.  */
#ifdef YYERROR_VERBOSE
# undef YYERROR_VERBOSE
# define YYERROR_VERBOSE 1
#else
# define YYERROR_VERBOSE 0
#endif

/* Enabling the token table.  */
#ifndef YYTOKEN_TABLE
# define YYTOKEN_TABLE 0
#endif

#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
typedef union YYSTYPE
#line 10 "pt_c.y"
{
Tree *t;
}
/* Line 193 of yacc.c.  */
#line 254 "pt_c.tab.cc"
	YYSTYPE;
# define yystype YYSTYPE /* obsolescent; will be withdrawn */
# define YYSTYPE_IS_DECLARED 1
# define YYSTYPE_IS_TRIVIAL 1
#endif



/* Copy the second part of user declarations.  */
#line 14 "pt_c.y"

void yyerror(char*s);
int yylex(YYSTYPE *yylvalp);

Tree *root;
#line 179 "pt_c.y"

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




/* Line 216 of yacc.c.  */
#line 295 "pt_c.tab.cc"

#ifdef short
# undef short
#endif

#ifdef YYTYPE_UINT8
typedef YYTYPE_UINT8 yytype_uint8;
#else
typedef unsigned char yytype_uint8;
#endif

#ifdef YYTYPE_INT8
typedef YYTYPE_INT8 yytype_int8;
#elif (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
typedef signed char yytype_int8;
#else
typedef short int yytype_int8;
#endif

#ifdef YYTYPE_UINT16
typedef YYTYPE_UINT16 yytype_uint16;
#else
typedef unsigned short int yytype_uint16;
#endif

#ifdef YYTYPE_INT16
typedef YYTYPE_INT16 yytype_int16;
#else
typedef short int yytype_int16;
#endif

#ifndef YYSIZE_T
# ifdef __SIZE_TYPE__
#  define YYSIZE_T __SIZE_TYPE__
# elif defined size_t
#  define YYSIZE_T size_t
# elif ! defined YYSIZE_T && (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
#  include <stddef.h> /* INFRINGES ON USER NAME SPACE */
#  define YYSIZE_T size_t
# else
#  define YYSIZE_T unsigned int
# endif
#endif

#define YYSIZE_MAXIMUM ((YYSIZE_T) -1)

#ifndef YY_
# if defined YYENABLE_NLS && YYENABLE_NLS
#  if ENABLE_NLS
#   include <libintl.h> /* INFRINGES ON USER NAME SPACE */
#   define YY_(msgid) dgettext ("bison-runtime", msgid)
#  endif
# endif
# ifndef YY_
#  define YY_(msgid) msgid
# endif
#endif

/* Suppress unused-variable warnings by "using" E.  */
#if ! defined lint || defined __GNUC__
# define YYUSE(e) ((void) (e))
#else
# define YYUSE(e) /* empty */
#endif

/* Identity function, used to suppress warnings about constant conditions.  */
#ifndef lint
# define YYID(n) (n)
#else
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static int
YYID (int i)
#else
static int
YYID (i)
    int i;
#endif
{
  return i;
}
#endif

#if ! defined yyoverflow || YYERROR_VERBOSE

/* The parser invokes alloca or malloc; define the necessary symbols.  */

# ifdef YYSTACK_USE_ALLOCA
#  if YYSTACK_USE_ALLOCA
#   ifdef __GNUC__
#    define YYSTACK_ALLOC __builtin_alloca
#   elif defined __BUILTIN_VA_ARG_INCR
#    include <alloca.h> /* INFRINGES ON USER NAME SPACE */
#   elif defined _AIX
#    define YYSTACK_ALLOC __alloca
#   elif defined _MSC_VER
#    include <malloc.h> /* INFRINGES ON USER NAME SPACE */
#    define alloca _alloca
#   else
#    define YYSTACK_ALLOC alloca
#    if ! defined _ALLOCA_H && ! defined _STDLIB_H && (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
#     include <stdlib.h> /* INFRINGES ON USER NAME SPACE */
#     ifndef _STDLIB_H
#      define _STDLIB_H 1
#     endif
#    endif
#   endif
#  endif
# endif

# ifdef YYSTACK_ALLOC
   /* Pacify GCC's `empty if-body' warning.  */
#  define YYSTACK_FREE(Ptr) do { /* empty */; } while (YYID (0))
#  ifndef YYSTACK_ALLOC_MAXIMUM
    /* The OS might guarantee only one guard page at the bottom of the stack,
       and a page size can be as small as 4096 bytes.  So we cannot safely
       invoke alloca (N) if N exceeds 4096.  Use a slightly smaller number
       to allow for a few compiler-allocated temporary stack slots.  */
#   define YYSTACK_ALLOC_MAXIMUM 4032 /* reasonable circa 2006 */
#  endif
# else
#  define YYSTACK_ALLOC YYMALLOC
#  define YYSTACK_FREE YYFREE
#  ifndef YYSTACK_ALLOC_MAXIMUM
#   define YYSTACK_ALLOC_MAXIMUM YYSIZE_MAXIMUM
#  endif
#  if (defined __cplusplus && ! defined _STDLIB_H \
       && ! ((defined YYMALLOC || defined malloc) \
	     && (defined YYFREE || defined free)))
#   include <stdlib.h> /* INFRINGES ON USER NAME SPACE */
#   ifndef _STDLIB_H
#    define _STDLIB_H 1
#   endif
#  endif
#  ifndef YYMALLOC
#   define YYMALLOC malloc
#   if ! defined malloc && ! defined _STDLIB_H && (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
void *malloc (YYSIZE_T); /* INFRINGES ON USER NAME SPACE */
#   endif
#  endif
#  ifndef YYFREE
#   define YYFREE free
#   if ! defined free && ! defined _STDLIB_H && (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
void free (void *); /* INFRINGES ON USER NAME SPACE */
#   endif
#  endif
# endif
#endif /* ! defined yyoverflow || YYERROR_VERBOSE */


#if (! defined yyoverflow \
     && (! defined __cplusplus \
	 || (defined YYSTYPE_IS_TRIVIAL && YYSTYPE_IS_TRIVIAL)))

/* A type that is properly aligned for any stack member.  */
union yyalloc
{
  yytype_int16 yyss;
  YYSTYPE yyvs;
  };

/* The size of the maximum gap between one aligned stack and the next.  */
# define YYSTACK_GAP_MAXIMUM (sizeof (union yyalloc) - 1)

/* The size of an array large to enough to hold all stacks, each with
   N elements.  */
# define YYSTACK_BYTES(N) \
     ((N) * (sizeof (yytype_int16) + sizeof (YYSTYPE)) \
      + YYSTACK_GAP_MAXIMUM)

/* Copy COUNT objects from FROM to TO.  The source and destination do
   not overlap.  */
# ifndef YYCOPY
#  if defined __GNUC__ && 1 < __GNUC__
#   define YYCOPY(To, From, Count) \
      __builtin_memcpy (To, From, (Count) * sizeof (*(From)))
#  else
#   define YYCOPY(To, From, Count)		\
      do					\
	{					\
	  YYSIZE_T yyi;				\
	  for (yyi = 0; yyi < (Count); yyi++)	\
	    (To)[yyi] = (From)[yyi];		\
	}					\
      while (YYID (0))
#  endif
# endif

/* Relocate STACK from its old location to the new one.  The
   local variables YYSIZE and YYSTACKSIZE give the old and new number of
   elements in the stack, and YYPTR gives the new location of the
   stack.  Advance YYPTR to a properly aligned location for the next
   stack.  */
# define YYSTACK_RELOCATE(Stack)					\
    do									\
      {									\
	YYSIZE_T yynewbytes;						\
	YYCOPY (&yyptr->Stack, Stack, yysize);				\
	Stack = &yyptr->Stack;						\
	yynewbytes = yystacksize * sizeof (*Stack) + YYSTACK_GAP_MAXIMUM; \
	yyptr += yynewbytes / sizeof (*yyptr);				\
      }									\
    while (YYID (0))

#endif

/* YYFINAL -- State number of the termination state.  */
#define YYFINAL  63
/* YYLAST -- Last index in YYTABLE.  */
#define YYLAST   3650

/* YYNTOKENS -- Number of terminals.  */
#define YYNTOKENS  97
/* YYNNTS -- Number of nonterminals.  */
#define YYNNTS  158
/* YYNRULES -- Number of rules.  */
#define YYNRULES  518
/* YYNRULES -- Number of states.  */
#define YYNSTATES  844

/* YYTRANSLATE(YYLEX) -- Bison symbol number corresponding to YYLEX.  */
#define YYUNDEFTOK  2
#define YYMAXUTOK   329

#define YYTRANSLATE(YYX)						\
  ((unsigned int) (YYX) <= YYMAXUTOK ? yytranslate[YYX] : YYUNDEFTOK)

/* YYTRANSLATE[YYLEX] -- Bison symbol number corresponding to YYLEX.  */
static const yytype_uint8 yytranslate[] =
{
       0,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,    50,     2,     2,     2,    68,    59,     2,
      74,    44,    66,    64,    43,    65,    73,    67,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,    54,    46,
       2,    51,     2,    53,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,    75,     2,    45,    58,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,    48,    57,    47,    49,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     1,     2,     3,     4,
       5,     6,     7,     8,     9,    10,    11,    12,    13,    14,
      15,    16,    17,    18,    19,    20,    21,    22,    23,    24,
      25,    26,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    39,    40,    41,    42,    52,    55,
      56,    60,    61,    62,    63,    69,    70,    71,    72,    76,
      77,    78,    79,    80,    81,    82,    83,    84,    85,    86,
      87,    88,    89,    90,    91,    92,    93,    94,    95,    96
};

#if YYDEBUG
/* YYPRHS[YYN] -- Index of the first RHS symbol of rule number YYN in
   YYRHS.  */
static const yytype_uint16 yyprhs[] =
{
       0,     0,     3,     4,     6,     8,    11,    13,    15,    17,
      23,    26,    30,    35,    40,    43,    46,    49,    51,    58,
      63,    70,    75,    81,    85,    87,    89,    91,    93,    95,
      97,    99,   101,   103,   105,   106,   108,   110,   114,   116,
     119,   122,   125,   128,   131,   136,   139,   144,   147,   150,
     152,   154,   156,   158,   163,   165,   169,   173,   177,   181,
     185,   189,   193,   197,   201,   205,   209,   213,   217,   221,
     227,   232,   236,   240,   242,   245,   249,   252,   254,   256,
     258,   265,   269,   273,   277,   281,   286,   293,   302,   309,
     314,   318,   322,   325,   328,   330,   331,   333,   336,   338,
     340,   343,   346,   351,   356,   359,   362,   365,   366,   368,
     373,   378,   382,   386,   389,   392,   394,   397,   400,   403,
     406,   409,   411,   414,   416,   419,   422,   425,   428,   431,
     434,   436,   439,   442,   445,   448,   451,   454,   457,   460,
     463,   466,   469,   472,   475,   478,   481,   484,   486,   489,
     492,   495,   498,   501,   504,   507,   510,   513,   516,   519,
     522,   525,   528,   531,   534,   537,   540,   543,   546,   549,
     552,   555,   558,   561,   564,   567,   570,   573,   576,   579,
     582,   585,   588,   591,   594,   597,   600,   603,   606,   609,
     612,   615,   618,   620,   622,   624,   626,   628,   630,   632,
     634,   636,   638,   640,   642,   644,   646,   648,   650,   652,
     654,   656,   658,   660,   662,   664,   666,   668,   670,   672,
     674,   676,   678,   680,   682,   684,   686,   688,   690,   692,
     694,   696,   698,   700,   702,   704,   706,   708,   710,   712,
     714,   716,   718,   720,   722,   724,   726,   728,   730,   731,
     733,   735,   737,   739,   741,   743,   745,   747,   752,   757,
     759,   764,   766,   771,   772,   777,   783,   787,   793,   797,
     798,   800,   802,   805,   812,   814,   818,   819,   821,   826,
     833,   838,   840,   842,   844,   846,   848,   850,   852,   856,
     858,   859,   862,   864,   868,   872,   875,   879,   881,   885,
     887,   889,   891,   894,   897,   903,   907,   912,   917,   919,
     921,   926,   930,   933,   937,   939,   941,   943,   947,   950,
     952,   956,   959,   963,   967,   972,   976,   981,   985,   988,
     990,   992,   995,   997,  1000,  1002,  1005,  1012,  1018,  1025,
    1031,  1039,  1046,  1049,  1052,  1055,  1056,  1058,  1059,  1061,
    1063,  1066,  1067,  1071,  1074,  1078,  1081,  1085,  1087,  1089,
    1092,  1094,  1099,  1101,  1106,  1109,  1114,  1118,  1121,  1126,
    1130,  1132,  1136,  1138,  1140,  1144,  1147,  1148,  1150,  1151,
    1153,  1156,  1158,  1160,  1162,  1166,  1169,  1173,  1178,  1182,
    1185,  1188,  1190,  1195,  1199,  1204,  1210,  1216,  1218,  1220,
    1222,  1224,  1226,  1229,  1232,  1235,  1238,  1240,  1243,  1246,
    1249,  1251,  1254,  1257,  1260,  1263,  1265,  1268,  1270,  1272,
    1274,  1276,  1279,  1280,  1281,  1282,  1284,  1286,  1289,  1293,
    1295,  1298,  1300,  1302,  1308,  1310,  1312,  1315,  1318,  1321,
    1324,  1329,  1333,  1334,  1336,  1339,  1341,  1344,  1347,  1351,
    1353,  1357,  1363,  1369,  1372,  1381,  1387,  1390,  1392,  1394,
    1396,  1399,  1402,  1405,  1409,  1416,  1425,  1436,  1449,  1453,
    1458,  1460,  1462,  1465,  1471,  1475,  1481,  1484,  1489,  1490,
    1492,  1493,  1495,  1496,  1498,  1500,  1504,  1509,  1517,  1519,
    1523,  1526,  1529,  1534,  1537,  1538,  1540,  1542,  1546,  1548,
    1552,  1557,  1562,  1566,  1571,  1575,  1580,  1585,  1589,  1594,
    1598,  1600,  1603,  1605,  1608,  1610,  1614,  1616,  1620
};

/* YYRHS -- A `-1'-separated list of the rules' RHS.  */
static const yytype_int16 yyrhs[] =
{
      98,     0,    -1,    -1,    99,    -1,   100,    -1,    99,   100,
      -1,   101,    -1,   103,    -1,   102,    -1,    28,    74,   106,
      44,    46,    -1,   254,   100,    -1,   122,   156,    46,    -1,
     142,   122,   156,    46,    -1,   141,   122,   155,    46,    -1,
     148,    46,    -1,     1,    46,    -1,     1,    47,    -1,    46,
      -1,   141,   122,   176,   116,   227,   218,    -1,   141,   122,
     176,     1,    -1,   142,   122,   181,   116,   227,   218,    -1,
     142,   122,   181,     1,    -1,   122,   181,   116,   227,   218,
      -1,   122,   181,     1,    -1,     3,    -1,     4,    -1,    59,
      -1,    65,    -1,    64,    -1,    70,    -1,    69,    -1,    49,
      -1,    50,    -1,   108,    -1,    -1,   108,    -1,   114,    -1,
     108,    43,   114,    -1,   115,    -1,    66,   113,    -1,   254,
     113,    -1,   105,   113,    -1,    56,   104,    -1,   110,   109,
      -1,   110,    74,   198,    44,    -1,   111,   109,    -1,   111,
      74,   198,    44,    -1,    34,   113,    -1,    35,   113,    -1,
      12,    -1,    30,    -1,    29,    -1,   109,    -1,    74,   198,
      44,   113,    -1,   113,    -1,   114,    64,   114,    -1,   114,
      65,   114,    -1,   114,    66,   114,    -1,   114,    67,   114,
      -1,   114,    68,   114,    -1,   114,    63,   114,    -1,   114,
      62,   114,    -1,   114,    61,   114,    -1,   114,    60,   114,
      -1,   114,    59,   114,    -1,   114,    57,   114,    -1,   114,
      58,   114,    -1,   114,    56,   114,    -1,   114,    55,   114,
      -1,   114,    53,   106,    54,   114,    -1,   114,    53,    54,
     114,    -1,   114,    51,   114,    -1,   114,    52,   114,    -1,
       3,    -1,     3,    10,    -1,     3,    10,     3,    -1,    10,
       3,    -1,     9,    -1,    10,    -1,    42,    -1,    74,   198,
      44,    48,   168,    47,    -1,    74,   106,    44,    -1,    74,
       1,    44,    -1,   222,   220,    44,    -1,   222,     1,    44,
      -1,   115,    74,   107,    44,    -1,    36,    74,   114,    43,
     198,    44,    -1,    37,    74,   114,    43,   114,    43,   114,
      44,    -1,    38,    74,   198,    43,   198,    44,    -1,   115,
      75,   106,    45,    -1,   115,    73,   104,    -1,   115,    76,
     104,    -1,   115,    70,    -1,   115,    69,    -1,   117,    -1,
      -1,   119,    -1,   227,   120,    -1,   118,    -1,   212,    -1,
     119,   118,    -1,   118,   212,    -1,   143,   122,   155,    46,
      -1,   144,   122,   156,    46,    -1,   143,    46,    -1,   144,
      46,    -1,   227,   124,    -1,    -1,   160,    -1,   141,   122,
     155,    46,    -1,   142,   122,   156,    46,    -1,   141,   122,
     174,    -1,   142,   122,   175,    -1,   148,    46,    -1,   254,
     124,    -1,     8,    -1,   125,     8,    -1,   126,     8,    -1,
     125,   161,    -1,   127,     8,    -1,   128,     8,    -1,   161,
      -1,   127,   161,    -1,   150,    -1,   129,     8,    -1,   130,
       8,    -1,   129,   152,    -1,   130,   152,    -1,   125,   150,
      -1,   126,   150,    -1,   151,    -1,   129,   161,    -1,   129,
     153,    -1,   130,   153,    -1,   125,   151,    -1,   126,   151,
      -1,   131,     8,    -1,   132,     8,    -1,   131,   152,    -1,
     132,   152,    -1,   127,   150,    -1,   128,   150,    -1,   131,
     161,    -1,   131,   153,    -1,   132,   153,    -1,   127,   151,
      -1,   128,   151,    -1,   166,    -1,   133,     8,    -1,   134,
       8,    -1,   125,   166,    -1,   126,   166,    -1,   133,   166,
      -1,   134,   166,    -1,   133,   161,    -1,   135,     8,    -1,
     136,     8,    -1,   127,   166,    -1,   128,   166,    -1,   135,
     166,    -1,   136,   166,    -1,   135,   161,    -1,   137,     8,
      -1,   138,     8,    -1,   137,   152,    -1,   138,   152,    -1,
     133,   150,    -1,   134,   150,    -1,   129,   166,    -1,   130,
     166,    -1,   137,   166,    -1,   138,   166,    -1,   137,   161,
      -1,   137,   153,    -1,   138,   153,    -1,   133,   151,    -1,
     134,   151,    -1,   139,     8,    -1,   140,     8,    -1,   139,
     152,    -1,   140,   152,    -1,   135,   150,    -1,   136,   150,
      -1,   131,   166,    -1,   132,   166,    -1,   139,   166,    -1,
     140,   166,    -1,   139,   161,    -1,   139,   153,    -1,   140,
     153,    -1,   135,   151,    -1,   136,   151,    -1,   129,    -1,
     130,    -1,   131,    -1,   132,    -1,   137,    -1,   138,    -1,
     139,    -1,   140,    -1,   125,    -1,   126,    -1,   127,    -1,
     128,    -1,   133,    -1,   134,    -1,   135,    -1,   136,    -1,
     129,    -1,   130,    -1,   137,    -1,   138,    -1,   125,    -1,
     126,    -1,   133,    -1,   134,    -1,   129,    -1,   130,    -1,
     131,    -1,   132,    -1,   125,    -1,   126,    -1,   127,    -1,
     128,    -1,   129,    -1,   130,    -1,   131,    -1,   132,    -1,
     125,    -1,   126,    -1,   127,    -1,   128,    -1,   125,    -1,
     126,    -1,   127,    -1,   128,    -1,   129,    -1,   130,    -1,
     131,    -1,   132,    -1,   133,    -1,   134,    -1,   135,    -1,
     136,    -1,   137,    -1,   138,    -1,   139,    -1,   140,    -1,
      -1,   146,    -1,   152,    -1,   154,    -1,   153,    -1,     7,
      -1,   186,    -1,   185,    -1,     4,    -1,   112,    74,   106,
      44,    -1,   112,    74,   198,    44,    -1,   158,    -1,   155,
      43,   123,   158,    -1,   159,    -1,   156,    43,   123,   159,
      -1,    -1,    28,    74,    10,    44,    -1,   176,   157,   160,
      51,   167,    -1,   176,   157,   160,    -1,   181,   157,   160,
      51,   167,    -1,   181,   157,   160,    -1,    -1,   161,    -1,
     162,    -1,   161,   162,    -1,    31,    74,    74,   163,    44,
      44,    -1,   164,    -1,   163,    43,   164,    -1,    -1,   165,
      -1,   165,    74,     3,    44,    -1,   165,    74,     3,    43,
     108,    44,    -1,   165,    74,   107,    44,    -1,   104,    -1,
     166,    -1,     7,    -1,     8,    -1,     6,    -1,     5,    -1,
     114,    -1,    48,   168,    47,    -1,     1,    -1,    -1,   169,
     187,    -1,   170,    -1,   169,    43,   170,    -1,   172,    51,
     171,    -1,   173,   171,    -1,   104,    54,   171,    -1,   171,
      -1,    48,   168,    47,    -1,   114,    -1,     1,    -1,   173,
      -1,   172,   173,    -1,    73,   104,    -1,    75,   114,    11,
     114,    45,    -1,    75,   114,    45,    -1,   176,   116,   227,
     223,    -1,   181,   116,   227,   223,    -1,   177,    -1,   181,
      -1,    74,   160,   177,    44,    -1,   177,    74,   250,    -1,
     177,   205,    -1,    66,   149,   177,    -1,     4,    -1,   179,
      -1,   180,    -1,   179,    74,   250,    -1,   179,   205,    -1,
       4,    -1,   180,    74,   250,    -1,   180,   205,    -1,    66,
     149,   179,    -1,    66,   149,   180,    -1,    74,   160,   180,
      44,    -1,   181,    74,   250,    -1,    74,   160,   181,    44,
      -1,    66,   149,   181,    -1,   181,   205,    -1,     3,    -1,
      14,    -1,    14,   161,    -1,    15,    -1,    15,   161,    -1,
      13,    -1,    13,   161,    -1,   182,   104,    48,   189,    47,
     160,    -1,   182,    48,   189,    47,   160,    -1,   183,   104,
      48,   189,    47,   160,    -1,   183,    48,   189,    47,   160,
      -1,   184,   104,    48,   196,   188,    47,   160,    -1,   184,
      48,   196,   188,    47,   160,    -1,   182,   104,    -1,   183,
     104,    -1,   184,   104,    -1,    -1,    43,    -1,    -1,    43,
      -1,   190,    -1,   190,   191,    -1,    -1,   190,   191,    46,
      -1,   190,    46,    -1,   145,   122,   192,    -1,   145,   122,
      -1,   146,   122,   193,    -1,   146,    -1,     1,    -1,   254,
     191,    -1,   194,    -1,   192,    43,   123,   194,    -1,   195,
      -1,   193,    43,   123,   195,    -1,   176,   160,    -1,   176,
      54,   114,   160,    -1,    54,   114,   160,    -1,   181,   160,
      -1,   181,    54,   114,   160,    -1,    54,   114,   160,    -1,
     197,    -1,   196,    43,   197,    -1,     1,    -1,   104,    -1,
     104,    51,   114,    -1,   147,   199,    -1,    -1,   201,    -1,
      -1,   201,    -1,   202,   161,    -1,   203,    -1,   202,    -1,
     204,    -1,    66,   149,   202,    -1,    66,   149,    -1,    66,
     149,   203,    -1,    74,   160,   201,    44,    -1,   204,    74,
     243,    -1,   204,   205,    -1,    74,   243,    -1,   205,    -1,
      75,   149,   114,    45,    -1,    75,   149,    45,    -1,    75,
     149,    66,    45,    -1,    75,     6,   149,   114,    45,    -1,
      75,   146,     6,   114,    45,    -1,   207,    -1,   208,    -1,
     209,    -1,   210,    -1,   230,    -1,   207,   230,    -1,   208,
     230,    -1,   209,   230,    -1,   210,   230,    -1,   121,    -1,
     207,   121,    -1,   208,   121,    -1,   210,   121,    -1,   231,
      -1,   207,   231,    -1,   208,   231,    -1,   209,   231,    -1,
     210,   231,    -1,   212,    -1,   211,   212,    -1,   207,    -1,
     208,    -1,   209,    -1,   210,    -1,     1,    46,    -1,    -1,
      -1,    -1,   216,    -1,   217,    -1,   216,   217,    -1,    33,
     253,    46,    -1,   223,    -1,     1,   223,    -1,    48,    -1,
      47,    -1,   213,   215,   221,    47,   214,    -1,   206,    -1,
       1,    -1,    74,    48,    -1,   219,   220,    -1,   225,   229,
      -1,   225,     1,    -1,    16,    74,   106,    44,    -1,    19,
     229,    18,    -1,    -1,   230,    -1,   231,   228,    -1,   228,
      -1,   227,   234,    -1,   227,   236,    -1,   224,    17,   229,
      -1,   224,    -1,   224,    17,     1,    -1,    18,    74,   106,
      44,   229,    -1,   226,    74,   106,    44,    46,    -1,   226,
       1,    -1,    20,    74,   233,   238,    46,   238,    44,   229,
      -1,    21,    74,   106,    44,   229,    -1,   238,    46,    -1,
     124,    -1,   223,    -1,   232,    -1,    24,    46,    -1,    25,
      46,    -1,    26,    46,    -1,    26,   106,    46,    -1,    28,
     237,    74,   106,    44,    46,    -1,    28,   237,    74,   106,
      54,   239,    44,    46,    -1,    28,   237,    74,   106,    54,
     239,    54,   239,    44,    46,    -1,    28,   237,    74,   106,
      54,   239,    54,   239,    54,   242,    44,    46,    -1,    27,
     104,    46,    -1,    27,    66,   106,    46,    -1,    46,    -1,
     235,    -1,   106,    46,    -1,   115,    74,   107,    44,   234,
      -1,    22,   114,    54,    -1,    22,   114,    11,   114,    54,
      -1,    23,    54,    -1,   104,   227,    54,   160,    -1,    -1,
       8,    -1,    -1,   106,    -1,    -1,   240,    -1,   241,    -1,
     240,    43,   241,    -1,    10,    74,   106,    44,    -1,    75,
     104,    45,    10,    74,   106,    44,    -1,    10,    -1,   242,
      43,    10,    -1,   160,   244,    -1,   245,    44,    -1,   246,
      46,   160,   244,    -1,     1,    44,    -1,    -1,    11,    -1,
     246,    -1,   246,    43,    11,    -1,   248,    -1,   246,    43,
     247,    -1,   141,   122,   178,   160,    -1,   141,   122,   181,
     160,    -1,   141,   122,   200,    -1,   142,   122,   181,   160,
      -1,   142,   122,   200,    -1,   143,   249,   178,   160,    -1,
     143,   249,   181,   160,    -1,   143,   249,   200,    -1,   144,
     249,   181,   160,    -1,   144,   249,   200,    -1,   122,    -1,
     160,   251,    -1,   244,    -1,   252,    44,    -1,     3,    -1,
     252,    43,     3,    -1,   104,    -1,   253,    43,   104,    -1,
      32,    -1
};

/* YYRLINE[YYN] -- source line where rule number YYN was defined.  */
static const yytype_uint16 yyrline[] =
{
       0,   282,   282,   289,   301,   312,   329,   340,   351,   362,
     397,   414,   437,   466,   495,   512,   523,   534,   545,   586,
     609,   650,   673,   708,   725,   736,   747,   758,   769,   780,
     791,   802,   813,   824,   836,   842,   853,   864,   887,   898,
     915,   932,   949,   966,   983,  1012,  1029,  1058,  1075,  1092,
    1103,  1114,  1125,  1136,  1165,  1176,  1199,  1222,  1245,  1268,
    1291,  1314,  1337,  1360,  1383,  1406,  1429,  1452,  1475,  1498,
    1533,  1562,  1585,  1608,  1619,  1636,  1659,  1676,  1687,  1698,
    1709,  1750,  1773,  1790,  1813,  1830,  1859,  1900,  1953,  1994,
    2023,  2046,  2069,  2086,  2103,  2115,  2121,  2132,  2149,  2160,
    2171,  2188,  2205,  2234,  2263,  2280,  2297,  2315,  2321,  2332,
    2361,  2390,  2413,  2436,  2453,  2470,  2481,  2498,  2515,  2532,
    2549,  2566,  2577,  2594,  2605,  2622,  2639,  2656,  2673,  2690,
    2707,  2718,  2735,  2752,  2769,  2786,  2803,  2820,  2837,  2854,
    2871,  2888,  2905,  2922,  2939,  2956,  2973,  2990,  3001,  3018,
    3035,  3052,  3069,  3086,  3103,  3120,  3137,  3154,  3171,  3188,
    3205,  3222,  3239,  3256,  3273,  3290,  3307,  3324,  3341,  3358,
    3375,  3392,  3409,  3426,  3443,  3460,  3477,  3494,  3511,  3528,
    3545,  3562,  3579,  3596,  3613,  3630,  3647,  3664,  3681,  3698,
    3715,  3732,  3749,  3760,  3771,  3782,  3793,  3804,  3815,  3826,
    3837,  3848,  3859,  3870,  3881,  3892,  3903,  3914,  3925,  3936,
    3947,  3958,  3969,  3980,  3991,  4002,  4013,  4024,  4035,  4046,
    4057,  4068,  4079,  4090,  4101,  4112,  4123,  4134,  4145,  4156,
    4167,  4178,  4189,  4200,  4211,  4222,  4233,  4244,  4255,  4266,
    4277,  4288,  4299,  4310,  4321,  4332,  4343,  4354,  4366,  4372,
    4383,  4394,  4405,  4416,  4427,  4438,  4449,  4460,  4489,  4518,
    4529,  4558,  4569,  4599,  4605,  4634,  4669,  4692,  4727,  4751,
    4757,  4768,  4779,  4796,  4837,  4848,  4872,  4878,  4889,  4918,
    4959,  4988,  4999,  5010,  5021,  5032,  5043,  5054,  5065,  5088,
    5096,  5102,  5119,  5130,  5153,  5176,  5193,  5216,  5227,  5250,
    5261,  5268,  5279,  5296,  5313,  5348,  5371,  5400,  5429,  5440,
    5451,  5480,  5503,  5520,  5543,  5554,  5565,  5576,  5599,  5616,
    5627,  5650,  5667,  5690,  5713,  5742,  5765,  5794,  5817,  5834,
    5845,  5856,  5873,  5884,  5901,  5912,  5929,  5970,  6005,  6046,
    6081,  6128,  6169,  6186,  6203,  6221,  6227,  6239,  6245,  6256,
    6267,  6285,  6291,  6314,  6331,  6354,  6371,  6394,  6405,  6412,
    6429,  6440,  6469,  6480,  6509,  6526,  6555,  6578,  6595,  6624,
    6647,  6658,  6681,  6688,  6699,  6722,  6740,  6746,  6758,  6764,
    6775,  6792,  6803,  6814,  6825,  6848,  6865,  6888,  6917,  6940,
    6957,  6974,  6985,  7014,  7037,  7066,  7101,  7136,  7147,  7158,
    7169,  7180,  7191,  7208,  7225,  7242,  7259,  7270,  7287,  7304,
    7321,  7332,  7349,  7366,  7383,  7400,  7411,  7428,  7439,  7450,
    7461,  7472,  7484,  7491,  7498,  7504,  7515,  7526,  7543,  7566,
    7577,  7588,  7599,  7610,  7645,  7656,  7663,  7680,  7697,  7714,
    7725,  7754,  7778,  7784,  7795,  7812,  7823,  7840,  7857,  7880,
    7891,  7908,  7943,  7978,  7989,  8042,  8077,  8094,  8105,  8116,
    8127,  8144,  8161,  8178,  8201,  8242,  8295,  8360,  8437,  8460,
    8489,  8500,  8511,  8528,  8563,  8586,  8621,  8638,  8668,  8674,
    8686,  8692,  8704,  8710,  8721,  8732,  8755,  8784,  8831,  8842,
    8865,  8882,  8899,  8928,  8940,  8946,  8957,  8968,  8991,  9002,
    9025,  9054,  9083,  9106,  9135,  9158,  9187,  9216,  9239,  9268,
    9291,  9302,  9319,  9330,  9347,  9358,  9381,  9392,  9415
};
#endif

#if YYDEBUG || YYERROR_VERBOSE || YYTOKEN_TABLE
/* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
   First, the terminals, then, starting at YYNTOKENS, nonterminals.  */
static const char *const yytname[] =
{
  "$end", "error", "$undefined", "IDENTIFIER", "TYPENAME", "SCSPEC",
  "STATIC", "TYPESPEC", "TYPE_QUAL", "CONSTANT", "STRING", "ELLIPSIS",
  "SIZEOF", "ENUM", "STRUCT", "UNION", "IF", "ELSE", "WHILE", "DO", "FOR",
  "SWITCH", "CASE", "DEFAULT", "BREAK", "CONTINUE", "RETURN", "GOTO",
  "ASM_KEYWORD", "TYPEOF", "ALIGNOF", "ATTRIBUTE", "EXTENSION", "LABEL",
  "REALPART", "IMAGPART", "VA_ARG", "CHOOSE_EXPR", "TYPES_COMPATIBLE_P",
  "PTR_VALUE", "PTR_BASE", "PTR_EXTENT", "FUNC_NAME", "','", "')'", "']'",
  "';'", "'}'", "'{'", "'~'", "'!'", "'='", "ASSIGN", "'?'", "':'", "OROR",
  "ANDAND", "'|'", "'^'", "'&'", "EQCOMPARE", "ARITHCOMPARE", "RSHIFT",
  "LSHIFT", "'+'", "'-'", "'*'", "'/'", "'%'", "MINUSMINUS", "PLUSPLUS",
  "UNARY", "HYPERUNARY", "'.'", "'('", "'['", "POINTSAT", "INTERFACE",
  "IMPLEMENTATION", "END", "SELECTOR", "DEFS", "ENCODE", "CLASSNAME",
  "PUBLIC", "PRIVATE", "PROTECTED", "PROTOCOL", "OBJECTNAME", "CLASS",
  "ALIAS", "AT_THROW", "AT_TRY", "AT_CATCH", "AT_FINALLY",
  "AT_SYNCHRONIZED", "OBJC_STRING", "$accept", "program", "extdefs",
  "extdef", "extdef_1", "datadef", "fndef", "identifier", "unop", "expr",
  "exprlist", "nonnull_exprlist", "unary_expr", "sizeof", "alignof",
  "typeof", "cast_expr", "expr_no_commas", "primary",
  "old_style_parm_decls", "old_style_parm_decls_1", "lineno_datadecl",
  "datadecls", "datadecl", "lineno_decl", "setspecs", "maybe_resetattrs",
  "decl", "declspecs_nosc_nots_nosa_noea", "declspecs_nosc_nots_nosa_ea",
  "declspecs_nosc_nots_sa_noea", "declspecs_nosc_nots_sa_ea",
  "declspecs_nosc_ts_nosa_noea", "declspecs_nosc_ts_nosa_ea",
  "declspecs_nosc_ts_sa_noea", "declspecs_nosc_ts_sa_ea",
  "declspecs_sc_nots_nosa_noea", "declspecs_sc_nots_nosa_ea",
  "declspecs_sc_nots_sa_noea", "declspecs_sc_nots_sa_ea",
  "declspecs_sc_ts_nosa_noea", "declspecs_sc_ts_nosa_ea",
  "declspecs_sc_ts_sa_noea", "declspecs_sc_ts_sa_ea", "declspecs_ts",
  "declspecs_nots", "declspecs_ts_nosa", "declspecs_nots_nosa",
  "declspecs_nosc_ts", "declspecs_nosc_nots", "declspecs_nosc",
  "declspecs", "maybe_type_quals_attrs", "typespec_nonattr",
  "typespec_attr", "typespec_reserved_nonattr", "typespec_reserved_attr",
  "typespec_nonreserved_nonattr", "initdecls", "notype_initdecls",
  "maybeasm", "initdcl", "notype_initdcl", "maybe_attribute", "attributes",
  "attribute", "attribute_list", "attrib", "any_word", "scspec", "init",
  "initlist_maybe_comma", "initlist1", "initelt", "initval",
  "designator_list", "designator", "nested_function",
  "notype_nested_function", "declarator", "after_type_declarator",
  "parm_declarator", "parm_declarator_starttypename",
  "parm_declarator_nostarttypename", "notype_declarator", "struct_head",
  "union_head", "enum_head", "structsp_attr", "structsp_nonattr",
  "maybecomma", "maybecomma_warn", "component_decl_list",
  "component_decl_list2", "component_decl", "components",
  "components_notype", "component_declarator",
  "component_notype_declarator", "enumlist", "enumerator", "typename",
  "absdcl", "absdcl_maybe_attribute", "absdcl1", "absdcl1_noea",
  "absdcl1_ea", "direct_absdcl1", "array_declarator", "stmts_and_decls",
  "lineno_stmt_decl_or_labels_ending_stmt",
  "lineno_stmt_decl_or_labels_ending_decl",
  "lineno_stmt_decl_or_labels_ending_label",
  "lineno_stmt_decl_or_labels_ending_error", "lineno_stmt_decl_or_labels",
  "errstmt", "pushlevel", "poplevel", "maybe_label_decls", "label_decls",
  "label_decl", "compstmt_or_error", "compstmt_start", "compstmt_nostart",
  "compstmt_contents_nonempty", "compstmt_primary_start", "compstmt",
  "simple_if", "if_prefix", "do_stmt_start", "save_location",
  "lineno_labeled_stmt", "c99_block_lineno_labeled_stmt", "lineno_stmt",
  "lineno_label", "select_or_iter_stmt", "for_init_stmt", "stmt",
  "exprstmt", "label", "maybe_type_qual", "xexpr", "asm_operands",
  "nonnull_asm_operands", "asm_operand", "asm_clobbers", "parmlist",
  "parmlist_1", "parmlist_2", "parms", "parm", "firstparm", "setspecs_fp",
  "parmlist_or_identifiers", "parmlist_or_identifiers_1", "identifiers",
  "identifiers_or_typenames", "extension", 0
};
#endif

# ifdef YYPRINT
/* YYTOKNUM[YYLEX-NUM] -- Internal token number corresponding to
   token YYLEX-NUM.  */
static const yytype_uint16 yytoknum[] =
{
       0,   256,   257,   258,   259,   260,   261,   262,   263,   264,
     265,   266,   267,   268,   269,   270,   271,   272,   273,   274,
     275,   276,   277,   278,   279,   280,   281,   282,   283,   284,
     285,   286,   287,   288,   289,   290,   291,   292,   293,   294,
     295,   296,   297,    44,    41,    93,    59,   125,   123,   126,
      33,    61,   298,    63,    58,   299,   300,   124,    94,    38,
     301,   302,   303,   304,    43,    45,    42,    47,    37,   305,
     306,   307,   308,    46,    40,    91,   309,   310,   311,   312,
     313,   314,   315,   316,   317,   318,   319,   320,   321,   322,
     323,   324,   325,   326,   327,   328,   329
};
# endif

/* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
static const yytype_uint8 yyr1[] =
{
       0,    97,    98,    98,    99,    99,   100,   101,   101,   101,
     101,   102,   102,   102,   102,   102,   102,   102,   103,   103,
     103,   103,   103,   103,   104,   104,   105,   105,   105,   105,
     105,   105,   105,   106,   107,   107,   108,   108,   109,   109,
     109,   109,   109,   109,   109,   109,   109,   109,   109,   110,
     111,   112,   113,   113,   114,   114,   114,   114,   114,   114,
     114,   114,   114,   114,   114,   114,   114,   114,   114,   114,
     114,   114,   114,   115,   115,   115,   115,   115,   115,   115,
     115,   115,   115,   115,   115,   115,   115,   115,   115,   115,
     115,   115,   115,   115,   116,   117,   117,   118,   119,   119,
     119,   119,   120,   120,   120,   120,   121,   122,   123,   124,
     124,   124,   124,   124,   124,   125,   125,   125,   126,   127,
     127,   128,   128,   129,   129,   129,   129,   129,   129,   129,
     130,   130,   130,   130,   130,   130,   131,   131,   131,   131,
     131,   131,   132,   132,   132,   132,   132,   133,   133,   133,
     133,   133,   133,   133,   134,   135,   135,   135,   135,   135,
     135,   136,   137,   137,   137,   137,   137,   137,   137,   137,
     137,   137,   138,   138,   138,   138,   138,   139,   139,   139,
     139,   139,   139,   139,   139,   139,   139,   140,   140,   140,
     140,   140,   141,   141,   141,   141,   141,   141,   141,   141,
     142,   142,   142,   142,   142,   142,   142,   142,   143,   143,
     143,   143,   144,   144,   144,   144,   145,   145,   145,   145,
     146,   146,   146,   146,   147,   147,   147,   147,   147,   147,
     147,   147,   148,   148,   148,   148,   148,   148,   148,   148,
     148,   148,   148,   148,   148,   148,   148,   148,   149,   149,
     150,   150,   151,   152,   152,   153,   154,   154,   154,   155,
     155,   156,   156,   157,   157,   158,   158,   159,   159,   160,
     160,   161,   161,   162,   163,   163,   164,   164,   164,   164,
     164,   165,   165,   165,   165,   166,   166,   167,   167,   167,
     168,   168,   169,   169,   170,   170,   170,   170,   171,   171,
     171,   172,   172,   173,   173,   173,   174,   175,   176,   176,
     177,   177,   177,   177,   177,   178,   178,   179,   179,   179,
     180,   180,   180,   180,   180,   181,   181,   181,   181,   181,
     182,   182,   183,   183,   184,   184,   185,   185,   185,   185,
     185,   185,   186,   186,   186,   187,   187,   188,   188,   189,
     189,   190,   190,   190,   191,   191,   191,   191,   191,   191,
     192,   192,   193,   193,   194,   194,   194,   195,   195,   195,
     196,   196,   196,   197,   197,   198,   199,   199,   200,   200,
     200,   201,   201,   202,   202,   203,   203,   204,   204,   204,
     204,   204,   205,   205,   205,   205,   205,   206,   206,   206,
     206,   207,   207,   207,   207,   207,   208,   208,   208,   208,
     209,   209,   209,   209,   209,   210,   210,   211,   211,   211,
     211,   212,   213,   214,   215,   215,   216,   216,   217,   218,
     218,   219,   220,   220,   221,   221,   222,   223,   224,   224,
     225,   226,   227,   228,   228,   229,   230,   231,   232,   232,
     232,   232,   232,   232,   232,   232,   233,   233,   234,   234,
     234,   234,   234,   234,   234,   234,   234,   234,   234,   234,
     234,   234,   235,   235,   236,   236,   236,   236,   237,   237,
     238,   238,   239,   239,   240,   240,   241,   241,   242,   242,
     243,   244,   244,   244,   245,   245,   245,   245,   246,   246,
     247,   247,   247,   247,   247,   248,   248,   248,   248,   248,
     249,   250,   251,   251,   252,   252,   253,   253,   254
};

/* YYR2[YYN] -- Number of symbols composing right hand side of rule YYN.  */
static const yytype_uint8 yyr2[] =
{
       0,     2,     0,     1,     1,     2,     1,     1,     1,     5,
       2,     3,     4,     4,     2,     2,     2,     1,     6,     4,
       6,     4,     5,     3,     1,     1,     1,     1,     1,     1,
       1,     1,     1,     1,     0,     1,     1,     3,     1,     2,
       2,     2,     2,     2,     4,     2,     4,     2,     2,     1,
       1,     1,     1,     4,     1,     3,     3,     3,     3,     3,
       3,     3,     3,     3,     3,     3,     3,     3,     3,     5,
       4,     3,     3,     1,     2,     3,     2,     1,     1,     1,
       6,     3,     3,     3,     3,     4,     6,     8,     6,     4,
       3,     3,     2,     2,     1,     0,     1,     2,     1,     1,
       2,     2,     4,     4,     2,     2,     2,     0,     1,     4,
       4,     3,     3,     2,     2,     1,     2,     2,     2,     2,
       2,     1,     2,     1,     2,     2,     2,     2,     2,     2,
       1,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     1,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     1,     1,     1,     1,     1,     1,     1,     1,
       1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
       1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
       1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
       1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
       1,     1,     1,     1,     1,     1,     1,     1,     0,     1,
       1,     1,     1,     1,     1,     1,     1,     4,     4,     1,
       4,     1,     4,     0,     4,     5,     3,     5,     3,     0,
       1,     1,     2,     6,     1,     3,     0,     1,     4,     6,
       4,     1,     1,     1,     1,     1,     1,     1,     3,     1,
       0,     2,     1,     3,     3,     2,     3,     1,     3,     1,
       1,     1,     2,     2,     5,     3,     4,     4,     1,     1,
       4,     3,     2,     3,     1,     1,     1,     3,     2,     1,
       3,     2,     3,     3,     4,     3,     4,     3,     2,     1,
       1,     2,     1,     2,     1,     2,     6,     5,     6,     5,
       7,     6,     2,     2,     2,     0,     1,     0,     1,     1,
       2,     0,     3,     2,     3,     2,     3,     1,     1,     2,
       1,     4,     1,     4,     2,     4,     3,     2,     4,     3,
       1,     3,     1,     1,     3,     2,     0,     1,     0,     1,
       2,     1,     1,     1,     3,     2,     3,     4,     3,     2,
       2,     1,     4,     3,     4,     5,     5,     1,     1,     1,
       1,     1,     2,     2,     2,     2,     1,     2,     2,     2,
       1,     2,     2,     2,     2,     1,     2,     1,     1,     1,
       1,     2,     0,     0,     0,     1,     1,     2,     3,     1,
       2,     1,     1,     5,     1,     1,     2,     2,     2,     2,
       4,     3,     0,     1,     2,     1,     2,     2,     3,     1,
       3,     5,     5,     2,     8,     5,     2,     1,     1,     1,
       2,     2,     2,     3,     6,     8,    10,    12,     3,     4,
       1,     1,     2,     5,     3,     5,     2,     4,     0,     1,
       0,     1,     0,     1,     1,     3,     4,     7,     1,     3,
       2,     2,     4,     2,     0,     1,     1,     3,     1,     3,
       4,     4,     3,     4,     3,     4,     4,     3,     4,     3,
       1,     2,     1,     2,     1,     3,     1,     3,     1
};

/* YYDEFACT[STATE-NAME] -- Default rule to reduce with in state
   STATE-NUM when YYTABLE doesn't specify something else to do.  Zero
   means the default is an error.  */
static const yytype_uint16 yydefact[] =
{
       0,     0,   256,   286,   285,   253,   115,   334,   330,   332,
       0,    51,     0,   518,    17,     0,     0,     4,     6,     8,
       7,     0,     0,   200,   201,   202,   203,   192,   193,   194,
     195,   204,   205,   206,   207,   196,   197,   198,   199,   107,
     107,     0,   123,   130,   250,   252,   251,   121,   271,   147,
       0,     0,     0,   255,   254,     0,    15,    16,   335,   331,
     333,     0,     0,     1,     5,     0,   329,   248,   269,     0,
     261,     0,   116,   128,   134,   118,   150,   117,   129,   135,
     151,   119,   140,   145,   122,   157,   120,   141,   146,   158,
     124,   126,   132,   131,   168,   125,   127,   133,   169,   136,
     138,   143,   142,   183,   137,   139,   144,   184,   148,   166,
     175,   154,   152,   149,   167,   176,   153,   155,   181,   190,
     161,   159,   156,   182,   191,   160,   162,   164,   173,   172,
     170,   163,   165,   174,   171,   177,   179,   188,   187,   185,
     178,   180,   189,   186,     0,     0,    14,   272,    24,    25,
     351,   342,   351,   343,     0,   344,    10,    73,    77,    78,
      49,    50,     0,     0,     0,     0,     0,    79,    31,    32,
       0,    26,    28,    27,     0,    30,    29,     0,     0,     0,
      33,    52,     0,     0,    54,    36,    38,     0,     0,   276,
       0,   228,   229,   230,   231,   224,   225,   226,   227,   376,
       0,   220,   221,   222,   223,   249,     0,     0,   270,   269,
      11,    23,     0,   269,   248,   442,    94,     0,   442,   269,
     328,    99,     0,   314,   248,   269,     0,   259,     0,   308,
     309,     0,     0,     0,     0,   351,     0,   351,   372,   373,
     347,   370,     0,    74,    76,    47,    48,     0,     0,     0,
      42,    39,     0,   436,     0,     0,    41,     0,     0,     0,
      43,     0,    45,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
      93,    92,     0,    34,     0,     0,     0,   432,   424,     0,
      40,   283,   284,   281,     0,   274,   277,   282,   257,   248,
     269,   375,   377,   382,   381,   383,   391,   258,   327,     0,
       0,   108,   421,     0,     0,   325,   248,   249,     0,     0,
       0,   101,   100,   268,    97,   212,   213,   208,   209,   214,
     215,   210,   211,   107,   107,     0,     0,   269,    13,    19,
     442,   269,   269,   312,    12,    21,   442,   269,   358,   353,
     220,   221,   222,   223,   216,   217,   218,   219,   107,   107,
     350,     0,     0,   269,     0,     0,   348,     0,   347,    75,
       0,     0,     0,    82,    81,     0,     9,    37,     0,     0,
      71,    72,     0,     0,    68,    67,    65,    66,    64,    63,
      62,    61,    60,    55,    56,    57,    58,    59,    90,     0,
      35,     0,    91,    84,     0,     0,   425,   426,    83,   276,
       0,    34,   385,     0,   390,   269,   389,   326,   262,   263,
       0,     0,   514,   495,   107,   107,   512,     0,   496,   498,
     511,     0,     0,     0,   393,     0,     0,     0,   431,    22,
     422,   429,     0,   104,     0,   105,     0,   313,     0,     0,
       0,   266,   311,     0,   337,   355,     0,   352,   359,   269,
     339,   269,   374,   371,   269,     0,     0,     0,     0,     0,
      53,    44,    46,    70,     0,    85,    89,   516,     0,   435,
     406,   434,   442,   442,   442,   442,     0,   415,     0,     0,
     401,   410,   427,   275,   273,    73,     0,   384,   386,     0,
     490,     0,   388,   264,   493,   510,   378,   378,   491,     0,
     269,     0,   513,     0,     0,   394,   392,   430,   437,   289,
       0,   287,   267,     0,   263,     0,   310,   260,    18,     0,
      20,     0,   269,   354,   360,     0,   269,   356,   362,   336,
     338,   341,   269,     0,     0,     0,   300,    73,     0,     0,
       0,     0,   299,     0,   345,   292,   297,     0,     0,    69,
       0,   428,   407,   402,   411,   408,   403,   412,     0,   404,
     413,   409,   405,   414,   416,   423,   256,     0,     0,   442,
       0,     0,     0,     0,     0,     0,     0,     0,   478,   470,
     442,     0,    38,   106,   107,   107,     0,   458,   449,     0,
       0,   459,   446,   471,   447,     0,     0,   278,   280,   387,
     319,   248,   269,   269,   315,   316,   269,   507,   379,   382,
     248,   269,   269,   509,   497,   200,   201,   202,   203,   192,
     193,   194,   195,   204,   205,   206,   207,   196,   197,   198,
     199,   107,   107,   499,     0,   515,   395,   396,     0,   102,
     103,   265,   269,     0,   364,   269,   269,     0,   367,   269,
     340,    86,     0,    88,     0,   303,     0,     0,    80,     0,
     291,     0,   302,   295,   517,   433,     0,     0,   445,     0,
     443,   442,   480,     0,     0,   476,   460,   461,   462,     0,
       0,     0,   479,     0,     0,   472,    34,     0,     0,   113,
       0,   439,   438,   453,     0,   114,     0,   385,     0,   505,
     269,   318,   269,   321,   506,   380,   385,     0,   508,   378,
     378,   492,   288,   366,   269,     0,   369,   269,     0,     0,
     298,     0,   305,   296,   293,   294,     0,     0,   441,   444,
     481,   457,   480,     0,     0,     0,   474,   463,     0,   468,
       0,   269,     0,     0,   111,     0,     0,   112,     0,   450,
     448,     0,   279,   322,   323,     0,   317,   320,   269,   269,
     502,   269,   504,   365,   361,   368,   363,    87,     0,   440,
     442,     0,   456,   442,     0,   469,     0,   477,    85,   109,
     442,   110,   442,     0,   324,   500,   501,   503,   304,   451,
     480,   455,   475,     0,   482,   473,     0,     0,   452,     0,
     464,     0,     0,     0,   483,   484,   306,   307,   442,     0,
       0,     0,   482,     0,   454,     0,     0,   465,     0,   485,
     486,     0,     0,     0,     0,   466,   488,     0,     0,     0,
       0,   487,   489,   467
};

/* YYDEFGOTO[NTERM-NUM].  */
static const yytype_int16 yydefgoto[] =
{
      -1,    15,    16,    17,    18,    19,    20,   551,   178,   254,
     399,   180,   181,   182,   183,    21,   184,   185,   186,   215,
     216,   217,   218,   324,   480,    22,   310,   593,   191,   192,
     193,   194,   195,   196,   197,   198,   329,   330,    33,    34,
     331,   332,    37,    38,    39,    40,   424,   425,   358,   205,
     199,    41,   206,    42,    43,    44,    45,    46,   226,    69,
     219,   227,    70,   311,   208,    48,   294,   295,   296,    49,
     522,   553,   554,   555,   556,   557,   558,   754,   757,   524,
     229,   613,   614,   615,   230,    50,    51,    52,    53,    54,
     670,   367,   233,   234,   360,   533,   537,   534,   538,   240,
     241,   200,   301,   617,   618,   303,   304,   305,   220,   481,
     482,   483,   484,   485,   486,   221,   288,   675,   405,   406,
     407,   439,   440,   289,   488,   187,   441,   598,   599,   600,
     568,   678,   679,   680,   681,   601,   742,   602,   603,   604,
     693,   743,   813,   814,   815,   837,   414,   500,   427,   428,
     643,   429,   506,   315,   430,   431,   478,   188
};

/* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
#define YYPACT_NINF -678
static const yytype_int16 yypact[] =
{
     999,   287,  -678,  -678,  -678,  -678,  -678,    29,    29,    29,
      17,  -678,    35,  -678,  -678,    87,  1678,  -678,  -678,  -678,
    -678,   112,   178,  1613,  1225,  2252,  1254,   467,   851,   931,
    1320,  2550,  1290,  2618,  1439,  1010,  1753,  1682,  1768,  -678,
    -678,    43,  -678,  -678,  -678,  -678,  -678,    29,  -678,  -678,
     186,   205,   210,  -678,  -678,  1138,  -678,  -678,    29,    29,
      29,  2866,   127,  -678,  -678,  2606,  -678,   161,    29,   141,
    -678,   912,  -678,  -678,  -678,    29,  -678,  -678,  -678,  -678,
    -678,  -678,  -678,  -678,    29,  -678,  -678,  -678,  -678,  -678,
    -678,  -678,  -678,    29,  -678,  -678,  -678,  -678,  -678,  -678,
    -678,  -678,    29,  -678,  -678,  -678,  -678,  -678,  -678,  -678,
    -678,    29,  -678,  -678,  -678,  -678,  -678,  -678,  -678,  -678,
      29,  -678,  -678,  -678,  -678,  -678,  -678,  -678,  -678,    29,
    -678,  -678,  -678,  -678,  -678,  -678,  -678,  -678,    29,  -678,
    -678,  -678,  -678,  -678,   212,   178,  -678,  -678,  -678,  -678,
    -678,    80,  -678,   157,   146,   169,  -678,   126,  -678,   172,
    -678,  -678,  2866,  2866,   158,   164,   193,  -678,  -678,  -678,
     362,  -678,  -678,  -678,  2866,  -678,  -678,  2240,  2866,   155,
     253,  -678,  2909,  2952,  -678,  3553,   320,  1952,  2866,  1168,
     257,  1704,   585,  3165,   873,   704,   434,  1067,   558,   270,
     264,   194,   339,   248,   345,  -678,   178,   178,    29,    29,
    -678,   282,   283,    29,   307,  -678,  -678,  1355,    65,    29,
    -678,  -678,  1372,  -678,   161,    29,   303,  -678,  3091,   296,
     363,   315,  1749,   330,  1039,  -678,   334,  -678,  -678,   348,
     342,  -678,   146,   404,  -678,  -678,  -678,  2866,  2866,  3348,
    -678,  -678,   376,  -678,   379,   381,  -678,   437,  2866,  2240,
    -678,  2240,  -678,  2866,  2866,  2649,  2866,  2866,  2866,  2866,
    2866,  2866,  2866,  2866,  2866,  2866,  2866,  2866,  2866,  2866,
    -678,  -678,   362,  2866,  2866,   362,   383,  -678,   460,   455,
    -678,  -678,  -678,  -678,   402,  -678,   335,  -678,  -678,   161,
      29,  -678,  -678,  -678,  -678,   394,  -678,  -678,   363,   215,
     178,  -678,  -678,   491,  3233,  -678,   161,   499,  2692,    76,
     282,  -678,  -678,   458,  -678,  3042,  2365,  1539,   481,  3305,
    2445,  1829,  1200,   482,   485,   212,   212,    29,  -678,   282,
    -678,    29,    29,  -678,  -678,   282,  -678,    29,  -678,  -678,
    1704,   585,  3165,   873,   704,   434,  1067,   558,  -678,   445,
     487,   651,   490,    29,   505,  2866,   362,   510,   342,  -678,
    3329,  3355,   524,  -678,  -678,  2735,  -678,  3553,   526,   536,
    3553,  3553,  2866,   534,  3582,  3098,  3222,  1837,   773,  1048,
     367,   634,   634,   411,   411,  -678,  -678,  -678,  -678,   557,
     253,   566,  -678,  -678,   362,  2024,   460,  -678,  -678,  1168,
     559,  2995,   270,  1877,  -678,    29,  -678,  -678,  -678,    88,
     568,   573,  -678,  -678,  -678,  -678,  -678,   579,   332,  -678,
    -678,   477,  2866,  2866,  -678,  2778,  3445,   577,  -678,  -678,
     580,  -678,  2283,  -678,   212,  -678,   178,   296,   229,   212,
      76,   583,  -678,    76,  -678,   156,   152,  -678,  -678,    29,
    -678,    29,  3553,  -678,    29,   588,  3348,  2866,  3348,  1243,
    -678,   582,   582,  3569,  2866,  -678,  -678,  -678,   340,   282,
    -678,  -678,    82,   118,   125,   151,   635,  -678,   592,  2398,
    -678,  -678,  -678,  -678,  -678,    54,   598,  -678,  -678,   600,
    -678,  3250,  -678,  -678,  -678,  -678,   108,   122,  -678,  1905,
      29,   645,  -678,  3469,  3493,  -678,  -678,  -678,  -678,  -678,
    1243,  3553,  -678,   360,   621,   365,  -678,  -678,  -678,  2283,
    -678,  2866,   262,   607,  -678,  2866,   223,   608,  -678,  -678,
    -678,  -678,    29,   610,  3381,   617,  -678,    42,  1243,   362,
    2866,   609,  3553,   622,   625,  -678,  -678,   249,  1601,  3569,
     362,  -678,  -678,  -678,  -678,  -678,  -678,  -678,  2466,  -678,
    -678,  -678,  -678,  -678,  -678,  -678,   618,   599,   602,  -678,
     604,   612,  2866,   620,   646,   647,  2821,   104,   696,  -678,
    -678,   661,   919,  -678,  -678,  -678,   664,  -678,   703,  2096,
      61,  -678,  -678,  -678,  -678,  2538,  2866,  -678,  -678,  -678,
    -678,   161,    29,    29,   476,   500,   189,  -678,  -678,    29,
     161,    29,   189,  -678,  -678,  3042,  2365,  3317,  3103,  1539,
     481,  1932,  1504,  3305,  2445,  3336,  3446,  1829,  1200,  2071,
    1573,  -678,  -678,  -678,  3250,  -678,  -678,  -678,   676,  -678,
    -678,  -678,  3240,  2866,  -678,    29,  3240,  2866,  -678,    29,
    -678,  -678,  2866,  -678,   677,  -678,   399,  2326,  -678,  1526,
    -678,  2326,  -678,  -678,  -678,  -678,  2866,  2866,  -678,   707,
    -678,  -678,  2538,  2866,  3023,  -678,  -678,  -678,  -678,   683,
    2866,   686,  -678,   662,   684,  -678,  2866,   212,   178,  -678,
    2168,  -678,  -678,  -678,  2866,  -678,   539,   108,  1476,  -678,
      29,  -678,    29,  -678,  -678,    29,   122,  1851,  -678,   108,
     122,  -678,  -678,  -678,  3240,   156,  -678,  3240,   152,  3421,
    -678,  2866,  -678,  -678,  -678,  -678,   697,   698,  -678,  -678,
    -678,  -678,  2866,   705,   706,  2866,  -678,  -678,   715,  -678,
    2866,    29,   708,   370,  -678,  3139,   371,  -678,  1802,  -678,
    -678,   709,  -678,   476,   500,   244,  -678,  -678,    29,   189,
    -678,   189,  -678,  -678,  -678,  -678,  -678,  -678,  3517,  -678,
    -678,   716,  -678,  -678,  3535,  -678,   175,  -678,  3183,  -678,
    -678,  -678,  -678,   720,  -678,  -678,  -678,  -678,  -678,  -678,
    2866,  -678,  -678,   721,    47,  -678,   577,   577,  -678,   724,
    -678,   675,   362,   240,   728,  -678,  -678,  -678,  -678,  2866,
     727,   729,    47,    47,  -678,   730,   766,  -678,   276,  -678,
    -678,   713,   734,   778,  2866,  -678,  -678,   551,   745,   781,
     746,  -678,  -678,  -678
};

/* YYPGOTO[NTERM-NUM].  */
static const yytype_int16 yypgoto[] =
{
    -678,  -678,  -678,    59,  -678,  -678,  -678,   -49,  -678,   -61,
    -394,  -274,   414,  -678,  -678,  -678,  -129,  1074,  -478,  -216,
    -678,   576,  -678,  -678,  -202,    46,  -296,  -567,    13,    26,
      27,    51,    23,    40,     8,    21,    14,    55,   288,   292,
      72,   101,   294,   298,  -410,  -318,   574,   587,  -678,  -161,
    -678,  -439,  -196,   522,   553,   863,   894,  -678,  -398,  -139,
    -193,   356,   501,   143,   777,    83,  -678,   422,  -678,   792,
     284,  -375,  -678,   174,  -510,  -678,   289,  -678,  -678,  -134,
     273,   130,   138,  -102,    62,  -678,  -678,  -678,  -678,  -678,
    -678,   486,   -91,  -678,   489,  -678,  -678,   135,   133,   626,
     496,  -157,  -678,  -449,  -174,  -368,  -393,  -678,   750,  -678,
    -678,  -678,  -678,  -678,  -678,  -181,  -678,  -678,  -678,  -678,
     457,  -158,  -678,   429,  -678,  -678,  -415,  -678,  -678,  -678,
     -64,   190,  -552,  -351,  -305,  -678,  -678,    96,  -678,  -678,
    -678,  -677,    63,  -678,    53,  -678,   474,  -309,  -678,  -678,
    -678,  -678,   470,  -310,  -678,  -678,  -678,    15
};

/* YYTABLE[YYPACT[STATE-NUM]].  What to do in state STATE-NUM.  If
   positive, shift that token.  If negative, reduce the rule which
   number is the opposite.  If zero, do what YYDEFACT says.
   If YYTABLE_NINF, syntax error.  */
#define YYTABLE_NINF -495
static const yytype_int16 yytable[] =
{
     179,   151,   153,   155,   190,   426,   231,   222,    29,   400,
     228,   592,   340,    23,    31,    55,   346,   496,   318,   498,
     255,    30,   517,    27,    29,   302,    24,    25,   335,    23,
      31,    55,   452,   245,   246,   341,   321,    30,   705,    27,
      28,   449,    24,    25,   497,   251,   523,   702,   673,   256,
     596,    26,   243,   317,   490,    32,    28,   811,   623,   290,
      12,   236,   703,    29,   243,   781,   -96,    26,    23,    31,
      55,    32,    35,   359,   597,    64,    30,   437,    27,   594,
     201,    24,    25,  -417,    71,   144,   145,    63,    35,   146,
     592,    61,   372,   202,   203,    28,   -24,   606,   607,   641,
     491,    36,   378,   412,   379,   239,    26,   148,   149,    62,
      32,    66,   610,   -96,   156,   741,   212,    36,   204,  -418,
     432,   250,   812,   809,   438,    66,  -419,    35,   235,  -397,
     147,   563,   566,   569,   572,   704,   243,   400,   619,   619,
     293,   147,   147,   147,   362,   648,   364,   238,   760,   148,
     149,   319,  -420,   597,   222,    66,    36,   733,   147,    66,
     223,   735,   213,   214,   222,  -398,   596,   147,   222,     6,
     690,   595,  -399,   664,   611,   244,   147,   564,   567,   570,
     573,    66,   612,   214,   209,   147,    65,   210,   620,   148,
     149,   642,    12,   239,   147,   594,   621,   214,  -400,   257,
     359,   189,    72,   147,   383,   237,   535,   232,   148,   149,
     531,   207,   147,   148,   149,    66,   223,   242,    67,   803,
      12,   147,   224,   401,   487,    12,    68,   201,   799,   804,
     225,   801,   247,   398,   150,   325,   402,   201,   248,   499,
     202,   203,   356,   596,    67,   327,   470,   350,   326,   361,
     202,   203,    68,   152,    12,   357,    81,   354,   154,   417,
     351,   352,   328,   213,   214,   204,   824,   249,   308,   309,
     770,   772,   594,   526,   355,   204,   450,   657,   224,    12,
     562,   565,   453,   571,   821,   353,   225,   595,   794,   213,
     214,   147,   528,    12,   822,   530,   258,   213,   214,   753,
     671,   298,   752,   342,   214,   574,   251,   525,   307,   543,
     592,   545,   201,   316,   498,     6,   653,   239,   712,   214,
     832,   532,   549,   498,   550,   202,   203,   325,   312,   201,
     833,   341,   706,    56,    57,   721,   299,   327,    12,   497,
     326,   489,   202,   203,   300,   214,   337,    77,   497,   338,
     204,   619,   619,    86,   328,   477,   314,   313,   209,   725,
     293,   344,   323,   728,   595,   148,   149,   204,   336,   356,
     342,   214,   419,   597,   350,   509,   361,   347,   510,   444,
     446,   363,   357,   560,   354,   366,   561,   351,   352,   280,
     281,   816,   817,   282,   283,   284,   285,   308,   309,   365,
     766,   355,   767,   337,   455,   456,   649,   369,   209,   411,
     731,   650,   353,   337,   209,   707,   789,   791,   489,   489,
     373,   489,   400,   374,   716,   375,   325,   403,   591,   273,
     274,   275,   276,   277,   278,   279,   327,   213,   214,   326,
     590,     5,    95,   413,   732,   409,   410,     7,     8,     9,
     263,   264,   265,   328,   266,   267,   268,   269,   270,   271,
     272,   273,   274,   275,   276,   277,   278,   279,   415,   214,
     505,   505,     3,     4,     5,    90,   290,   277,   278,   279,
       7,     8,     9,   376,   451,   314,     3,     4,     5,    95,
     454,  -357,  -357,   404,     7,     8,     9,    29,    12,   408,
     665,   420,    23,    31,   605,   433,   460,   591,   419,   442,
      30,   674,    27,  -236,   325,    24,    25,   631,   536,   590,
     511,   512,   625,   633,   327,   689,   694,   326,   443,    28,
     632,   445,   629,   457,   499,   626,   627,   459,   691,   790,
      26,   328,   792,   499,    32,    73,    78,    82,    87,   630,
     710,   214,   461,   109,   114,   118,   123,   464,   501,   756,
     628,    35,   341,   755,   634,     5,   104,   468,   616,   622,
     471,     7,     8,     9,   712,   214,    74,    79,    83,    88,
     472,   637,   258,   762,   110,   115,   119,   124,   474,     2,
      36,   532,     5,    77,   839,   840,   260,   262,     7,     8,
       9,   475,   539,   494,   540,   764,   765,   541,   447,   448,
     638,   476,   503,    29,    11,   736,   737,   504,    23,    31,
     605,   740,   744,   508,   201,   438,    30,   287,    27,   748,
     469,    24,    25,   201,   529,   542,   320,   202,   203,   575,
     697,   698,   608,   761,   609,    28,   202,   203,   645,   212,
     655,   659,   348,   644,   661,     2,    26,   325,     5,     6,
      32,   663,   204,   667,     7,     8,     9,   327,   669,   668,
     326,   204,   -25,   676,   685,   654,   677,    35,   682,   658,
      11,   740,    12,    13,   328,   660,   683,   719,   720,   786,
      29,   222,   686,   687,   222,    23,    31,   605,   275,   276,
     277,   278,   279,    30,   692,    27,    36,   695,    24,    25,
     699,     5,    90,    73,    78,    82,    87,     7,     8,     9,
     700,   325,    28,   722,   730,   738,   806,   591,   807,   747,
     325,   327,   749,    26,   326,    12,   750,    32,   751,   740,
     327,   779,   780,   326,    74,    79,    83,    88,   328,   819,
     783,   782,   788,   793,    35,   708,   709,   328,   825,   714,
     758,   785,   800,   820,   717,   718,   808,   810,   818,   308,
     309,   823,   826,   838,   830,   827,   831,    47,   308,   309,
     835,   769,   771,    36,    58,    59,    60,   834,   836,   841,
     536,   842,   843,    47,   322,   723,   333,   635,   147,   726,
      75,   636,    84,   639,    93,   527,   102,   640,   111,   334,
     120,   418,   129,   651,   138,    76,    80,    85,    89,    94,
      98,   103,   107,   112,   116,   121,   125,   130,   134,   139,
     143,   493,    47,   271,   272,   273,   274,   275,   276,   277,
     278,   279,    47,   734,    47,   763,   672,    73,    78,   768,
     458,   109,   114,   314,   465,   314,     3,     4,     5,    95,
     774,   776,   463,   492,     7,     8,     9,   773,   368,   518,
     775,   739,    73,    78,    82,    87,   829,     2,    74,    79,
       5,    86,   110,   115,   805,   828,     7,     8,     9,   502,
      91,    96,   100,   105,   787,   507,     0,  -237,   127,   132,
     136,   141,    11,    74,    79,    83,    88,     0,     0,     0,
       0,   795,   796,   211,   797,     0,  -442,  -442,  -442,  -442,
    -442,    92,    97,   101,   106,  -442,  -442,  -442,     0,   128,
     133,   137,   142,     0,     0,     0,     3,     4,     5,    99,
     212,  -442,     0,  -263,     7,     8,     9,     0,     0,   306,
       0,     0,     0,     0,    47,  -263,     0,     0,  -263,     0,
     -95,     0,    12,  -263,     0,     0,     0,     0,    75,     0,
      84,     0,    93,     0,   102,     0,     0,  -238,    75,   343,
      84,   297,     0,     0,     0,     0,   213,   214,   280,   281,
       0,    47,   282,   696,   284,   285,     0,     0,     0,    -2,
       1,    47,  -107,     2,     3,     4,     5,     6,     0,     0,
       0,    47,     7,     8,     9,     3,     4,     5,   126,     0,
       0,     0,     0,     7,     8,     9,    47,    10,    11,     0,
      12,    13,     0,     0,     0,     0,    47,     0,    47,     0,
     348,    12,     0,     2,     0,    14,     5,     6,     0,     0,
       0,     0,     7,     8,     9,   416,  -244,     0,    91,    96,
     100,   105,     0,     0,     0,  -107,     0,     0,    11,     0,
      12,    13,     0,  -107,     5,    99,    47,     0,     0,     0,
       7,     8,     9,     0,     0,   349,  -349,     0,     0,    92,
      97,   101,   106,    47,     0,     0,     0,     0,    12,     0,
       0,     0,    75,     0,    93,     0,   111,     0,   129,   272,
     273,   274,   275,   276,   277,   278,   279,    76,    80,    94,
      98,   112,   116,   130,   134,     0,     0,    75,     0,    84,
       0,    93,     0,   102,     0,     0,     0,     0,    47,     1,
       0,  -107,     2,     3,     4,     5,     6,    73,    78,    82,
      87,     7,     8,     9,     0,   109,   114,   118,   123,     0,
       0,     0,   306,   306,     0,     0,    10,    11,     0,    12,
      13,   148,   149,     3,     4,   291,   292,     0,    74,    79,
      83,    88,     0,     0,    14,     0,   110,   115,   119,   124,
      91,    96,     0,     0,   127,   132,     0,   343,   343,     0,
       0,   297,     0,     0,  -107,     3,     4,     5,   131,     0,
       0,     0,  -107,     7,     8,     9,     0,    91,    96,   100,
     105,    92,    97,     0,     0,   128,   133,     0,     0,     2,
       3,     4,     5,    77,     0,     0,     0,     0,     7,     8,
       9,     0,     0,    47,   546,    47,   547,   149,    92,    97,
     101,   106,   158,   159,    11,   160,   306,   306,     2,     3,
       4,     5,    86,     0,     0,     0,    47,     7,     8,     9,
       0,  -233,     0,   161,     0,    13,     0,   162,   163,   164,
     165,   166,     0,    11,     0,   167,    47,     0,     0,     0,
    -290,   548,   168,   169,     2,     3,     4,     5,   113,   170,
    -235,     0,   171,     7,     8,     9,     0,   172,   173,   174,
       0,     0,   175,   176,     0,     0,   549,   177,   550,    11,
       0,   370,   371,     0,     0,     3,     4,     5,   104,     0,
       0,     0,   377,     7,     8,     9,  -241,   380,   381,     0,
     384,   385,   386,   387,   388,   389,   390,   391,   392,   393,
     394,   395,   396,   397,     0,     0,   320,     0,     0,   -98,
     -98,   -98,   -98,   -98,   711,   713,  -239,     0,   -98,   -98,
     -98,     0,     0,     0,     0,     0,     2,     3,     4,     5,
       6,     0,    47,     0,   -98,     7,     8,     9,    47,     0,
       0,     0,   436,     0,     0,     0,   715,    47,     0,     0,
       0,    11,    75,   -98,    84,     0,    93,     0,   102,     0,
     111,     0,   120,     0,   129,     0,   138,    76,    80,    85,
      89,    94,    98,   103,   107,   112,   116,   121,   125,   130,
     134,   139,   143,     0,     0,     0,     0,     0,     0,   462,
       0,     0,     0,     2,     3,     4,     5,   122,     0,     0,
       0,     0,     7,     8,     9,     0,   473,   306,   306,    47,
       0,     0,     0,     0,     0,     0,   306,   306,    11,   306,
     306,     0,     0,     0,     0,     0,     0,   421,     0,    66,
       2,     3,     4,     5,     6,  -243,     0,   423,     0,     7,
       8,     9,    91,    96,   100,   105,     0,     0,     0,     0,
     127,   132,   136,   141,     0,    11,   513,   514,     0,     3,
       4,     5,   104,   711,   713,   713,   521,     7,     8,     9,
    -494,     0,     0,    92,    97,   101,   106,   546,     0,   547,
     149,   128,   133,   137,   142,   158,   159,     0,   160,     0,
       0,   544,   611,   552,     3,     4,     5,    90,   559,     0,
     612,   214,     7,     8,     9,     0,   161,     0,    13,     0,
     162,   163,   164,   165,   166,     0,     0,     0,   167,     0,
      12,     0,     0,  -346,   548,   168,   169,     0,     3,     4,
       5,   140,   170,     0,     0,   171,     7,     8,     9,     0,
     172,   173,   174,     0,   552,   175,   176,     0,     0,   549,
     177,   550,   546,   521,   157,   652,     0,     0,     0,   656,
     158,   159,     0,   160,     0,     0,     0,     2,     3,     4,
       5,    72,   552,     0,   666,     0,     7,     8,     9,     0,
       0,   161,   552,    13,     0,   162,   163,   164,   165,   166,
       0,     0,    11,   167,    12,     0,     0,     0,     0,   548,
     168,   169,  -301,     0,     0,     0,   684,   170,     0,  -232,
     171,     0,     0,     0,     0,   172,   173,   174,     0,     0,
     175,   176,     0,     0,  -301,   177,  -301,     0,    -3,     1,
       0,  -107,     2,     3,     4,     5,     6,     3,     4,     5,
     135,     7,     8,     9,     0,     7,     8,     9,     0,     0,
       0,     0,     0,     0,     0,     0,    10,    11,     2,    12,
      13,     5,    72,    12,     0,     0,     0,     7,     8,     9,
       0,     0,     0,     0,    14,     0,     0,   724,  -246,     0,
       0,   727,     0,    11,     0,    12,   729,     0,     0,     0,
       0,   552,     0,   552,  -107,   552,     0,     0,     0,     0,
     345,     0,  -107,  -442,  -442,  -442,  -442,  -442,     3,     4,
       5,   131,  -442,  -442,  -442,     0,     7,     8,     9,     0,
       0,     0,     0,     3,     4,     5,   140,   212,  -442,     0,
    -263,     7,     8,     9,     0,     0,     0,     0,     0,     0,
       0,     0,  -263,     0,     0,  -263,     0,   -95,     0,  -245,
    -263,     0,     0,   320,     0,   778,  -442,  -442,  -442,  -442,
    -442,     0,     0,     0,  -247,  -442,  -442,  -442,     0,   784,
       0,     0,     0,   213,   214,     0,     0,     0,     0,     0,
     212,  -442,     0,  -263,     3,     4,     5,   126,     0,     0,
       0,     0,     7,     8,     9,  -263,     0,     0,  -263,     0,
     -95,     0,   421,  -263,    66,     2,     3,     4,     5,     6,
      12,     0,   423,     0,     7,     8,     9,     0,     0,     0,
       0,     0,     0,     0,     0,     0,   213,   214,   421,     0,
      11,     2,     3,     4,     5,     6,     0,     0,   423,     0,
       7,     8,     9,     0,     0,  -494,   270,   271,   272,   273,
     274,   275,   276,   277,   278,   279,    11,     0,     0,     2,
       3,     4,     5,     6,     0,     0,   624,   620,     7,     8,
       9,  -494,     0,     0,     0,   621,   214,     0,     0,     0,
       0,     0,     0,     0,    11,     0,    12,     3,     4,     5,
      99,     0,     0,   299,     0,     7,     8,     9,     0,     0,
       0,   300,   214,   286,     0,  -422,  -422,  -422,  -422,  -422,
    -422,  -422,  -422,    12,  -422,  -422,  -422,  -422,  -422,     0,
    -422,  -422,  -422,  -422,  -422,  -422,  -422,  -422,  -422,  -422,
    -422,  -422,  -422,  -422,  -422,  -422,  -422,  -422,  -422,  -422,
    -422,     0,     0,     0,  -422,     0,     0,     0,  -422,   287,
    -422,  -422,  -422,     0,     0,     0,     0,     0,  -422,     0,
       0,  -422,     0,     0,     0,     0,  -422,  -422,  -422,     0,
       0,  -422,  -422,     0,     0,   479,  -422,  -442,  -442,  -442,
    -442,  -442,  -442,  -442,  -442,     0,  -442,  -442,  -442,  -442,
    -442,     0,  -442,  -442,  -442,  -442,  -442,  -442,  -442,  -442,
    -442,  -442,  -442,  -442,  -442,  -442,  -442,     0,  -442,  -442,
    -442,  -442,  -442,     0,     0,     0,  -442,     0,     0,     0,
    -442,     0,  -442,  -442,  -442,     0,     3,     4,     5,   135,
    -442,     0,     0,  -442,     7,     8,     9,     0,  -442,  -442,
    -442,     0,     0,  -442,  -442,     0,     0,   701,  -442,  -442,
    -442,     0,    12,     0,     0,  -442,  -442,     0,  -442,     0,
       0,     0,  -442,     0,  -442,  -442,  -442,  -442,  -442,  -442,
    -442,  -442,  -442,  -442,  -442,     0,  -442,     0,  -442,     0,
    -442,  -442,  -442,  -442,  -442,     0,     0,     0,  -442,     0,
       0,     0,  -442,     0,  -442,  -442,  -442,     0,     0,     0,
       0,     0,  -442,     0,     0,  -442,     0,     0,     0,     0,
    -442,  -442,  -442,     0,     0,  -442,  -442,     0,     0,   759,
    -442,  -442,  -442,     0,     0,     0,     0,  -442,  -442,     0,
    -442,     0,     0,     0,  -442,     0,  -442,  -442,  -442,  -442,
    -442,  -442,  -442,  -442,  -442,  -442,  -442,     0,  -442,     0,
    -442,     0,  -442,  -442,  -442,  -442,  -442,     0,     0,     0,
    -442,     0,     0,     0,  -442,     0,  -442,  -442,  -442,     0,
       0,     0,     0,     0,  -442,     0,     0,  -442,     0,     0,
       0,     0,  -442,  -442,  -442,     0,     0,  -442,  -442,     0,
       0,   252,  -442,   157,     2,     0,     0,     5,     6,   158,
     159,     0,   160,     7,     8,     9,     2,     3,     4,     5,
      81,     0,     0,     0,     0,     7,     8,     9,     0,    11,
     161,    12,    13,     0,   162,   163,   164,   165,   166,     0,
       0,    11,   167,    12,   519,     0,   157,     0,   253,   168,
     169,     0,   158,   159,     0,   160,   170,     0,  -234,   171,
       0,     0,     0,     0,   172,   173,   174,     0,     0,   175,
     176,     0,     0,   161,   177,    13,     0,   162,   163,   164,
     165,   166,     0,     0,     0,   167,     0,   546,     0,   157,
       0,   520,   168,   169,     0,   158,   159,     0,   160,   170,
       0,     0,   171,     0,     0,     0,     0,   172,   173,   174,
       0,     0,   175,   176,     0,     0,   161,   177,    13,     0,
     162,   163,   164,   165,   166,     0,     0,     0,   167,     2,
       3,     4,     5,    77,   548,   168,   169,     0,     7,     8,
       9,     0,   170,     0,     0,   171,     0,     0,     0,     0,
     172,   173,   174,     0,    11,   175,   176,     0,     0,     0,
     177,   547,   576,     3,     4,     5,     6,   158,   159,     0,
     160,     7,     8,     9,   577,     0,   578,   579,   580,   581,
     582,   583,   584,   585,   586,   587,   588,    11,   161,    12,
      13,     0,   162,   163,   164,   165,   166,     0,     0,     0,
     167,     0,     0,     0,   589,     0,   438,   168,   169,     2,
       3,     4,     5,   113,   170,     0,     0,   171,     7,     8,
       9,     0,   172,   173,   174,     0,     0,   175,   176,   547,
     149,     0,   177,     0,    11,   158,   159,     0,   160,     0,
       0,     0,   577,     0,   578,   579,   580,   581,   582,   583,
     584,   585,   586,   587,   588,     0,   161,     0,    13,     0,
     162,   163,   164,   165,   166,     0,     0,     0,   167,     0,
       0,     0,   589,     0,   438,   168,   169,     0,     0,     0,
       0,     0,   170,     0,     0,   171,     0,     0,     0,     0,
     172,   173,   174,     0,     0,   175,   176,     0,     0,     0,
     177,   157,     2,     3,     4,     5,     6,   158,   159,     0,
     160,     7,     8,     9,     2,     3,     4,     5,   108,     0,
       0,     0,     0,     7,     8,     9,     0,    11,   161,    12,
      13,     0,   162,   163,   164,   165,   166,     0,     0,    11,
     167,    12,     0,     0,     0,     0,     0,   168,   169,     0,
       0,     0,     0,     0,   170,     0,  -240,   171,     0,     0,
       0,     0,   172,   173,   174,     0,     0,   175,   176,   157,
       2,     0,   177,     5,     6,   158,   159,     0,   160,     7,
       8,     9,     2,     3,     4,     5,   117,     0,     0,     0,
       0,     7,     8,     9,     0,    11,   161,    12,    13,     0,
     162,   163,   164,   165,   166,     0,     0,    11,   167,    12,
       0,     0,   157,     0,     0,   168,   169,     0,   158,   159,
       0,   160,   170,     0,  -242,   171,     0,     0,     0,     0,
     172,   173,   174,     0,     0,   175,   176,     0,     0,   161,
     177,    13,     0,   162,   163,   164,   165,   166,     0,     0,
       0,   167,     0,     0,     0,   157,     0,     0,   168,   169,
       0,   158,   159,   382,   160,   170,     0,     0,   171,     0,
       0,     0,     0,   172,   173,   174,     0,     0,   175,   176,
       0,     0,   161,   177,    13,     0,   162,   163,   164,   165,
     166,     0,     0,     0,   167,     0,     0,   434,   157,     0,
       0,   168,   169,     0,   158,   159,     0,   160,   170,     0,
       0,   171,     0,     0,     0,     0,   172,   173,   435,     0,
       0,   175,   176,     0,     0,   161,   177,    13,     0,   162,
     163,   164,   165,   166,     0,     0,     0,   167,     0,     0,
       0,   157,     0,   469,   168,   169,     0,   158,   159,     0,
     160,   170,     0,     0,   171,     0,     0,     0,     0,   172,
     173,   174,     0,     0,   175,   176,     0,     0,   161,   177,
      13,     0,   162,   163,   164,   165,   166,     0,     0,     0,
     167,     0,     0,   515,   157,     0,     0,   168,   169,     0,
     158,   159,     0,   160,   170,     0,     0,   171,     0,     0,
       0,     0,   172,   173,   174,     0,     0,   175,   176,     0,
       0,   161,   177,    13,     0,   162,   163,   164,   165,   166,
       0,     0,     0,   167,     0,     0,     0,   688,     0,   157,
     168,   169,     0,     0,     0,   158,   159,   170,   160,     0,
     171,     0,     0,     0,     0,   172,   173,   174,     0,     0,
     175,   176,     0,     0,     0,   177,   161,     0,    13,     0,
     162,   163,   164,   165,   166,     0,     0,     0,   167,     0,
       0,     0,   157,     0,     0,   168,   169,     0,   158,   159,
       0,   160,   170,     0,     0,   171,     0,     0,     0,     0,
     172,   173,   174,     0,     0,   175,   176,     0,     0,   161,
     177,    13,     0,   162,   163,   164,   165,   166,     0,     0,
       0,   167,     0,     0,     0,   157,     0,     0,   168,   169,
       0,   158,   159,     0,   160,   170,     0,     0,   171,     0,
       0,     0,     0,   172,   173,   174,     0,     0,   175,   176,
       0,     0,   161,   259,    13,     0,   162,   163,   164,   165,
     166,     0,     0,     0,   167,     0,     0,     0,   495,     0,
       0,   168,   169,     0,   158,   159,     0,   160,   170,     0,
       0,   171,     0,     0,     0,     0,   172,   173,   174,     0,
       0,   175,   176,     0,     0,   161,   261,    13,     0,   162,
     163,   164,   165,   166,   745,     0,     0,   167,     0,     0,
       0,     0,     0,     0,   168,   169,     2,     3,     4,     5,
      72,   170,     0,     0,   171,     7,     8,     9,     0,   172,
     173,   174,     0,     0,   175,   176,     0,     0,     0,   177,
       0,    11,     0,    12,   263,   264,   265,   746,   266,   267,
     268,   269,   270,   271,   272,   273,   274,   275,   276,   277,
     278,   279,   339,     0,     0,  -442,  -442,  -442,  -442,  -442,
       0,     0,     0,     0,  -442,  -442,  -442,     2,     3,     4,
       5,    86,     0,     0,     0,     0,     7,     8,     9,   212,
    -442,     0,  -263,     0,     0,     0,     0,     0,     0,     0,
       0,     0,    11,     0,  -263,     0,     0,  -263,     0,   -95,
     320,     0,  -263,  -442,  -442,  -442,  -442,  -442,     0,     0,
       0,     0,  -442,  -442,  -442,   268,   269,   270,   271,   272,
     273,   274,   275,   276,   277,   278,   279,   212,  -442,     2,
    -263,     0,     5,    81,     0,     0,     0,     0,     7,     8,
       9,     0,  -263,     0,     0,  -263,   157,   -95,     0,     0,
    -263,     0,   158,   159,    11,   160,    12,     0,     0,   577,
       0,   578,   579,   580,   581,     0,     0,   584,   585,   586,
     587,   588,     0,   161,     0,    13,     0,   162,   163,   164,
     165,   166,     0,     0,     0,   167,     0,     0,     0,   589,
       0,   438,   168,   169,   421,     0,   422,     2,     3,     4,
       5,     6,     0,     0,   423,     0,     7,     8,     9,     0,
       0,   421,     0,     0,     2,     3,     4,     5,     6,     0,
       0,   423,    11,     7,     8,     9,     0,     0,     0,     0,
       0,    12,     0,     0,     0,     0,     0,  -494,     0,    11,
     269,   270,   271,   272,   273,   274,   275,   276,   277,   278,
     279,   263,   264,   265,  -494,   266,   267,   268,   269,   270,
     271,   272,   273,   274,   275,   276,   277,   278,   279,     2,
       3,     4,     5,   108,     0,     0,     0,     0,     7,     8,
       9,     2,     3,     4,     5,    81,     0,     0,     0,     0,
       7,     8,     9,     0,    11,     0,    12,     0,     0,     0,
       2,     3,     4,     5,   117,     0,    11,     0,    12,     7,
       8,     9,     2,     0,     0,     5,     6,     0,     0,     0,
       0,     7,     8,     9,     0,    11,     0,    12,     0,     0,
       0,     0,   466,     0,     0,     0,     0,    11,     0,    12,
     263,   264,   265,     0,   266,   267,   268,   269,   270,   271,
     272,   273,   274,   275,   276,   277,   278,   279,   467,     0,
       0,     0,     0,     0,     0,     0,   263,   264,   265,     0,
     266,   267,   268,   269,   270,   271,   272,   273,   274,   275,
     276,   277,   278,   279,   662,     0,     0,     0,     0,     0,
       0,     0,   263,   264,   265,     0,   266,   267,   268,   269,
     270,   271,   272,   273,   274,   275,   276,   277,   278,   279,
       2,     3,     4,     5,   122,     0,     0,     0,     0,     7,
       8,     9,     0,     0,     0,   777,     0,     0,     0,     0,
       0,     0,   263,   264,   265,    11,   266,   267,   268,   269,
     270,   271,   272,   273,   274,   275,   276,   277,   278,   279,
     516,     0,     0,     0,     0,     0,   263,   264,   265,     0,
     266,   267,   268,   269,   270,   271,   272,   273,   274,   275,
     276,   277,   278,   279,   646,     0,     0,     0,     0,     0,
     263,   264,   265,     0,   266,   267,   268,   269,   270,   271,
     272,   273,   274,   275,   276,   277,   278,   279,   647,     0,
       0,     0,     0,     0,   263,   264,   265,     0,   266,   267,
     268,   269,   270,   271,   272,   273,   274,   275,   276,   277,
     278,   279,   798,     0,     0,     0,     0,     0,   263,   264,
     265,     0,   266,   267,   268,   269,   270,   271,   272,   273,
     274,   275,   276,   277,   278,   279,   263,   264,   265,   802,
     266,   267,   268,   269,   270,   271,   272,   273,   274,   275,
     276,   277,   278,   279,   263,   264,   265,     0,   266,   267,
     268,   269,   270,   271,   272,   273,   274,   275,   276,   277,
     278,   279,   265,     0,   266,   267,   268,   269,   270,   271,
     272,   273,   274,   275,   276,   277,   278,   279,   267,   268,
     269,   270,   271,   272,   273,   274,   275,   276,   277,   278,
     279
};

static const yytype_int16 yycheck[] =
{
      61,    50,    51,    52,    65,   314,   145,    71,     0,   283,
     144,   489,   228,     0,     0,     0,   232,   411,   214,   412,
     177,     0,   437,     0,    16,   199,     0,     0,   224,    16,
      16,    16,   342,   162,   163,   228,   217,    16,   605,    16,
       0,   337,    16,    16,   412,   174,   444,   599,   558,   178,
     489,     0,    10,   214,   405,     0,    16,    10,   507,   188,
      31,   152,     1,    55,    10,   742,     1,    16,    55,    55,
      55,    16,     0,   234,   489,    16,    55,     1,    55,   489,
      67,    55,    55,     1,    22,    39,    40,     0,    16,    46,
     568,    74,   249,    67,    67,    55,    54,    43,    44,   509,
     405,     0,   259,   299,   261,   154,    55,     3,     4,    74,
      55,     3,     4,    48,    55,   682,    28,    16,    67,     1,
     316,   170,    75,   800,    48,     3,     1,    55,    48,    47,
      47,   482,   483,   484,   485,    74,    10,   411,   506,   507,
     189,    58,    59,    60,   235,   520,   237,     1,   700,     3,
       4,   215,     1,   568,   218,     3,    55,   667,    75,     3,
       4,   671,    74,    75,   228,    47,   605,    84,   232,     8,
      66,   489,    47,   548,    66,     3,    93,   482,   483,   484,
     485,     3,    74,    75,    43,   102,    74,    46,    66,     3,
       4,   509,    31,   242,   111,   605,    74,    75,    47,    44,
     361,    74,     8,   120,   265,    48,    54,   145,     3,     4,
      54,    68,   129,     3,     4,     3,     4,    48,    66,    44,
      31,   138,    66,   284,   405,    31,    74,   214,   780,    54,
      74,   783,    74,   282,    48,   222,   285,   224,    74,   413,
     214,   214,   234,   682,    66,   222,   375,   234,   222,   234,
     224,   224,    74,    48,    31,   234,     8,   234,    48,    44,
     234,   234,   222,    74,    75,   214,   818,    74,   206,   207,
     719,   720,   682,    44,   234,   224,   340,    54,    66,    31,
     482,   483,   346,   485,    44,   234,    74,   605,    44,    74,
      75,   208,   450,    31,    54,   453,    43,    74,    75,   697,
      51,    44,   696,    74,    75,   486,   435,   446,    44,   466,
     788,   468,   299,     6,   707,     8,    54,   366,    74,    75,
      44,   455,    73,   716,    75,   299,   299,   314,    46,   316,
      54,   524,   606,    46,    47,   644,    66,   314,    31,   707,
     314,   405,   316,   316,    74,    75,    43,     8,   716,    46,
     299,   719,   720,     8,   314,   404,   213,    74,    43,   655,
     409,    46,   219,   659,   682,     3,     4,   316,   225,   361,
      74,    75,   310,   788,   361,    43,   361,    47,    46,   333,
     334,    47,   361,    43,   361,    43,    46,   361,   361,    69,
      70,   806,   807,    73,    74,    75,    76,   335,   336,    51,
     710,   361,   712,    43,   358,   359,    46,     3,    43,    74,
      11,    46,   361,    43,    43,   611,    46,    46,   482,   483,
      44,   485,   696,    44,   620,    44,   413,    44,   489,    62,
      63,    64,    65,    66,    67,    68,   413,    74,    75,   413,
     489,     7,     8,   300,    45,    43,    44,    13,    14,    15,
      51,    52,    53,   413,    55,    56,    57,    58,    59,    60,
      61,    62,    63,    64,    65,    66,    67,    68,    74,    75,
     424,   425,     5,     6,     7,     8,   605,    66,    67,    68,
      13,    14,    15,    46,   341,   342,     5,     6,     7,     8,
     347,    46,    47,    33,    13,    14,    15,   489,    31,    44,
     549,    10,   489,   489,   489,     6,   363,   568,   446,    51,
     489,   560,   489,    46,   501,   489,   489,   509,   456,   568,
      43,    44,   509,   509,   501,   586,   590,   501,    46,   489,
     509,    46,   509,    46,   708,   509,   509,    47,   587,   755,
     489,   501,   758,   717,   489,    23,    24,    25,    26,   509,
      74,    75,    47,    31,    32,    33,    34,    47,   415,   698,
     509,   489,   755,   697,   509,     7,     8,    43,   506,   507,
      44,    13,    14,    15,    74,    75,    23,    24,    25,    26,
      44,   509,    43,    44,    31,    32,    33,    34,    54,     4,
     489,   725,     7,     8,    43,    44,   182,   183,    13,    14,
      15,    44,   459,    44,   461,   707,   708,   464,   335,   336,
     509,    45,    44,   605,    29,   676,   677,    44,   605,   605,
     605,   682,   683,    44,   611,    48,   605,    47,   605,   690,
      48,   605,   605,   620,    51,    47,     1,   611,   611,    47,
     594,   595,    44,   704,    44,   605,   620,   620,     3,    28,
      43,    43,     1,   510,    44,     4,   605,   644,     7,     8,
     605,    44,   611,    54,    13,    14,    15,   644,    43,    47,
     644,   620,    54,    74,    54,   532,    74,   605,    74,   536,
      29,   742,    31,    32,   644,   542,    74,   641,   642,   750,
     682,   755,    46,    46,   758,   682,   682,   682,    64,    65,
      66,    67,    68,   682,     8,   682,   605,    46,   682,   682,
      46,     7,     8,   191,   192,   193,   194,    13,    14,    15,
      17,   708,   682,    47,    47,    18,   790,   788,   792,    46,
     717,   708,    46,   682,   708,    31,    74,   682,    54,   800,
     717,    44,    44,   717,   191,   192,   193,   194,   708,    74,
      44,    46,    44,    44,   682,   612,   613,   717,   819,   616,
     698,    46,    46,   812,   621,   622,    46,    46,    44,   707,
     708,    43,    45,   834,    44,    46,    10,     0,   716,   717,
      46,   719,   720,   682,     7,     8,     9,    74,    10,    44,
     728,    10,    46,    16,   218,   652,   222,   509,   715,   656,
      23,   509,    25,   509,    27,   449,    29,   509,    31,   222,
      33,   310,    35,   529,    37,    23,    24,    25,    26,    27,
      28,    29,    30,    31,    32,    33,    34,    35,    36,    37,
      38,   409,    55,    60,    61,    62,    63,    64,    65,    66,
      67,    68,    65,   669,    67,   707,   557,   325,   326,   719,
     361,   329,   330,   710,   368,   712,     5,     6,     7,     8,
     725,   728,   366,   406,    13,    14,    15,   724,   242,   440,
     727,   681,   350,   351,   352,   353,   823,     4,   325,   326,
       7,     8,   329,   330,   788,   822,    13,    14,    15,   415,
      27,    28,    29,    30,   751,   425,    -1,    46,    35,    36,
      37,    38,    29,   350,   351,   352,   353,    -1,    -1,    -1,
      -1,   768,   769,     1,   771,    -1,     4,     5,     6,     7,
       8,    27,    28,    29,    30,    13,    14,    15,    -1,    35,
      36,    37,    38,    -1,    -1,    -1,     5,     6,     7,     8,
      28,    29,    -1,    31,    13,    14,    15,    -1,    -1,   199,
      -1,    -1,    -1,    -1,   177,    43,    -1,    -1,    46,    -1,
      48,    -1,    31,    51,    -1,    -1,    -1,    -1,   191,    -1,
     193,    -1,   195,    -1,   197,    -1,    -1,    46,   201,   229,
     203,   189,    -1,    -1,    -1,    -1,    74,    75,    69,    70,
      -1,   214,    73,    74,    75,    76,    -1,    -1,    -1,     0,
       1,   224,     3,     4,     5,     6,     7,     8,    -1,    -1,
      -1,   234,    13,    14,    15,     5,     6,     7,     8,    -1,
      -1,    -1,    -1,    13,    14,    15,   249,    28,    29,    -1,
      31,    32,    -1,    -1,    -1,    -1,   259,    -1,   261,    -1,
       1,    31,    -1,     4,    -1,    46,     7,     8,    -1,    -1,
      -1,    -1,    13,    14,    15,   305,    46,    -1,   195,   196,
     197,   198,    -1,    -1,    -1,    66,    -1,    -1,    29,    -1,
      31,    32,    -1,    74,     7,     8,   299,    -1,    -1,    -1,
      13,    14,    15,    -1,    -1,    46,    47,    -1,    -1,   195,
     196,   197,   198,   316,    -1,    -1,    -1,    -1,    31,    -1,
      -1,    -1,   325,    -1,   327,    -1,   329,    -1,   331,    61,
      62,    63,    64,    65,    66,    67,    68,   325,   326,   327,
     328,   329,   330,   331,   332,    -1,    -1,   350,    -1,   352,
      -1,   354,    -1,   356,    -1,    -1,    -1,    -1,   361,     1,
      -1,     3,     4,     5,     6,     7,     8,   625,   626,   627,
     628,    13,    14,    15,    -1,   633,   634,   635,   636,    -1,
      -1,    -1,   412,   413,    -1,    -1,    28,    29,    -1,    31,
      32,     3,     4,     5,     6,     7,     8,    -1,   625,   626,
     627,   628,    -1,    -1,    46,    -1,   633,   634,   635,   636,
     327,   328,    -1,    -1,   331,   332,    -1,   447,   448,    -1,
      -1,   409,    -1,    -1,    66,     5,     6,     7,     8,    -1,
      -1,    -1,    74,    13,    14,    15,    -1,   354,   355,   356,
     357,   327,   328,    -1,    -1,   331,   332,    -1,    -1,     4,
       5,     6,     7,     8,    -1,    -1,    -1,    -1,    13,    14,
      15,    -1,    -1,   466,     1,   468,     3,     4,   354,   355,
     356,   357,     9,    10,    29,    12,   506,   507,     4,     5,
       6,     7,     8,    -1,    -1,    -1,   489,    13,    14,    15,
      -1,    46,    -1,    30,    -1,    32,    -1,    34,    35,    36,
      37,    38,    -1,    29,    -1,    42,   509,    -1,    -1,    -1,
      47,    48,    49,    50,     4,     5,     6,     7,     8,    56,
      46,    -1,    59,    13,    14,    15,    -1,    64,    65,    66,
      -1,    -1,    69,    70,    -1,    -1,    73,    74,    75,    29,
      -1,   247,   248,    -1,    -1,     5,     6,     7,     8,    -1,
      -1,    -1,   258,    13,    14,    15,    46,   263,   264,    -1,
     266,   267,   268,   269,   270,   271,   272,   273,   274,   275,
     276,   277,   278,   279,    -1,    -1,     1,    -1,    -1,     4,
       5,     6,     7,     8,   614,   615,    46,    -1,    13,    14,
      15,    -1,    -1,    -1,    -1,    -1,     4,     5,     6,     7,
       8,    -1,   605,    -1,    29,    13,    14,    15,   611,    -1,
      -1,    -1,   318,    -1,    -1,    -1,   619,   620,    -1,    -1,
      -1,    29,   625,    48,   627,    -1,   629,    -1,   631,    -1,
     633,    -1,   635,    -1,   637,    -1,   639,   625,   626,   627,
     628,   629,   630,   631,   632,   633,   634,   635,   636,   637,
     638,   639,   640,    -1,    -1,    -1,    -1,    -1,    -1,   365,
      -1,    -1,    -1,     4,     5,     6,     7,     8,    -1,    -1,
      -1,    -1,    13,    14,    15,    -1,   382,   707,   708,   682,
      -1,    -1,    -1,    -1,    -1,    -1,   716,   717,    29,   719,
     720,    -1,    -1,    -1,    -1,    -1,    -1,     1,    -1,     3,
       4,     5,     6,     7,     8,    46,    -1,    11,    -1,    13,
      14,    15,   629,   630,   631,   632,    -1,    -1,    -1,    -1,
     637,   638,   639,   640,    -1,    29,   432,   433,    -1,     5,
       6,     7,     8,   763,   764,   765,   442,    13,    14,    15,
      44,    -1,    -1,   629,   630,   631,   632,     1,    -1,     3,
       4,   637,   638,   639,   640,     9,    10,    -1,    12,    -1,
      -1,   467,    66,   469,     5,     6,     7,     8,   474,    -1,
      74,    75,    13,    14,    15,    -1,    30,    -1,    32,    -1,
      34,    35,    36,    37,    38,    -1,    -1,    -1,    42,    -1,
      31,    -1,    -1,    47,    48,    49,    50,    -1,     5,     6,
       7,     8,    56,    -1,    -1,    59,    13,    14,    15,    -1,
      64,    65,    66,    -1,   520,    69,    70,    -1,    -1,    73,
      74,    75,     1,   529,     3,   531,    -1,    -1,    -1,   535,
       9,    10,    -1,    12,    -1,    -1,    -1,     4,     5,     6,
       7,     8,   548,    -1,   550,    -1,    13,    14,    15,    -1,
      -1,    30,   558,    32,    -1,    34,    35,    36,    37,    38,
      -1,    -1,    29,    42,    31,    -1,    -1,    -1,    -1,    48,
      49,    50,    51,    -1,    -1,    -1,   582,    56,    -1,    46,
      59,    -1,    -1,    -1,    -1,    64,    65,    66,    -1,    -1,
      69,    70,    -1,    -1,    73,    74,    75,    -1,     0,     1,
      -1,     3,     4,     5,     6,     7,     8,     5,     6,     7,
       8,    13,    14,    15,    -1,    13,    14,    15,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    28,    29,     4,    31,
      32,     7,     8,    31,    -1,    -1,    -1,    13,    14,    15,
      -1,    -1,    -1,    -1,    46,    -1,    -1,   653,    46,    -1,
      -1,   657,    -1,    29,    -1,    31,   662,    -1,    -1,    -1,
      -1,   667,    -1,   669,    66,   671,    -1,    -1,    -1,    -1,
       1,    -1,    74,     4,     5,     6,     7,     8,     5,     6,
       7,     8,    13,    14,    15,    -1,    13,    14,    15,    -1,
      -1,    -1,    -1,     5,     6,     7,     8,    28,    29,    -1,
      31,    13,    14,    15,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    43,    -1,    -1,    46,    -1,    48,    -1,    46,
      51,    -1,    -1,     1,    -1,   731,     4,     5,     6,     7,
       8,    -1,    -1,    -1,    46,    13,    14,    15,    -1,   745,
      -1,    -1,    -1,    74,    75,    -1,    -1,    -1,    -1,    -1,
      28,    29,    -1,    31,     5,     6,     7,     8,    -1,    -1,
      -1,    -1,    13,    14,    15,    43,    -1,    -1,    46,    -1,
      48,    -1,     1,    51,     3,     4,     5,     6,     7,     8,
      31,    -1,    11,    -1,    13,    14,    15,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    74,    75,     1,    -1,
      29,     4,     5,     6,     7,     8,    -1,    -1,    11,    -1,
      13,    14,    15,    -1,    -1,    44,    59,    60,    61,    62,
      63,    64,    65,    66,    67,    68,    29,    -1,    -1,     4,
       5,     6,     7,     8,    -1,    -1,    11,    66,    13,    14,
      15,    44,    -1,    -1,    -1,    74,    75,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    29,    -1,    31,     5,     6,     7,
       8,    -1,    -1,    66,    -1,    13,    14,    15,    -1,    -1,
      -1,    74,    75,     1,    -1,     3,     4,     5,     6,     7,
       8,     9,    10,    31,    12,    13,    14,    15,    16,    -1,
      18,    19,    20,    21,    22,    23,    24,    25,    26,    27,
      28,    29,    30,    31,    32,    33,    34,    35,    36,    37,
      38,    -1,    -1,    -1,    42,    -1,    -1,    -1,    46,    47,
      48,    49,    50,    -1,    -1,    -1,    -1,    -1,    56,    -1,
      -1,    59,    -1,    -1,    -1,    -1,    64,    65,    66,    -1,
      -1,    69,    70,    -1,    -1,     1,    74,     3,     4,     5,
       6,     7,     8,     9,    10,    -1,    12,    13,    14,    15,
      16,    -1,    18,    19,    20,    21,    22,    23,    24,    25,
      26,    27,    28,    29,    30,    31,    32,    -1,    34,    35,
      36,    37,    38,    -1,    -1,    -1,    42,    -1,    -1,    -1,
      46,    -1,    48,    49,    50,    -1,     5,     6,     7,     8,
      56,    -1,    -1,    59,    13,    14,    15,    -1,    64,    65,
      66,    -1,    -1,    69,    70,    -1,    -1,     1,    74,     3,
       4,    -1,    31,    -1,    -1,     9,    10,    -1,    12,    -1,
      -1,    -1,    16,    -1,    18,    19,    20,    21,    22,    23,
      24,    25,    26,    27,    28,    -1,    30,    -1,    32,    -1,
      34,    35,    36,    37,    38,    -1,    -1,    -1,    42,    -1,
      -1,    -1,    46,    -1,    48,    49,    50,    -1,    -1,    -1,
      -1,    -1,    56,    -1,    -1,    59,    -1,    -1,    -1,    -1,
      64,    65,    66,    -1,    -1,    69,    70,    -1,    -1,     1,
      74,     3,     4,    -1,    -1,    -1,    -1,     9,    10,    -1,
      12,    -1,    -1,    -1,    16,    -1,    18,    19,    20,    21,
      22,    23,    24,    25,    26,    27,    28,    -1,    30,    -1,
      32,    -1,    34,    35,    36,    37,    38,    -1,    -1,    -1,
      42,    -1,    -1,    -1,    46,    -1,    48,    49,    50,    -1,
      -1,    -1,    -1,    -1,    56,    -1,    -1,    59,    -1,    -1,
      -1,    -1,    64,    65,    66,    -1,    -1,    69,    70,    -1,
      -1,     1,    74,     3,     4,    -1,    -1,     7,     8,     9,
      10,    -1,    12,    13,    14,    15,     4,     5,     6,     7,
       8,    -1,    -1,    -1,    -1,    13,    14,    15,    -1,    29,
      30,    31,    32,    -1,    34,    35,    36,    37,    38,    -1,
      -1,    29,    42,    31,     1,    -1,     3,    -1,    48,    49,
      50,    -1,     9,    10,    -1,    12,    56,    -1,    46,    59,
      -1,    -1,    -1,    -1,    64,    65,    66,    -1,    -1,    69,
      70,    -1,    -1,    30,    74,    32,    -1,    34,    35,    36,
      37,    38,    -1,    -1,    -1,    42,    -1,     1,    -1,     3,
      -1,    48,    49,    50,    -1,     9,    10,    -1,    12,    56,
      -1,    -1,    59,    -1,    -1,    -1,    -1,    64,    65,    66,
      -1,    -1,    69,    70,    -1,    -1,    30,    74,    32,    -1,
      34,    35,    36,    37,    38,    -1,    -1,    -1,    42,     4,
       5,     6,     7,     8,    48,    49,    50,    -1,    13,    14,
      15,    -1,    56,    -1,    -1,    59,    -1,    -1,    -1,    -1,
      64,    65,    66,    -1,    29,    69,    70,    -1,    -1,    -1,
      74,     3,     4,     5,     6,     7,     8,     9,    10,    -1,
      12,    13,    14,    15,    16,    -1,    18,    19,    20,    21,
      22,    23,    24,    25,    26,    27,    28,    29,    30,    31,
      32,    -1,    34,    35,    36,    37,    38,    -1,    -1,    -1,
      42,    -1,    -1,    -1,    46,    -1,    48,    49,    50,     4,
       5,     6,     7,     8,    56,    -1,    -1,    59,    13,    14,
      15,    -1,    64,    65,    66,    -1,    -1,    69,    70,     3,
       4,    -1,    74,    -1,    29,     9,    10,    -1,    12,    -1,
      -1,    -1,    16,    -1,    18,    19,    20,    21,    22,    23,
      24,    25,    26,    27,    28,    -1,    30,    -1,    32,    -1,
      34,    35,    36,    37,    38,    -1,    -1,    -1,    42,    -1,
      -1,    -1,    46,    -1,    48,    49,    50,    -1,    -1,    -1,
      -1,    -1,    56,    -1,    -1,    59,    -1,    -1,    -1,    -1,
      64,    65,    66,    -1,    -1,    69,    70,    -1,    -1,    -1,
      74,     3,     4,     5,     6,     7,     8,     9,    10,    -1,
      12,    13,    14,    15,     4,     5,     6,     7,     8,    -1,
      -1,    -1,    -1,    13,    14,    15,    -1,    29,    30,    31,
      32,    -1,    34,    35,    36,    37,    38,    -1,    -1,    29,
      42,    31,    -1,    -1,    -1,    -1,    -1,    49,    50,    -1,
      -1,    -1,    -1,    -1,    56,    -1,    46,    59,    -1,    -1,
      -1,    -1,    64,    65,    66,    -1,    -1,    69,    70,     3,
       4,    -1,    74,     7,     8,     9,    10,    -1,    12,    13,
      14,    15,     4,     5,     6,     7,     8,    -1,    -1,    -1,
      -1,    13,    14,    15,    -1,    29,    30,    31,    32,    -1,
      34,    35,    36,    37,    38,    -1,    -1,    29,    42,    31,
      -1,    -1,     3,    -1,    -1,    49,    50,    -1,     9,    10,
      -1,    12,    56,    -1,    46,    59,    -1,    -1,    -1,    -1,
      64,    65,    66,    -1,    -1,    69,    70,    -1,    -1,    30,
      74,    32,    -1,    34,    35,    36,    37,    38,    -1,    -1,
      -1,    42,    -1,    -1,    -1,     3,    -1,    -1,    49,    50,
      -1,     9,    10,    54,    12,    56,    -1,    -1,    59,    -1,
      -1,    -1,    -1,    64,    65,    66,    -1,    -1,    69,    70,
      -1,    -1,    30,    74,    32,    -1,    34,    35,    36,    37,
      38,    -1,    -1,    -1,    42,    -1,    -1,    45,     3,    -1,
      -1,    49,    50,    -1,     9,    10,    -1,    12,    56,    -1,
      -1,    59,    -1,    -1,    -1,    -1,    64,    65,    66,    -1,
      -1,    69,    70,    -1,    -1,    30,    74,    32,    -1,    34,
      35,    36,    37,    38,    -1,    -1,    -1,    42,    -1,    -1,
      -1,     3,    -1,    48,    49,    50,    -1,     9,    10,    -1,
      12,    56,    -1,    -1,    59,    -1,    -1,    -1,    -1,    64,
      65,    66,    -1,    -1,    69,    70,    -1,    -1,    30,    74,
      32,    -1,    34,    35,    36,    37,    38,    -1,    -1,    -1,
      42,    -1,    -1,    45,     3,    -1,    -1,    49,    50,    -1,
       9,    10,    -1,    12,    56,    -1,    -1,    59,    -1,    -1,
      -1,    -1,    64,    65,    66,    -1,    -1,    69,    70,    -1,
      -1,    30,    74,    32,    -1,    34,    35,    36,    37,    38,
      -1,    -1,    -1,    42,    -1,    -1,    -1,    46,    -1,     3,
      49,    50,    -1,    -1,    -1,     9,    10,    56,    12,    -1,
      59,    -1,    -1,    -1,    -1,    64,    65,    66,    -1,    -1,
      69,    70,    -1,    -1,    -1,    74,    30,    -1,    32,    -1,
      34,    35,    36,    37,    38,    -1,    -1,    -1,    42,    -1,
      -1,    -1,     3,    -1,    -1,    49,    50,    -1,     9,    10,
      -1,    12,    56,    -1,    -1,    59,    -1,    -1,    -1,    -1,
      64,    65,    66,    -1,    -1,    69,    70,    -1,    -1,    30,
      74,    32,    -1,    34,    35,    36,    37,    38,    -1,    -1,
      -1,    42,    -1,    -1,    -1,     3,    -1,    -1,    49,    50,
      -1,     9,    10,    -1,    12,    56,    -1,    -1,    59,    -1,
      -1,    -1,    -1,    64,    65,    66,    -1,    -1,    69,    70,
      -1,    -1,    30,    74,    32,    -1,    34,    35,    36,    37,
      38,    -1,    -1,    -1,    42,    -1,    -1,    -1,     3,    -1,
      -1,    49,    50,    -1,     9,    10,    -1,    12,    56,    -1,
      -1,    59,    -1,    -1,    -1,    -1,    64,    65,    66,    -1,
      -1,    69,    70,    -1,    -1,    30,    74,    32,    -1,    34,
      35,    36,    37,    38,    11,    -1,    -1,    42,    -1,    -1,
      -1,    -1,    -1,    -1,    49,    50,     4,     5,     6,     7,
       8,    56,    -1,    -1,    59,    13,    14,    15,    -1,    64,
      65,    66,    -1,    -1,    69,    70,    -1,    -1,    -1,    74,
      -1,    29,    -1,    31,    51,    52,    53,    54,    55,    56,
      57,    58,    59,    60,    61,    62,    63,    64,    65,    66,
      67,    68,     1,    -1,    -1,     4,     5,     6,     7,     8,
      -1,    -1,    -1,    -1,    13,    14,    15,     4,     5,     6,
       7,     8,    -1,    -1,    -1,    -1,    13,    14,    15,    28,
      29,    -1,    31,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    29,    -1,    43,    -1,    -1,    46,    -1,    48,
       1,    -1,    51,     4,     5,     6,     7,     8,    -1,    -1,
      -1,    -1,    13,    14,    15,    57,    58,    59,    60,    61,
      62,    63,    64,    65,    66,    67,    68,    28,    29,     4,
      31,    -1,     7,     8,    -1,    -1,    -1,    -1,    13,    14,
      15,    -1,    43,    -1,    -1,    46,     3,    48,    -1,    -1,
      51,    -1,     9,    10,    29,    12,    31,    -1,    -1,    16,
      -1,    18,    19,    20,    21,    -1,    -1,    24,    25,    26,
      27,    28,    -1,    30,    -1,    32,    -1,    34,    35,    36,
      37,    38,    -1,    -1,    -1,    42,    -1,    -1,    -1,    46,
      -1,    48,    49,    50,     1,    -1,     3,     4,     5,     6,
       7,     8,    -1,    -1,    11,    -1,    13,    14,    15,    -1,
      -1,     1,    -1,    -1,     4,     5,     6,     7,     8,    -1,
      -1,    11,    29,    13,    14,    15,    -1,    -1,    -1,    -1,
      -1,    31,    -1,    -1,    -1,    -1,    -1,    44,    -1,    29,
      58,    59,    60,    61,    62,    63,    64,    65,    66,    67,
      68,    51,    52,    53,    44,    55,    56,    57,    58,    59,
      60,    61,    62,    63,    64,    65,    66,    67,    68,     4,
       5,     6,     7,     8,    -1,    -1,    -1,    -1,    13,    14,
      15,     4,     5,     6,     7,     8,    -1,    -1,    -1,    -1,
      13,    14,    15,    -1,    29,    -1,    31,    -1,    -1,    -1,
       4,     5,     6,     7,     8,    -1,    29,    -1,    31,    13,
      14,    15,     4,    -1,    -1,     7,     8,    -1,    -1,    -1,
      -1,    13,    14,    15,    -1,    29,    -1,    31,    -1,    -1,
      -1,    -1,    43,    -1,    -1,    -1,    -1,    29,    -1,    31,
      51,    52,    53,    -1,    55,    56,    57,    58,    59,    60,
      61,    62,    63,    64,    65,    66,    67,    68,    43,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    51,    52,    53,    -1,
      55,    56,    57,    58,    59,    60,    61,    62,    63,    64,
      65,    66,    67,    68,    43,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    51,    52,    53,    -1,    55,    56,    57,    58,
      59,    60,    61,    62,    63,    64,    65,    66,    67,    68,
       4,     5,     6,     7,     8,    -1,    -1,    -1,    -1,    13,
      14,    15,    -1,    -1,    -1,    44,    -1,    -1,    -1,    -1,
      -1,    -1,    51,    52,    53,    29,    55,    56,    57,    58,
      59,    60,    61,    62,    63,    64,    65,    66,    67,    68,
      45,    -1,    -1,    -1,    -1,    -1,    51,    52,    53,    -1,
      55,    56,    57,    58,    59,    60,    61,    62,    63,    64,
      65,    66,    67,    68,    45,    -1,    -1,    -1,    -1,    -1,
      51,    52,    53,    -1,    55,    56,    57,    58,    59,    60,
      61,    62,    63,    64,    65,    66,    67,    68,    45,    -1,
      -1,    -1,    -1,    -1,    51,    52,    53,    -1,    55,    56,
      57,    58,    59,    60,    61,    62,    63,    64,    65,    66,
      67,    68,    45,    -1,    -1,    -1,    -1,    -1,    51,    52,
      53,    -1,    55,    56,    57,    58,    59,    60,    61,    62,
      63,    64,    65,    66,    67,    68,    51,    52,    53,    54,
      55,    56,    57,    58,    59,    60,    61,    62,    63,    64,
      65,    66,    67,    68,    51,    52,    53,    -1,    55,    56,
      57,    58,    59,    60,    61,    62,    63,    64,    65,    66,
      67,    68,    53,    -1,    55,    56,    57,    58,    59,    60,
      61,    62,    63,    64,    65,    66,    67,    68,    56,    57,
      58,    59,    60,    61,    62,    63,    64,    65,    66,    67,
      68
};

/* YYSTOS[STATE-NUM] -- The (internal number of the) accessing
   symbol of state STATE-NUM.  */
static const yytype_uint8 yystos[] =
{
       0,     1,     4,     5,     6,     7,     8,    13,    14,    15,
      28,    29,    31,    32,    46,    98,    99,   100,   101,   102,
     103,   112,   122,   125,   126,   127,   128,   129,   130,   131,
     132,   133,   134,   135,   136,   137,   138,   139,   140,   141,
     142,   148,   150,   151,   152,   153,   154,   161,   162,   166,
     182,   183,   184,   185,   186,   254,    46,    47,   161,   161,
     161,    74,    74,     0,   100,    74,     3,    66,    74,   156,
     159,   181,     8,   150,   151,   161,   166,     8,   150,   151,
     166,     8,   150,   151,   161,   166,     8,   150,   151,   166,
       8,   152,   153,   161,   166,     8,   152,   153,   166,     8,
     152,   153,   161,   166,     8,   152,   153,   166,     8,   150,
     151,   161,   166,     8,   150,   151,   166,     8,   150,   151,
     161,   166,     8,   150,   151,   166,     8,   152,   153,   161,
     166,     8,   152,   153,   166,     8,   152,   153,   161,   166,
       8,   152,   153,   166,   122,   122,    46,   162,     3,     4,
      48,   104,    48,   104,    48,   104,   100,     3,     9,    10,
      12,    30,    34,    35,    36,    37,    38,    42,    49,    50,
      56,    59,    64,    65,    66,    69,    70,    74,   105,   106,
     108,   109,   110,   111,   113,   114,   115,   222,   254,    74,
     106,   125,   126,   127,   128,   129,   130,   131,   132,   147,
     198,   125,   126,   127,   128,   146,   149,   160,   161,    43,
      46,     1,    28,    74,    75,   116,   117,   118,   119,   157,
     205,   212,   227,     4,    66,    74,   155,   158,   176,   177,
     181,   156,   181,   189,   190,    48,   189,    48,     1,   104,
     196,   197,    48,    10,     3,   113,   113,    74,    74,    74,
     104,   113,     1,    48,   106,   198,   113,    44,    43,    74,
     109,    74,   109,    51,    52,    53,    55,    56,    57,    58,
      59,    60,    61,    62,    63,    64,    65,    66,    67,    68,
      69,    70,    73,    74,    75,    76,     1,    47,   213,   220,
     113,     7,     8,   104,   163,   164,   165,   166,    44,    66,
      74,   199,   201,   202,   203,   204,   205,    44,   181,   181,
     123,   160,    46,    74,   160,   250,     6,   146,   149,   227,
       1,   212,   118,   160,   120,   125,   126,   129,   130,   133,
     134,   137,   138,   143,   144,   149,   160,    43,    46,     1,
     116,   157,    74,   205,    46,     1,   116,    47,     1,    46,
     125,   126,   127,   128,   129,   130,   131,   132,   145,   146,
     191,   254,   189,    47,   189,    51,    43,   188,   196,     3,
     114,   114,   198,    44,    44,    44,    46,   114,   198,   198,
     114,   114,    54,   106,   114,   114,   114,   114,   114,   114,
     114,   114,   114,   114,   114,   114,   114,   114,   104,   107,
     108,   106,   104,    44,    33,   215,   216,   217,    44,    43,
      44,    74,   149,   160,   243,    74,   205,    44,   159,   181,
      10,     1,     3,    11,   143,   144,   244,   245,   246,   248,
     251,   252,   149,     6,    45,    66,   114,     1,    48,   218,
     219,   223,    51,    46,   122,    46,   122,   177,   177,   123,
     227,   160,   250,   227,   160,   122,   122,    46,   191,    47,
     160,    47,   114,   197,    47,   188,    43,    43,    43,    48,
     113,    44,    44,   114,    54,    44,    45,   104,   253,     1,
     121,   206,   207,   208,   209,   210,   211,   212,   221,   227,
     230,   231,   217,   164,    44,     3,   107,   202,   203,   201,
     244,   160,   243,    44,    44,   122,   249,   249,    44,    43,
      46,    43,    44,   114,   114,    45,    45,   223,   220,     1,
      48,   114,   167,   155,   176,   156,    44,   158,   218,    51,
     218,    54,   176,   192,   194,    54,   181,   193,   195,   160,
     160,   160,    47,   198,   114,   198,     1,     3,    48,    73,
      75,   104,   114,   168,   169,   170,   171,   172,   173,   114,
      43,    46,   121,   230,   231,   121,   230,   231,   227,   230,
     231,   121,   230,   231,   212,    47,     4,    16,    18,    19,
      20,    21,    22,    23,    24,    25,    26,    27,    28,    46,
     104,   106,   115,   124,   141,   142,   148,   223,   224,   225,
     226,   232,   234,   235,   236,   254,    43,    44,    44,    44,
       4,    66,    74,   178,   179,   180,   181,   200,   201,   202,
      66,    74,   181,   200,    11,   125,   126,   127,   128,   129,
     130,   131,   132,   133,   134,   135,   136,   137,   138,   139,
     140,   141,   142,   247,   160,     3,    45,    45,   168,    46,
      46,   167,   114,    54,   160,    43,   114,    54,   160,    43,
     160,    44,    43,    44,   168,   104,   114,    54,    47,    43,
     187,    51,   173,   171,   104,   214,    74,    74,   228,   229,
     230,   231,    74,    74,   114,    54,    46,    46,    46,   106,
      66,   104,     8,   237,   227,    46,    74,   122,   122,    46,
      17,     1,   229,     1,    74,   124,   108,   149,   160,   160,
      74,   205,    74,   205,   160,   161,   149,   160,   160,   122,
     122,   244,    47,   160,   114,   123,   160,   114,   123,   114,
      47,    11,    45,   171,   170,   171,   106,   106,    18,   228,
     106,   124,   233,   238,   106,    11,    54,    46,   106,    46,
      74,    54,   107,   155,   174,   176,   156,   175,   181,     1,
     229,   106,    44,   179,   180,   180,   250,   250,   178,   181,
     200,   181,   200,   160,   194,   160,   195,    44,   114,    44,
      44,   238,    46,    44,   114,    46,   106,   160,    44,    46,
     116,    46,   116,    44,    44,   160,   160,   160,    45,   229,
      46,   229,    54,    44,    54,   234,   227,   227,    46,   238,
      46,    10,    75,   239,   240,   241,   223,   223,    44,    74,
     104,    44,    54,    43,   229,   106,    45,    46,   239,   241,
      44,    10,    44,    54,    74,    46,    10,   242,   106,    43,
      44,    44,    10,    46
};

#define yyerrok		(yyerrstatus = 0)
#define yyclearin	(yychar = YYEMPTY)
#define YYEMPTY		(-2)
#define YYEOF		0

#define YYACCEPT	goto yyacceptlab
#define YYABORT		goto yyabortlab
#define YYERROR		goto yyerrorlab


/* Like YYERROR except do call yyerror.  This remains here temporarily
   to ease the transition to the new meaning of YYERROR, for GCC.
   Once GCC version 2 has supplanted version 1, this can go.  */

#define YYFAIL		goto yyerrlab

#define YYRECOVERING()  (!!yyerrstatus)

#define YYBACKUP(Token, Value)					\
do								\
  if (yychar == YYEMPTY && yylen == 1)				\
    {								\
      yychar = (Token);						\
      yylval = (Value);						\
      yytoken = YYTRANSLATE (yychar);				\
      YYPOPSTACK (1);						\
      goto yybackup;						\
    }								\
  else								\
    {								\
      yyerror (YY_("syntax error: cannot back up")); \
      YYERROR;							\
    }								\
while (YYID (0))


#define YYTERROR	1
#define YYERRCODE	256


/* YYLLOC_DEFAULT -- Set CURRENT to span from RHS[1] to RHS[N].
   If N is 0, then set CURRENT to the empty location which ends
   the previous symbol: RHS[0] (always defined).  */

#define YYRHSLOC(Rhs, K) ((Rhs)[K])
#ifndef YYLLOC_DEFAULT
# define YYLLOC_DEFAULT(Current, Rhs, N)				\
    do									\
      if (YYID (N))                                                    \
	{								\
	  (Current).first_line   = YYRHSLOC (Rhs, 1).first_line;	\
	  (Current).first_column = YYRHSLOC (Rhs, 1).first_column;	\
	  (Current).last_line    = YYRHSLOC (Rhs, N).last_line;		\
	  (Current).last_column  = YYRHSLOC (Rhs, N).last_column;	\
	}								\
      else								\
	{								\
	  (Current).first_line   = (Current).last_line   =		\
	    YYRHSLOC (Rhs, 0).last_line;				\
	  (Current).first_column = (Current).last_column =		\
	    YYRHSLOC (Rhs, 0).last_column;				\
	}								\
    while (YYID (0))
#endif


/* YY_LOCATION_PRINT -- Print the location on the stream.
   This macro was not mandated originally: define only if we know
   we won't break user code: when these are the locations we know.  */

#ifndef YY_LOCATION_PRINT
# if defined YYLTYPE_IS_TRIVIAL && YYLTYPE_IS_TRIVIAL
#  define YY_LOCATION_PRINT(File, Loc)			\
     fprintf (File, "%d.%d-%d.%d",			\
	      (Loc).first_line, (Loc).first_column,	\
	      (Loc).last_line,  (Loc).last_column)
# else
#  define YY_LOCATION_PRINT(File, Loc) ((void) 0)
# endif
#endif


/* YYLEX -- calling `yylex' with the right arguments.  */

#ifdef YYLEX_PARAM
# define YYLEX yylex (&yylval, YYLEX_PARAM)
#else
# define YYLEX yylex (&yylval)
#endif

/* Enable debugging if requested.  */
#if YYDEBUG

# ifndef YYFPRINTF
#  include <stdio.h> /* INFRINGES ON USER NAME SPACE */
#  define YYFPRINTF fprintf
# endif

# define YYDPRINTF(Args)			\
do {						\
  if (yydebug)					\
    YYFPRINTF Args;				\
} while (YYID (0))

# define YY_SYMBOL_PRINT(Title, Type, Value, Location)			  \
do {									  \
  if (yydebug)								  \
    {									  \
      YYFPRINTF (stderr, "%s ", Title);					  \
      yy_symbol_print (stderr,						  \
		  Type, Value); \
      YYFPRINTF (stderr, "\n");						  \
    }									  \
} while (YYID (0))


/*--------------------------------.
| Print this symbol on YYOUTPUT.  |
`--------------------------------*/

/*ARGSUSED*/
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static void
yy_symbol_value_print (FILE *yyoutput, int yytype, YYSTYPE const * const yyvaluep)
#else
static void
yy_symbol_value_print (yyoutput, yytype, yyvaluep)
    FILE *yyoutput;
    int yytype;
    YYSTYPE const * const yyvaluep;
#endif
{
  if (!yyvaluep)
    return;
# ifdef YYPRINT
  if (yytype < YYNTOKENS)
    YYPRINT (yyoutput, yytoknum[yytype], *yyvaluep);
# else
  YYUSE (yyoutput);
# endif
  switch (yytype)
    {
      default:
	break;
    }
}


/*--------------------------------.
| Print this symbol on YYOUTPUT.  |
`--------------------------------*/

#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static void
yy_symbol_print (FILE *yyoutput, int yytype, YYSTYPE const * const yyvaluep)
#else
static void
yy_symbol_print (yyoutput, yytype, yyvaluep)
    FILE *yyoutput;
    int yytype;
    YYSTYPE const * const yyvaluep;
#endif
{
  if (yytype < YYNTOKENS)
    YYFPRINTF (yyoutput, "token %s (", yytname[yytype]);
  else
    YYFPRINTF (yyoutput, "nterm %s (", yytname[yytype]);

  yy_symbol_value_print (yyoutput, yytype, yyvaluep);
  YYFPRINTF (yyoutput, ")");
}

/*------------------------------------------------------------------.
| yy_stack_print -- Print the state stack from its BOTTOM up to its |
| TOP (included).                                                   |
`------------------------------------------------------------------*/

#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static void
yy_stack_print (yytype_int16 *bottom, yytype_int16 *top)
#else
static void
yy_stack_print (bottom, top)
    yytype_int16 *bottom;
    yytype_int16 *top;
#endif
{
  YYFPRINTF (stderr, "Stack now");
  for (; bottom <= top; ++bottom)
    YYFPRINTF (stderr, " %d", *bottom);
  YYFPRINTF (stderr, "\n");
}

# define YY_STACK_PRINT(Bottom, Top)				\
do {								\
  if (yydebug)							\
    yy_stack_print ((Bottom), (Top));				\
} while (YYID (0))


/*------------------------------------------------.
| Report that the YYRULE is going to be reduced.  |
`------------------------------------------------*/

#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static void
yy_reduce_print (YYSTYPE *yyvsp, int yyrule)
#else
static void
yy_reduce_print (yyvsp, yyrule)
    YYSTYPE *yyvsp;
    int yyrule;
#endif
{
  int yynrhs = yyr2[yyrule];
  int yyi;
  unsigned long int yylno = yyrline[yyrule];
  YYFPRINTF (stderr, "Reducing stack by rule %d (line %lu):\n",
	     yyrule - 1, yylno);
  /* The symbols being reduced.  */
  for (yyi = 0; yyi < yynrhs; yyi++)
    {
      fprintf (stderr, "   $%d = ", yyi + 1);
      yy_symbol_print (stderr, yyrhs[yyprhs[yyrule] + yyi],
		       &(yyvsp[(yyi + 1) - (yynrhs)])
		       		       );
      fprintf (stderr, "\n");
    }
}

# define YY_REDUCE_PRINT(Rule)		\
do {					\
  if (yydebug)				\
    yy_reduce_print (yyvsp, Rule); \
} while (YYID (0))

/* Nonzero means print parse trace.  It is left uninitialized so that
   multiple parsers can coexist.  */
int yydebug;
#else /* !YYDEBUG */
# define YYDPRINTF(Args)
# define YY_SYMBOL_PRINT(Title, Type, Value, Location)
# define YY_STACK_PRINT(Bottom, Top)
# define YY_REDUCE_PRINT(Rule)
#endif /* !YYDEBUG */


/* YYINITDEPTH -- initial size of the parser's stacks.  */
#ifndef	YYINITDEPTH
# define YYINITDEPTH 200
#endif

/* YYMAXDEPTH -- maximum size the stacks can grow to (effective only
   if the built-in stack extension method is used).

   Do not make this value too large; the results are undefined if
   YYSTACK_ALLOC_MAXIMUM < YYSTACK_BYTES (YYMAXDEPTH)
   evaluated with infinite-precision integer arithmetic.  */

#ifndef YYMAXDEPTH
# define YYMAXDEPTH 10000
#endif



#if YYERROR_VERBOSE

# ifndef yystrlen
#  if defined __GLIBC__ && defined _STRING_H
#   define yystrlen strlen
#  else
/* Return the length of YYSTR.  */
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static YYSIZE_T
yystrlen (const char *yystr)
#else
static YYSIZE_T
yystrlen (yystr)
    const char *yystr;
#endif
{
  YYSIZE_T yylen;
  for (yylen = 0; yystr[yylen]; yylen++)
    continue;
  return yylen;
}
#  endif
# endif

# ifndef yystpcpy
#  if defined __GLIBC__ && defined _STRING_H && defined _GNU_SOURCE
#   define yystpcpy stpcpy
#  else
/* Copy YYSRC to YYDEST, returning the address of the terminating '\0' in
   YYDEST.  */
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static char *
yystpcpy (char *yydest, const char *yysrc)
#else
static char *
yystpcpy (yydest, yysrc)
    char *yydest;
    const char *yysrc;
#endif
{
  char *yyd = yydest;
  const char *yys = yysrc;

  while ((*yyd++ = *yys++) != '\0')
    continue;

  return yyd - 1;
}
#  endif
# endif

# ifndef yytnamerr
/* Copy to YYRES the contents of YYSTR after stripping away unnecessary
   quotes and backslashes, so that it's suitable for yyerror.  The
   heuristic is that double-quoting is unnecessary unless the string
   contains an apostrophe, a comma, or backslash (other than
   backslash-backslash).  YYSTR is taken from yytname.  If YYRES is
   null, do not copy; instead, return the length of what the result
   would have been.  */
static YYSIZE_T
yytnamerr (char *yyres, const char *yystr)
{
  if (*yystr == '"')
    {
      YYSIZE_T yyn = 0;
      char const *yyp = yystr;

      for (;;)
	switch (*++yyp)
	  {
	  case '\'':
	  case ',':
	    goto do_not_strip_quotes;

	  case '\\':
	    if (*++yyp != '\\')
	      goto do_not_strip_quotes;
	    /* Fall through.  */
	  default:
	    if (yyres)
	      yyres[yyn] = *yyp;
	    yyn++;
	    break;

	  case '"':
	    if (yyres)
	      yyres[yyn] = '\0';
	    return yyn;
	  }
    do_not_strip_quotes: ;
    }

  if (! yyres)
    return yystrlen (yystr);

  return yystpcpy (yyres, yystr) - yyres;
}
# endif

/* Copy into YYRESULT an error message about the unexpected token
   YYCHAR while in state YYSTATE.  Return the number of bytes copied,
   including the terminating null byte.  If YYRESULT is null, do not
   copy anything; just return the number of bytes that would be
   copied.  As a special case, return 0 if an ordinary "syntax error"
   message will do.  Return YYSIZE_MAXIMUM if overflow occurs during
   size calculation.  */
static YYSIZE_T
yysyntax_error (char *yyresult, int yystate, int yychar)
{
  int yyn = yypact[yystate];

  if (! (YYPACT_NINF < yyn && yyn <= YYLAST))
    return 0;
  else
    {
      int yytype = YYTRANSLATE (yychar);
      YYSIZE_T yysize0 = yytnamerr (0, yytname[yytype]);
      YYSIZE_T yysize = yysize0;
      YYSIZE_T yysize1;
      int yysize_overflow = 0;
      enum { YYERROR_VERBOSE_ARGS_MAXIMUM = 5 };
      char const *yyarg[YYERROR_VERBOSE_ARGS_MAXIMUM];
      int yyx;

# if 0
      /* This is so xgettext sees the translatable formats that are
	 constructed on the fly.  */
      YY_("syntax error, unexpected %s");
      YY_("syntax error, unexpected %s, expecting %s");
      YY_("syntax error, unexpected %s, expecting %s or %s");
      YY_("syntax error, unexpected %s, expecting %s or %s or %s");
      YY_("syntax error, unexpected %s, expecting %s or %s or %s or %s");
# endif
      char *yyfmt;
      char const *yyf;
      static char const yyunexpected[] = "syntax error, unexpected %s";
      static char const yyexpecting[] = ", expecting %s";
      static char const yyor[] = " or %s";
      char yyformat[sizeof yyunexpected
		    + sizeof yyexpecting - 1
		    + ((YYERROR_VERBOSE_ARGS_MAXIMUM - 2)
		       * (sizeof yyor - 1))];
      char const *yyprefix = yyexpecting;

      /* Start YYX at -YYN if negative to avoid negative indexes in
	 YYCHECK.  */
      int yyxbegin = yyn < 0 ? -yyn : 0;

      /* Stay within bounds of both yycheck and yytname.  */
      int yychecklim = YYLAST - yyn + 1;
      int yyxend = yychecklim < YYNTOKENS ? yychecklim : YYNTOKENS;
      int yycount = 1;

      yyarg[0] = yytname[yytype];
      yyfmt = yystpcpy (yyformat, yyunexpected);

      for (yyx = yyxbegin; yyx < yyxend; ++yyx)
	if (yycheck[yyx + yyn] == yyx && yyx != YYTERROR)
	  {
	    if (yycount == YYERROR_VERBOSE_ARGS_MAXIMUM)
	      {
		yycount = 1;
		yysize = yysize0;
		yyformat[sizeof yyunexpected - 1] = '\0';
		break;
	      }
	    yyarg[yycount++] = yytname[yyx];
	    yysize1 = yysize + yytnamerr (0, yytname[yyx]);
	    yysize_overflow |= (yysize1 < yysize);
	    yysize = yysize1;
	    yyfmt = yystpcpy (yyfmt, yyprefix);
	    yyprefix = yyor;
	  }

      yyf = YY_(yyformat);
      yysize1 = yysize + yystrlen (yyf);
      yysize_overflow |= (yysize1 < yysize);
      yysize = yysize1;

      if (yysize_overflow)
	return YYSIZE_MAXIMUM;

      if (yyresult)
	{
	  /* Avoid sprintf, as that infringes on the user's name space.
	     Don't have undefined behavior even if the translation
	     produced a string with the wrong number of "%s"s.  */
	  char *yyp = yyresult;
	  int yyi = 0;
	  while ((*yyp = *yyf) != '\0')
	    {
	      if (*yyp == '%' && yyf[1] == 's' && yyi < yycount)
		{
		  yyp += yytnamerr (yyp, yyarg[yyi++]);
		  yyf += 2;
		}
	      else
		{
		  yyp++;
		  yyf++;
		}
	    }
	}
      return yysize;
    }
}
#endif /* YYERROR_VERBOSE */


/*-----------------------------------------------.
| Release the memory associated to this symbol.  |
`-----------------------------------------------*/

/*ARGSUSED*/
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static void
yydestruct (const char *yymsg, int yytype, YYSTYPE *yyvaluep)
#else
static void
yydestruct (yymsg, yytype, yyvaluep)
    const char *yymsg;
    int yytype;
    YYSTYPE *yyvaluep;
#endif
{
  YYUSE (yyvaluep);

  if (!yymsg)
    yymsg = "Deleting";
  YY_SYMBOL_PRINT (yymsg, yytype, yyvaluep, yylocationp);

  switch (yytype)
    {

      default:
	break;
    }
}


/* Prevent warnings from -Wmissing-prototypes.  */

#ifdef YYPARSE_PARAM
#if defined __STDC__ || defined __cplusplus
int yyparse (void *YYPARSE_PARAM);
#else
int yyparse ();
#endif
#else /* ! YYPARSE_PARAM */
#if defined __STDC__ || defined __cplusplus
int yyparse (void);
#else
int yyparse ();
#endif
#endif /* ! YYPARSE_PARAM */






/*----------.
| yyparse.  |
`----------*/

#ifdef YYPARSE_PARAM
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
int
yyparse (void *YYPARSE_PARAM)
#else
int
yyparse (YYPARSE_PARAM)
    void *YYPARSE_PARAM;
#endif
#else /* ! YYPARSE_PARAM */
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
int
yyparse (void)
#else
int
yyparse ()

#endif
#endif
{
  /* The look-ahead symbol.  */
int yychar;

/* The semantic value of the look-ahead symbol.  */
YYSTYPE yylval;

/* Number of syntax errors so far.  */
int yynerrs;

  int yystate;
  int yyn;
  int yyresult;
  /* Number of tokens to shift before error messages enabled.  */
  int yyerrstatus;
  /* Look-ahead token as an internal (translated) token number.  */
  int yytoken = 0;
#if YYERROR_VERBOSE
  /* Buffer for error messages, and its allocated size.  */
  char yymsgbuf[128];
  char *yymsg = yymsgbuf;
  YYSIZE_T yymsg_alloc = sizeof yymsgbuf;
#endif

  /* Three stacks and their tools:
     `yyss': related to states,
     `yyvs': related to semantic values,
     `yyls': related to locations.

     Refer to the stacks thru separate pointers, to allow yyoverflow
     to reallocate them elsewhere.  */

  /* The state stack.  */
  yytype_int16 yyssa[YYINITDEPTH];
  yytype_int16 *yyss = yyssa;
  yytype_int16 *yyssp;

  /* The semantic value stack.  */
  YYSTYPE yyvsa[YYINITDEPTH];
  YYSTYPE *yyvs = yyvsa;
  YYSTYPE *yyvsp;



#define YYPOPSTACK(N)   (yyvsp -= (N), yyssp -= (N))

  YYSIZE_T yystacksize = YYINITDEPTH;

  /* The variables used to return semantic value and location from the
     action routines.  */
  YYSTYPE yyval;


  /* The number of symbols on the RHS of the reduced rule.
     Keep to zero when no symbol should be popped.  */
  int yylen = 0;

  YYDPRINTF ((stderr, "Starting parse\n"));

  yystate = 0;
  yyerrstatus = 0;
  yynerrs = 0;
  yychar = YYEMPTY;		/* Cause a token to be read.  */

  /* Initialize stack pointers.
     Waste one element of value and location stack
     so that they stay on the same level as the state stack.
     The wasted elements are never initialized.  */

  yyssp = yyss;
  yyvsp = yyvs;

  goto yysetstate;

/*------------------------------------------------------------.
| yynewstate -- Push a new state, which is found in yystate.  |
`------------------------------------------------------------*/
 yynewstate:
  /* In all cases, when you get here, the value and location stacks
     have just been pushed.  So pushing a state here evens the stacks.  */
  yyssp++;

 yysetstate:
  *yyssp = yystate;

  if (yyss + yystacksize - 1 <= yyssp)
    {
      /* Get the current used size of the three stacks, in elements.  */
      YYSIZE_T yysize = yyssp - yyss + 1;

#ifdef yyoverflow
      {
	/* Give user a chance to reallocate the stack.  Use copies of
	   these so that the &'s don't force the real ones into
	   memory.  */
	YYSTYPE *yyvs1 = yyvs;
	yytype_int16 *yyss1 = yyss;


	/* Each stack pointer address is followed by the size of the
	   data in use in that stack, in bytes.  This used to be a
	   conditional around just the two extra args, but that might
	   be undefined if yyoverflow is a macro.  */
	yyoverflow (YY_("memory exhausted"),
		    &yyss1, yysize * sizeof (*yyssp),
		    &yyvs1, yysize * sizeof (*yyvsp),

		    &yystacksize);

	yyss = yyss1;
	yyvs = yyvs1;
      }
#else /* no yyoverflow */
# ifndef YYSTACK_RELOCATE
      goto yyexhaustedlab;
# else
      /* Extend the stack our own way.  */
      if (YYMAXDEPTH <= yystacksize)
	goto yyexhaustedlab;
      yystacksize *= 2;
      if (YYMAXDEPTH < yystacksize)
	yystacksize = YYMAXDEPTH;

      {
	yytype_int16 *yyss1 = yyss;
	union yyalloc *yyptr =
	  (union yyalloc *) YYSTACK_ALLOC (YYSTACK_BYTES (yystacksize));
	if (! yyptr)
	  goto yyexhaustedlab;
	YYSTACK_RELOCATE (yyss);
	YYSTACK_RELOCATE (yyvs);

#  undef YYSTACK_RELOCATE
	if (yyss1 != yyssa)
	  YYSTACK_FREE (yyss1);
      }
# endif
#endif /* no yyoverflow */

      yyssp = yyss + yysize - 1;
      yyvsp = yyvs + yysize - 1;


      YYDPRINTF ((stderr, "Stack size increased to %lu\n",
		  (unsigned long int) yystacksize));

      if (yyss + yystacksize - 1 <= yyssp)
	YYABORT;
    }

  YYDPRINTF ((stderr, "Entering state %d\n", yystate));

  goto yybackup;

/*-----------.
| yybackup.  |
`-----------*/
yybackup:

  /* Do appropriate processing given the current state.  Read a
     look-ahead token if we need one and don't already have one.  */

  /* First try to decide what to do without reference to look-ahead token.  */
  yyn = yypact[yystate];
  if (yyn == YYPACT_NINF)
    goto yydefault;

  /* Not known => get a look-ahead token if don't already have one.  */

  /* YYCHAR is either YYEMPTY or YYEOF or a valid look-ahead symbol.  */
  if (yychar == YYEMPTY)
    {
      YYDPRINTF ((stderr, "Reading a token: "));
      yychar = YYLEX;
    }

  if (yychar <= YYEOF)
    {
      yychar = yytoken = YYEOF;
      YYDPRINTF ((stderr, "Now at end of input.\n"));
    }
  else
    {
      yytoken = YYTRANSLATE (yychar);
      YY_SYMBOL_PRINT ("Next token is", yytoken, &yylval, &yylloc);
    }

  /* If the proper action on seeing token YYTOKEN is to reduce or to
     detect an error, take that action.  */
  yyn += yytoken;
  if (yyn < 0 || YYLAST < yyn || yycheck[yyn] != yytoken)
    goto yydefault;
  yyn = yytable[yyn];
  if (yyn <= 0)
    {
      if (yyn == 0 || yyn == YYTABLE_NINF)
	goto yyerrlab;
      yyn = -yyn;
      goto yyreduce;
    }

  if (yyn == YYFINAL)
    YYACCEPT;

  /* Count tokens shifted since error; after three, turn off error
     status.  */
  if (yyerrstatus)
    yyerrstatus--;

  /* Shift the look-ahead token.  */
  YY_SYMBOL_PRINT ("Shifting", yytoken, &yylval, &yylloc);

  /* Discard the shifted token unless it is eof.  */
  if (yychar != YYEOF)
    yychar = YYEMPTY;

  yystate = yyn;
  *++yyvsp = yylval;

  goto yynewstate;


/*-----------------------------------------------------------.
| yydefault -- do the default action for the current state.  |
`-----------------------------------------------------------*/
yydefault:
  yyn = yydefact[yystate];
  if (yyn == 0)
    goto yyerrlab;
  goto yyreduce;


/*-----------------------------.
| yyreduce -- Do a reduction.  |
`-----------------------------*/
yyreduce:
  /* yyn is the number of a rule to reduce with.  */
  yylen = yyr2[yyn];

  /* If YYLEN is nonzero, implement the default value of the action:
     `$$ = $1'.

     Otherwise, the following line sets YYVAL to garbage.
     This behavior is undocumented and Bison
     users should not rely upon it.  Assigning to YYVAL
     unconditionally makes the parser a bit smaller, and it avoids a
     GCC warning that YYVAL may be used uninitialized.  */
  yyval = yyvsp[1-yylen];


  YY_REDUCE_PRINT (yyn);
  switch (yyn)
    {
        case 2:
#line 282 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 14 );
root= (yyval.t);

    ;}
    break;

  case 3:
#line 290 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 14 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);
root= (yyval.t);

    ;}
    break;

  case 4:
#line 302 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 3 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 5:
#line 313 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 3 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 6:
#line 330 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 131 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 7:
#line 341 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 155 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 8:
#line 352 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 155 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 9:
#line 363 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 155 );

        (yyval.t)->addChild((yyvsp[(1) - (5)].t));

        (yyvsp[(1) - (5)].t)->parent= (yyval.t);

        (yyvsp[(1) - (5)].t)->nextSibbling= (yyvsp[(2) - (5)].t);

        (yyval.t)->addChild((yyvsp[(2) - (5)].t));

        (yyvsp[(2) - (5)].t)->parent= (yyval.t);

        (yyvsp[(2) - (5)].t)->nextSibbling= (yyvsp[(3) - (5)].t);

        (yyval.t)->addChild((yyvsp[(3) - (5)].t));

        (yyvsp[(3) - (5)].t)->parent= (yyval.t);

        (yyvsp[(3) - (5)].t)->nextSibbling= (yyvsp[(4) - (5)].t);

        (yyval.t)->addChild((yyvsp[(4) - (5)].t));

        (yyvsp[(4) - (5)].t)->parent= (yyval.t);

        (yyvsp[(4) - (5)].t)->nextSibbling= (yyvsp[(5) - (5)].t);

        (yyval.t)->addChild((yyvsp[(5) - (5)].t));

        (yyvsp[(5) - (5)].t)->parent= (yyval.t);

    ;}
    break;

  case 10:
#line 398 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 155 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 11:
#line 415 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 22 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 12:
#line 438 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 22 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 13:
#line 467 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 22 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 14:
#line 496 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 22 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 15:
#line 513 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 22 );

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 16:
#line 524 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 22 );

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 17:
#line 535 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 22 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 18:
#line 546 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 96 );

        (yyval.t)->addChild((yyvsp[(1) - (6)].t));

        (yyvsp[(1) - (6)].t)->parent= (yyval.t);

        (yyvsp[(1) - (6)].t)->nextSibbling= (yyvsp[(2) - (6)].t);

        (yyval.t)->addChild((yyvsp[(2) - (6)].t));

        (yyvsp[(2) - (6)].t)->parent= (yyval.t);

        (yyvsp[(2) - (6)].t)->nextSibbling= (yyvsp[(3) - (6)].t);

        (yyval.t)->addChild((yyvsp[(3) - (6)].t));

        (yyvsp[(3) - (6)].t)->parent= (yyval.t);

        (yyvsp[(3) - (6)].t)->nextSibbling= (yyvsp[(4) - (6)].t);

        (yyval.t)->addChild((yyvsp[(4) - (6)].t));

        (yyvsp[(4) - (6)].t)->parent= (yyval.t);

        (yyvsp[(4) - (6)].t)->nextSibbling= (yyvsp[(5) - (6)].t);

        (yyval.t)->addChild((yyvsp[(5) - (6)].t));

        (yyvsp[(5) - (6)].t)->parent= (yyval.t);

        (yyvsp[(5) - (6)].t)->nextSibbling= (yyvsp[(6) - (6)].t);

        (yyval.t)->addChild((yyvsp[(6) - (6)].t));

        (yyvsp[(6) - (6)].t)->parent= (yyval.t);

    ;}
    break;

  case 19:
#line 587 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 96 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 20:
#line 610 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 96 );

        (yyval.t)->addChild((yyvsp[(1) - (6)].t));

        (yyvsp[(1) - (6)].t)->parent= (yyval.t);

        (yyvsp[(1) - (6)].t)->nextSibbling= (yyvsp[(2) - (6)].t);

        (yyval.t)->addChild((yyvsp[(2) - (6)].t));

        (yyvsp[(2) - (6)].t)->parent= (yyval.t);

        (yyvsp[(2) - (6)].t)->nextSibbling= (yyvsp[(3) - (6)].t);

        (yyval.t)->addChild((yyvsp[(3) - (6)].t));

        (yyvsp[(3) - (6)].t)->parent= (yyval.t);

        (yyvsp[(3) - (6)].t)->nextSibbling= (yyvsp[(4) - (6)].t);

        (yyval.t)->addChild((yyvsp[(4) - (6)].t));

        (yyvsp[(4) - (6)].t)->parent= (yyval.t);

        (yyvsp[(4) - (6)].t)->nextSibbling= (yyvsp[(5) - (6)].t);

        (yyval.t)->addChild((yyvsp[(5) - (6)].t));

        (yyvsp[(5) - (6)].t)->parent= (yyval.t);

        (yyvsp[(5) - (6)].t)->nextSibbling= (yyvsp[(6) - (6)].t);

        (yyval.t)->addChild((yyvsp[(6) - (6)].t));

        (yyvsp[(6) - (6)].t)->parent= (yyval.t);

    ;}
    break;

  case 21:
#line 651 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 96 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 22:
#line 674 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 96 );

        (yyval.t)->addChild((yyvsp[(1) - (5)].t));

        (yyvsp[(1) - (5)].t)->parent= (yyval.t);

        (yyvsp[(1) - (5)].t)->nextSibbling= (yyvsp[(2) - (5)].t);

        (yyval.t)->addChild((yyvsp[(2) - (5)].t));

        (yyvsp[(2) - (5)].t)->parent= (yyval.t);

        (yyvsp[(2) - (5)].t)->nextSibbling= (yyvsp[(3) - (5)].t);

        (yyval.t)->addChild((yyvsp[(3) - (5)].t));

        (yyvsp[(3) - (5)].t)->parent= (yyval.t);

        (yyvsp[(3) - (5)].t)->nextSibbling= (yyvsp[(4) - (5)].t);

        (yyval.t)->addChild((yyvsp[(4) - (5)].t));

        (yyvsp[(4) - (5)].t)->parent= (yyval.t);

        (yyvsp[(4) - (5)].t)->nextSibbling= (yyvsp[(5) - (5)].t);

        (yyval.t)->addChild((yyvsp[(5) - (5)].t));

        (yyvsp[(5) - (5)].t)->parent= (yyval.t);

    ;}
    break;

  case 23:
#line 709 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 96 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 24:
#line 726 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 39 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 25:
#line 737 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 39 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 26:
#line 748 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 121 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 27:
#line 759 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 121 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 28:
#line 770 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 121 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 29:
#line 781 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 121 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 30:
#line 792 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 121 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 31:
#line 803 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 121 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 32:
#line 814 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 121 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 33:
#line 825 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 112 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 34:
#line 836 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 109 );

    ;}
    break;

  case 35:
#line 843 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 109 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 36:
#line 854 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 16 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 37:
#line 865 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 16 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 38:
#line 888 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 83 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 39:
#line 899 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 83 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 40:
#line 916 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 83 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 41:
#line 933 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 83 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 42:
#line 950 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 83 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 43:
#line 967 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 83 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 44:
#line 984 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 83 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 45:
#line 1013 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 83 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 46:
#line 1030 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 83 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 47:
#line 1059 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 83 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 48:
#line 1076 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 83 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 49:
#line 1093 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 47 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 50:
#line 1104 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 140 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 51:
#line 1115 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 62 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 52:
#line 1126 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 134 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 53:
#line 1137 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 134 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 54:
#line 1166 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 128 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 55:
#line 1177 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 128 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 56:
#line 1200 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 128 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 57:
#line 1223 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 128 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 58:
#line 1246 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 128 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 59:
#line 1269 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 128 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 60:
#line 1292 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 128 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 61:
#line 1315 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 128 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 62:
#line 1338 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 128 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 63:
#line 1361 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 128 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 64:
#line 1384 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 128 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 65:
#line 1407 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 128 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 66:
#line 1430 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 128 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 67:
#line 1453 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 128 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 68:
#line 1476 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 128 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 69:
#line 1499 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 128 );

        (yyval.t)->addChild((yyvsp[(1) - (5)].t));

        (yyvsp[(1) - (5)].t)->parent= (yyval.t);

        (yyvsp[(1) - (5)].t)->nextSibbling= (yyvsp[(2) - (5)].t);

        (yyval.t)->addChild((yyvsp[(2) - (5)].t));

        (yyvsp[(2) - (5)].t)->parent= (yyval.t);

        (yyvsp[(2) - (5)].t)->nextSibbling= (yyvsp[(3) - (5)].t);

        (yyval.t)->addChild((yyvsp[(3) - (5)].t));

        (yyvsp[(3) - (5)].t)->parent= (yyval.t);

        (yyvsp[(3) - (5)].t)->nextSibbling= (yyvsp[(4) - (5)].t);

        (yyval.t)->addChild((yyvsp[(4) - (5)].t));

        (yyvsp[(4) - (5)].t)->parent= (yyval.t);

        (yyvsp[(4) - (5)].t)->nextSibbling= (yyvsp[(5) - (5)].t);

        (yyval.t)->addChild((yyvsp[(5) - (5)].t));

        (yyvsp[(5) - (5)].t)->parent= (yyval.t);

    ;}
    break;

  case 70:
#line 1534 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 128 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 71:
#line 1563 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 128 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 72:
#line 1586 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 128 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 73:
#line 1609 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 84 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 74:
#line 1620 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 84 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 75:
#line 1637 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 84 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 76:
#line 1660 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 84 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 77:
#line 1677 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 84 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 78:
#line 1688 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 84 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 79:
#line 1699 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 84 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 80:
#line 1710 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 84 );

        (yyval.t)->addChild((yyvsp[(1) - (6)].t));

        (yyvsp[(1) - (6)].t)->parent= (yyval.t);

        (yyvsp[(1) - (6)].t)->nextSibbling= (yyvsp[(2) - (6)].t);

        (yyval.t)->addChild((yyvsp[(2) - (6)].t));

        (yyvsp[(2) - (6)].t)->parent= (yyval.t);

        (yyvsp[(2) - (6)].t)->nextSibbling= (yyvsp[(3) - (6)].t);

        (yyval.t)->addChild((yyvsp[(3) - (6)].t));

        (yyvsp[(3) - (6)].t)->parent= (yyval.t);

        (yyvsp[(3) - (6)].t)->nextSibbling= (yyvsp[(4) - (6)].t);

        (yyval.t)->addChild((yyvsp[(4) - (6)].t));

        (yyvsp[(4) - (6)].t)->parent= (yyval.t);

        (yyvsp[(4) - (6)].t)->nextSibbling= (yyvsp[(5) - (6)].t);

        (yyval.t)->addChild((yyvsp[(5) - (6)].t));

        (yyvsp[(5) - (6)].t)->parent= (yyval.t);

        (yyvsp[(5) - (6)].t)->nextSibbling= (yyvsp[(6) - (6)].t);

        (yyval.t)->addChild((yyvsp[(6) - (6)].t));

        (yyvsp[(6) - (6)].t)->parent= (yyval.t);

    ;}
    break;

  case 81:
#line 1751 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 84 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 82:
#line 1774 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 84 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 83:
#line 1791 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 84 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 84:
#line 1814 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 84 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 85:
#line 1831 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 84 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 86:
#line 1860 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 84 );

        (yyval.t)->addChild((yyvsp[(1) - (6)].t));

        (yyvsp[(1) - (6)].t)->parent= (yyval.t);

        (yyvsp[(1) - (6)].t)->nextSibbling= (yyvsp[(2) - (6)].t);

        (yyval.t)->addChild((yyvsp[(2) - (6)].t));

        (yyvsp[(2) - (6)].t)->parent= (yyval.t);

        (yyvsp[(2) - (6)].t)->nextSibbling= (yyvsp[(3) - (6)].t);

        (yyval.t)->addChild((yyvsp[(3) - (6)].t));

        (yyvsp[(3) - (6)].t)->parent= (yyval.t);

        (yyvsp[(3) - (6)].t)->nextSibbling= (yyvsp[(4) - (6)].t);

        (yyval.t)->addChild((yyvsp[(4) - (6)].t));

        (yyvsp[(4) - (6)].t)->parent= (yyval.t);

        (yyvsp[(4) - (6)].t)->nextSibbling= (yyvsp[(5) - (6)].t);

        (yyval.t)->addChild((yyvsp[(5) - (6)].t));

        (yyvsp[(5) - (6)].t)->parent= (yyval.t);

        (yyvsp[(5) - (6)].t)->nextSibbling= (yyvsp[(6) - (6)].t);

        (yyval.t)->addChild((yyvsp[(6) - (6)].t));

        (yyvsp[(6) - (6)].t)->parent= (yyval.t);

    ;}
    break;

  case 87:
#line 1901 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 84 );

        (yyval.t)->addChild((yyvsp[(1) - (8)].t));

        (yyvsp[(1) - (8)].t)->parent= (yyval.t);

        (yyvsp[(1) - (8)].t)->nextSibbling= (yyvsp[(2) - (8)].t);

        (yyval.t)->addChild((yyvsp[(2) - (8)].t));

        (yyvsp[(2) - (8)].t)->parent= (yyval.t);

        (yyvsp[(2) - (8)].t)->nextSibbling= (yyvsp[(3) - (8)].t);

        (yyval.t)->addChild((yyvsp[(3) - (8)].t));

        (yyvsp[(3) - (8)].t)->parent= (yyval.t);

        (yyvsp[(3) - (8)].t)->nextSibbling= (yyvsp[(4) - (8)].t);

        (yyval.t)->addChild((yyvsp[(4) - (8)].t));

        (yyvsp[(4) - (8)].t)->parent= (yyval.t);

        (yyvsp[(4) - (8)].t)->nextSibbling= (yyvsp[(5) - (8)].t);

        (yyval.t)->addChild((yyvsp[(5) - (8)].t));

        (yyvsp[(5) - (8)].t)->parent= (yyval.t);

        (yyvsp[(5) - (8)].t)->nextSibbling= (yyvsp[(6) - (8)].t);

        (yyval.t)->addChild((yyvsp[(6) - (8)].t));

        (yyvsp[(6) - (8)].t)->parent= (yyval.t);

        (yyvsp[(6) - (8)].t)->nextSibbling= (yyvsp[(7) - (8)].t);

        (yyval.t)->addChild((yyvsp[(7) - (8)].t));

        (yyvsp[(7) - (8)].t)->parent= (yyval.t);

        (yyvsp[(7) - (8)].t)->nextSibbling= (yyvsp[(8) - (8)].t);

        (yyval.t)->addChild((yyvsp[(8) - (8)].t));

        (yyvsp[(8) - (8)].t)->parent= (yyval.t);

    ;}
    break;

  case 88:
#line 1954 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 84 );

        (yyval.t)->addChild((yyvsp[(1) - (6)].t));

        (yyvsp[(1) - (6)].t)->parent= (yyval.t);

        (yyvsp[(1) - (6)].t)->nextSibbling= (yyvsp[(2) - (6)].t);

        (yyval.t)->addChild((yyvsp[(2) - (6)].t));

        (yyvsp[(2) - (6)].t)->parent= (yyval.t);

        (yyvsp[(2) - (6)].t)->nextSibbling= (yyvsp[(3) - (6)].t);

        (yyval.t)->addChild((yyvsp[(3) - (6)].t));

        (yyvsp[(3) - (6)].t)->parent= (yyval.t);

        (yyvsp[(3) - (6)].t)->nextSibbling= (yyvsp[(4) - (6)].t);

        (yyval.t)->addChild((yyvsp[(4) - (6)].t));

        (yyvsp[(4) - (6)].t)->parent= (yyval.t);

        (yyvsp[(4) - (6)].t)->nextSibbling= (yyvsp[(5) - (6)].t);

        (yyval.t)->addChild((yyvsp[(5) - (6)].t));

        (yyvsp[(5) - (6)].t)->parent= (yyval.t);

        (yyvsp[(5) - (6)].t)->nextSibbling= (yyvsp[(6) - (6)].t);

        (yyval.t)->addChild((yyvsp[(6) - (6)].t));

        (yyvsp[(6) - (6)].t)->parent= (yyval.t);

    ;}
    break;

  case 89:
#line 1995 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 84 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 90:
#line 2024 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 84 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 91:
#line 2047 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 84 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 92:
#line 2070 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 84 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 93:
#line 2087 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 84 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 94:
#line 2104 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 42 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 95:
#line 2115 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 102 );

    ;}
    break;

  case 96:
#line 2122 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 102 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 97:
#line 2133 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 104 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 98:
#line 2150 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 120 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 99:
#line 2161 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 120 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 100:
#line 2172 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 120 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 101:
#line 2189 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 120 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 102:
#line 2206 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 50 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 103:
#line 2235 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 50 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 104:
#line 2264 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 50 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 105:
#line 2281 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 50 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 106:
#line 2298 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 25 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 107:
#line 2315 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 125 );

    ;}
    break;

  case 108:
#line 2322 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 26 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 109:
#line 2333 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 0 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 110:
#line 2362 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 0 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 111:
#line 2391 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 0 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 112:
#line 2414 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 0 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 113:
#line 2437 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 0 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 114:
#line 2454 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 0 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 115:
#line 2471 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 32 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 116:
#line 2482 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 32 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 117:
#line 2499 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 32 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 118:
#line 2516 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 10 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 119:
#line 2533 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 150 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 120:
#line 2550 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 150 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 121:
#line 2567 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 100 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 122:
#line 2578 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 100 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 123:
#line 2595 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 151 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 124:
#line 2606 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 151 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 125:
#line 2623 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 151 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 126:
#line 2640 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 151 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 127:
#line 2657 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 151 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 128:
#line 2674 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 151 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 129:
#line 2691 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 151 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 130:
#line 2708 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 78 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 131:
#line 2719 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 78 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 132:
#line 2736 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 78 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 133:
#line 2753 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 78 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 134:
#line 2770 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 78 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 135:
#line 2787 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 78 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 136:
#line 2804 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 76 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 137:
#line 2821 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 76 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 138:
#line 2838 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 76 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 139:
#line 2855 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 76 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 140:
#line 2872 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 76 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 141:
#line 2889 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 76 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 142:
#line 2906 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 11 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 143:
#line 2923 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 11 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 144:
#line 2940 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 11 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 145:
#line 2957 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 11 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 146:
#line 2974 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 11 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 147:
#line 2991 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 146 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 148:
#line 3002 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 146 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 149:
#line 3019 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 146 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 150:
#line 3036 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 146 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 151:
#line 3053 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 146 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 152:
#line 3070 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 146 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 153:
#line 3087 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 146 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 154:
#line 3104 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 37 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 155:
#line 3121 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 1 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 156:
#line 3138 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 1 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 157:
#line 3155 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 1 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 158:
#line 3172 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 1 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 159:
#line 3189 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 1 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 160:
#line 3206 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 1 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 161:
#line 3223 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 4 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 162:
#line 3240 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 2 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 163:
#line 3257 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 2 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 164:
#line 3274 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 2 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 165:
#line 3291 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 2 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 166:
#line 3308 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 2 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 167:
#line 3325 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 2 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 168:
#line 3342 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 2 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 169:
#line 3359 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 2 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 170:
#line 3376 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 2 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 171:
#line 3393 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 2 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 172:
#line 3410 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 94 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 173:
#line 3427 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 94 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 174:
#line 3444 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 94 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 175:
#line 3461 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 94 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 176:
#line 3478 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 94 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 177:
#line 3495 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 98 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 178:
#line 3512 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 98 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 179:
#line 3529 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 98 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 180:
#line 3546 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 98 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 181:
#line 3563 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 98 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 182:
#line 3580 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 98 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 183:
#line 3597 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 98 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 184:
#line 3614 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 98 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 185:
#line 3631 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 98 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 186:
#line 3648 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 98 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 187:
#line 3665 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 27 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 188:
#line 3682 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 27 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 189:
#line 3699 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 27 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 190:
#line 3716 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 27 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 191:
#line 3733 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 27 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 192:
#line 3750 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 20 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 193:
#line 3761 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 20 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 194:
#line 3772 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 20 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 195:
#line 3783 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 20 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 196:
#line 3794 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 20 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 197:
#line 3805 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 20 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 198:
#line 3816 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 20 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 199:
#line 3827 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 20 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 200:
#line 3838 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 75 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 201:
#line 3849 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 75 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 202:
#line 3860 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 75 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 203:
#line 3871 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 75 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 204:
#line 3882 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 75 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 205:
#line 3893 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 75 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 206:
#line 3904 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 75 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 207:
#line 3915 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 75 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 208:
#line 3926 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 70 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 209:
#line 3937 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 70 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 210:
#line 3948 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 70 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 211:
#line 3959 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 70 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 212:
#line 3970 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 145 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 213:
#line 3981 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 145 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 214:
#line 3992 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 145 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 215:
#line 4003 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 145 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 216:
#line 4014 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 119 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 217:
#line 4025 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 119 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 218:
#line 4036 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 119 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 219:
#line 4047 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 119 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 220:
#line 4058 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 86 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 221:
#line 4069 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 86 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 222:
#line 4080 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 86 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 223:
#line 4091 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 86 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 224:
#line 4102 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 93 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 225:
#line 4113 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 93 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 226:
#line 4124 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 93 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 227:
#line 4135 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 93 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 228:
#line 4146 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 93 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 229:
#line 4157 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 93 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 230:
#line 4168 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 93 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 231:
#line 4179 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 93 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 232:
#line 4190 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 67 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 233:
#line 4201 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 67 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 234:
#line 4212 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 67 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 235:
#line 4223 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 67 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 236:
#line 4234 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 67 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 237:
#line 4245 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 67 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 238:
#line 4256 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 67 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 239:
#line 4267 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 67 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 240:
#line 4278 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 67 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 241:
#line 4289 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 67 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 242:
#line 4300 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 67 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 243:
#line 4311 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 67 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 244:
#line 4322 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 67 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 245:
#line 4333 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 67 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 246:
#line 4344 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 67 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 247:
#line 4355 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 67 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 248:
#line 4366 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 68 );

    ;}
    break;

  case 249:
#line 4373 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 68 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 250:
#line 4384 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 28 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 251:
#line 4395 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 28 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 252:
#line 4406 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 31 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 253:
#line 4417 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 106 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 254:
#line 4428 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 106 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 255:
#line 4439 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 44 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 256:
#line 4450 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 53 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 257:
#line 4461 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 53 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 258:
#line 4490 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 53 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 259:
#line 4519 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 82 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 260:
#line 4530 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 82 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 261:
#line 4559 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 153 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 262:
#line 4570 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 153 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 263:
#line 4599 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 114 );

    ;}
    break;

  case 264:
#line 4606 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 114 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 265:
#line 4635 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 59 );

        (yyval.t)->addChild((yyvsp[(1) - (5)].t));

        (yyvsp[(1) - (5)].t)->parent= (yyval.t);

        (yyvsp[(1) - (5)].t)->nextSibbling= (yyvsp[(2) - (5)].t);

        (yyval.t)->addChild((yyvsp[(2) - (5)].t));

        (yyvsp[(2) - (5)].t)->parent= (yyval.t);

        (yyvsp[(2) - (5)].t)->nextSibbling= (yyvsp[(3) - (5)].t);

        (yyval.t)->addChild((yyvsp[(3) - (5)].t));

        (yyvsp[(3) - (5)].t)->parent= (yyval.t);

        (yyvsp[(3) - (5)].t)->nextSibbling= (yyvsp[(4) - (5)].t);

        (yyval.t)->addChild((yyvsp[(4) - (5)].t));

        (yyvsp[(4) - (5)].t)->parent= (yyval.t);

        (yyvsp[(4) - (5)].t)->nextSibbling= (yyvsp[(5) - (5)].t);

        (yyval.t)->addChild((yyvsp[(5) - (5)].t));

        (yyvsp[(5) - (5)].t)->parent= (yyval.t);

    ;}
    break;

  case 266:
#line 4670 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 59 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 267:
#line 4693 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 79 );

        (yyval.t)->addChild((yyvsp[(1) - (5)].t));

        (yyvsp[(1) - (5)].t)->parent= (yyval.t);

        (yyvsp[(1) - (5)].t)->nextSibbling= (yyvsp[(2) - (5)].t);

        (yyval.t)->addChild((yyvsp[(2) - (5)].t));

        (yyvsp[(2) - (5)].t)->parent= (yyval.t);

        (yyvsp[(2) - (5)].t)->nextSibbling= (yyvsp[(3) - (5)].t);

        (yyval.t)->addChild((yyvsp[(3) - (5)].t));

        (yyvsp[(3) - (5)].t)->parent= (yyval.t);

        (yyvsp[(3) - (5)].t)->nextSibbling= (yyvsp[(4) - (5)].t);

        (yyval.t)->addChild((yyvsp[(4) - (5)].t));

        (yyvsp[(4) - (5)].t)->parent= (yyval.t);

        (yyvsp[(4) - (5)].t)->nextSibbling= (yyvsp[(5) - (5)].t);

        (yyval.t)->addChild((yyvsp[(5) - (5)].t));

        (yyvsp[(5) - (5)].t)->parent= (yyval.t);

    ;}
    break;

  case 268:
#line 4728 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 79 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 269:
#line 4751 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 74 );

    ;}
    break;

  case 270:
#line 4758 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 74 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 271:
#line 4769 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 8 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 272:
#line 4780 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 8 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 273:
#line 4797 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 124 );

        (yyval.t)->addChild((yyvsp[(1) - (6)].t));

        (yyvsp[(1) - (6)].t)->parent= (yyval.t);

        (yyvsp[(1) - (6)].t)->nextSibbling= (yyvsp[(2) - (6)].t);

        (yyval.t)->addChild((yyvsp[(2) - (6)].t));

        (yyvsp[(2) - (6)].t)->parent= (yyval.t);

        (yyvsp[(2) - (6)].t)->nextSibbling= (yyvsp[(3) - (6)].t);

        (yyval.t)->addChild((yyvsp[(3) - (6)].t));

        (yyvsp[(3) - (6)].t)->parent= (yyval.t);

        (yyvsp[(3) - (6)].t)->nextSibbling= (yyvsp[(4) - (6)].t);

        (yyval.t)->addChild((yyvsp[(4) - (6)].t));

        (yyvsp[(4) - (6)].t)->parent= (yyval.t);

        (yyvsp[(4) - (6)].t)->nextSibbling= (yyvsp[(5) - (6)].t);

        (yyval.t)->addChild((yyvsp[(5) - (6)].t));

        (yyvsp[(5) - (6)].t)->parent= (yyval.t);

        (yyvsp[(5) - (6)].t)->nextSibbling= (yyvsp[(6) - (6)].t);

        (yyval.t)->addChild((yyvsp[(6) - (6)].t));

        (yyvsp[(6) - (6)].t)->parent= (yyval.t);

    ;}
    break;

  case 274:
#line 4838 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 137 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 275:
#line 4849 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 137 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 276:
#line 4872 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 80 );

    ;}
    break;

  case 277:
#line 4879 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 80 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 278:
#line 4890 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 80 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 279:
#line 4919 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 80 );

        (yyval.t)->addChild((yyvsp[(1) - (6)].t));

        (yyvsp[(1) - (6)].t)->parent= (yyval.t);

        (yyvsp[(1) - (6)].t)->nextSibbling= (yyvsp[(2) - (6)].t);

        (yyval.t)->addChild((yyvsp[(2) - (6)].t));

        (yyvsp[(2) - (6)].t)->parent= (yyval.t);

        (yyvsp[(2) - (6)].t)->nextSibbling= (yyvsp[(3) - (6)].t);

        (yyval.t)->addChild((yyvsp[(3) - (6)].t));

        (yyvsp[(3) - (6)].t)->parent= (yyval.t);

        (yyvsp[(3) - (6)].t)->nextSibbling= (yyvsp[(4) - (6)].t);

        (yyval.t)->addChild((yyvsp[(4) - (6)].t));

        (yyvsp[(4) - (6)].t)->parent= (yyval.t);

        (yyvsp[(4) - (6)].t)->nextSibbling= (yyvsp[(5) - (6)].t);

        (yyval.t)->addChild((yyvsp[(5) - (6)].t));

        (yyvsp[(5) - (6)].t)->parent= (yyval.t);

        (yyvsp[(5) - (6)].t)->nextSibbling= (yyvsp[(6) - (6)].t);

        (yyval.t)->addChild((yyvsp[(6) - (6)].t));

        (yyvsp[(6) - (6)].t)->parent= (yyval.t);

    ;}
    break;

  case 280:
#line 4960 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 80 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 281:
#line 4989 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 129 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 282:
#line 5000 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 129 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 283:
#line 5011 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 129 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 284:
#line 5022 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 129 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 285:
#line 5033 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 107 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 286:
#line 5044 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 107 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 287:
#line 5055 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 13 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 288:
#line 5066 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 13 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 289:
#line 5089 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 13 );

    ;}
    break;

  case 290:
#line 5096 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 19 );

    ;}
    break;

  case 291:
#line 5103 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 19 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 292:
#line 5120 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 95 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 293:
#line 5131 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 95 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 294:
#line 5154 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 108 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 295:
#line 5177 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 108 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 296:
#line 5194 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 108 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 297:
#line 5217 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 108 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 298:
#line 5228 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 40 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 299:
#line 5251 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 40 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 300:
#line 5262 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 40 );

    ;}
    break;

  case 301:
#line 5269 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 38 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 302:
#line 5280 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 38 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 303:
#line 5297 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 142 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 304:
#line 5314 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 142 );

        (yyval.t)->addChild((yyvsp[(1) - (5)].t));

        (yyvsp[(1) - (5)].t)->parent= (yyval.t);

        (yyvsp[(1) - (5)].t)->nextSibbling= (yyvsp[(2) - (5)].t);

        (yyval.t)->addChild((yyvsp[(2) - (5)].t));

        (yyvsp[(2) - (5)].t)->parent= (yyval.t);

        (yyvsp[(2) - (5)].t)->nextSibbling= (yyvsp[(3) - (5)].t);

        (yyval.t)->addChild((yyvsp[(3) - (5)].t));

        (yyvsp[(3) - (5)].t)->parent= (yyval.t);

        (yyvsp[(3) - (5)].t)->nextSibbling= (yyvsp[(4) - (5)].t);

        (yyval.t)->addChild((yyvsp[(4) - (5)].t));

        (yyvsp[(4) - (5)].t)->parent= (yyval.t);

        (yyvsp[(4) - (5)].t)->nextSibbling= (yyvsp[(5) - (5)].t);

        (yyval.t)->addChild((yyvsp[(5) - (5)].t));

        (yyvsp[(5) - (5)].t)->parent= (yyval.t);

    ;}
    break;

  case 305:
#line 5349 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 142 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 306:
#line 5372 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 97 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 307:
#line 5401 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 126 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 308:
#line 5430 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 36 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 309:
#line 5441 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 36 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 310:
#line 5452 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 49 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 311:
#line 5481 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 49 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 312:
#line 5504 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 49 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 313:
#line 5521 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 49 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 314:
#line 5544 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 49 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 315:
#line 5555 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 156 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 316:
#line 5566 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 156 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 317:
#line 5577 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 136 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 318:
#line 5600 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 136 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 319:
#line 5617 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 136 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 320:
#line 5628 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 73 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 321:
#line 5651 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 73 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 322:
#line 5668 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 73 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 323:
#line 5691 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 73 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 324:
#line 5714 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 73 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 325:
#line 5743 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 34 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 326:
#line 5766 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 34 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 327:
#line 5795 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 34 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 328:
#line 5818 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 34 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 329:
#line 5835 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 34 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 330:
#line 5846 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 130 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 331:
#line 5857 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 130 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 332:
#line 5874 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 149 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 333:
#line 5885 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 149 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 334:
#line 5902 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 147 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 335:
#line 5913 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 147 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 336:
#line 5930 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 115 );

        (yyval.t)->addChild((yyvsp[(1) - (6)].t));

        (yyvsp[(1) - (6)].t)->parent= (yyval.t);

        (yyvsp[(1) - (6)].t)->nextSibbling= (yyvsp[(2) - (6)].t);

        (yyval.t)->addChild((yyvsp[(2) - (6)].t));

        (yyvsp[(2) - (6)].t)->parent= (yyval.t);

        (yyvsp[(2) - (6)].t)->nextSibbling= (yyvsp[(3) - (6)].t);

        (yyval.t)->addChild((yyvsp[(3) - (6)].t));

        (yyvsp[(3) - (6)].t)->parent= (yyval.t);

        (yyvsp[(3) - (6)].t)->nextSibbling= (yyvsp[(4) - (6)].t);

        (yyval.t)->addChild((yyvsp[(4) - (6)].t));

        (yyvsp[(4) - (6)].t)->parent= (yyval.t);

        (yyvsp[(4) - (6)].t)->nextSibbling= (yyvsp[(5) - (6)].t);

        (yyval.t)->addChild((yyvsp[(5) - (6)].t));

        (yyvsp[(5) - (6)].t)->parent= (yyval.t);

        (yyvsp[(5) - (6)].t)->nextSibbling= (yyvsp[(6) - (6)].t);

        (yyval.t)->addChild((yyvsp[(6) - (6)].t));

        (yyvsp[(6) - (6)].t)->parent= (yyval.t);

    ;}
    break;

  case 337:
#line 5971 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 115 );

        (yyval.t)->addChild((yyvsp[(1) - (5)].t));

        (yyvsp[(1) - (5)].t)->parent= (yyval.t);

        (yyvsp[(1) - (5)].t)->nextSibbling= (yyvsp[(2) - (5)].t);

        (yyval.t)->addChild((yyvsp[(2) - (5)].t));

        (yyvsp[(2) - (5)].t)->parent= (yyval.t);

        (yyvsp[(2) - (5)].t)->nextSibbling= (yyvsp[(3) - (5)].t);

        (yyval.t)->addChild((yyvsp[(3) - (5)].t));

        (yyvsp[(3) - (5)].t)->parent= (yyval.t);

        (yyvsp[(3) - (5)].t)->nextSibbling= (yyvsp[(4) - (5)].t);

        (yyval.t)->addChild((yyvsp[(4) - (5)].t));

        (yyvsp[(4) - (5)].t)->parent= (yyval.t);

        (yyvsp[(4) - (5)].t)->nextSibbling= (yyvsp[(5) - (5)].t);

        (yyval.t)->addChild((yyvsp[(5) - (5)].t));

        (yyvsp[(5) - (5)].t)->parent= (yyval.t);

    ;}
    break;

  case 338:
#line 6006 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 115 );

        (yyval.t)->addChild((yyvsp[(1) - (6)].t));

        (yyvsp[(1) - (6)].t)->parent= (yyval.t);

        (yyvsp[(1) - (6)].t)->nextSibbling= (yyvsp[(2) - (6)].t);

        (yyval.t)->addChild((yyvsp[(2) - (6)].t));

        (yyvsp[(2) - (6)].t)->parent= (yyval.t);

        (yyvsp[(2) - (6)].t)->nextSibbling= (yyvsp[(3) - (6)].t);

        (yyval.t)->addChild((yyvsp[(3) - (6)].t));

        (yyvsp[(3) - (6)].t)->parent= (yyval.t);

        (yyvsp[(3) - (6)].t)->nextSibbling= (yyvsp[(4) - (6)].t);

        (yyval.t)->addChild((yyvsp[(4) - (6)].t));

        (yyvsp[(4) - (6)].t)->parent= (yyval.t);

        (yyvsp[(4) - (6)].t)->nextSibbling= (yyvsp[(5) - (6)].t);

        (yyval.t)->addChild((yyvsp[(5) - (6)].t));

        (yyvsp[(5) - (6)].t)->parent= (yyval.t);

        (yyvsp[(5) - (6)].t)->nextSibbling= (yyvsp[(6) - (6)].t);

        (yyval.t)->addChild((yyvsp[(6) - (6)].t));

        (yyvsp[(6) - (6)].t)->parent= (yyval.t);

    ;}
    break;

  case 339:
#line 6047 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 115 );

        (yyval.t)->addChild((yyvsp[(1) - (5)].t));

        (yyvsp[(1) - (5)].t)->parent= (yyval.t);

        (yyvsp[(1) - (5)].t)->nextSibbling= (yyvsp[(2) - (5)].t);

        (yyval.t)->addChild((yyvsp[(2) - (5)].t));

        (yyvsp[(2) - (5)].t)->parent= (yyval.t);

        (yyvsp[(2) - (5)].t)->nextSibbling= (yyvsp[(3) - (5)].t);

        (yyval.t)->addChild((yyvsp[(3) - (5)].t));

        (yyvsp[(3) - (5)].t)->parent= (yyval.t);

        (yyvsp[(3) - (5)].t)->nextSibbling= (yyvsp[(4) - (5)].t);

        (yyval.t)->addChild((yyvsp[(4) - (5)].t));

        (yyvsp[(4) - (5)].t)->parent= (yyval.t);

        (yyvsp[(4) - (5)].t)->nextSibbling= (yyvsp[(5) - (5)].t);

        (yyval.t)->addChild((yyvsp[(5) - (5)].t));

        (yyvsp[(5) - (5)].t)->parent= (yyval.t);

    ;}
    break;

  case 340:
#line 6082 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 115 );

        (yyval.t)->addChild((yyvsp[(1) - (7)].t));

        (yyvsp[(1) - (7)].t)->parent= (yyval.t);

        (yyvsp[(1) - (7)].t)->nextSibbling= (yyvsp[(2) - (7)].t);

        (yyval.t)->addChild((yyvsp[(2) - (7)].t));

        (yyvsp[(2) - (7)].t)->parent= (yyval.t);

        (yyvsp[(2) - (7)].t)->nextSibbling= (yyvsp[(3) - (7)].t);

        (yyval.t)->addChild((yyvsp[(3) - (7)].t));

        (yyvsp[(3) - (7)].t)->parent= (yyval.t);

        (yyvsp[(3) - (7)].t)->nextSibbling= (yyvsp[(4) - (7)].t);

        (yyval.t)->addChild((yyvsp[(4) - (7)].t));

        (yyvsp[(4) - (7)].t)->parent= (yyval.t);

        (yyvsp[(4) - (7)].t)->nextSibbling= (yyvsp[(5) - (7)].t);

        (yyval.t)->addChild((yyvsp[(5) - (7)].t));

        (yyvsp[(5) - (7)].t)->parent= (yyval.t);

        (yyvsp[(5) - (7)].t)->nextSibbling= (yyvsp[(6) - (7)].t);

        (yyval.t)->addChild((yyvsp[(6) - (7)].t));

        (yyvsp[(6) - (7)].t)->parent= (yyval.t);

        (yyvsp[(6) - (7)].t)->nextSibbling= (yyvsp[(7) - (7)].t);

        (yyval.t)->addChild((yyvsp[(7) - (7)].t));

        (yyvsp[(7) - (7)].t)->parent= (yyval.t);

    ;}
    break;

  case 341:
#line 6129 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 115 );

        (yyval.t)->addChild((yyvsp[(1) - (6)].t));

        (yyvsp[(1) - (6)].t)->parent= (yyval.t);

        (yyvsp[(1) - (6)].t)->nextSibbling= (yyvsp[(2) - (6)].t);

        (yyval.t)->addChild((yyvsp[(2) - (6)].t));

        (yyvsp[(2) - (6)].t)->parent= (yyval.t);

        (yyvsp[(2) - (6)].t)->nextSibbling= (yyvsp[(3) - (6)].t);

        (yyval.t)->addChild((yyvsp[(3) - (6)].t));

        (yyvsp[(3) - (6)].t)->parent= (yyval.t);

        (yyvsp[(3) - (6)].t)->nextSibbling= (yyvsp[(4) - (6)].t);

        (yyval.t)->addChild((yyvsp[(4) - (6)].t));

        (yyvsp[(4) - (6)].t)->parent= (yyval.t);

        (yyvsp[(4) - (6)].t)->nextSibbling= (yyvsp[(5) - (6)].t);

        (yyval.t)->addChild((yyvsp[(5) - (6)].t));

        (yyvsp[(5) - (6)].t)->parent= (yyval.t);

        (yyvsp[(5) - (6)].t)->nextSibbling= (yyvsp[(6) - (6)].t);

        (yyval.t)->addChild((yyvsp[(6) - (6)].t));

        (yyvsp[(6) - (6)].t)->parent= (yyval.t);

    ;}
    break;

  case 342:
#line 6170 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 46 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 343:
#line 6187 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 46 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 344:
#line 6204 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 46 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 345:
#line 6221 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 113 );

    ;}
    break;

  case 346:
#line 6228 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 113 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 347:
#line 6239 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 144 );

    ;}
    break;

  case 348:
#line 6246 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 144 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 349:
#line 6257 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 9 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 350:
#line 6268 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 9 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 351:
#line 6285 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 23 );

    ;}
    break;

  case 352:
#line 6292 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 23 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 353:
#line 6315 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 23 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 354:
#line 6332 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 88 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 355:
#line 6355 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 88 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 356:
#line 6372 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 88 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 357:
#line 6395 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 88 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 358:
#line 6406 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 88 );

    ;}
    break;

  case 359:
#line 6413 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 88 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 360:
#line 6430 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 154 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 361:
#line 6441 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 154 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 362:
#line 6470 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 92 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 363:
#line 6481 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 92 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 364:
#line 6510 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 60 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 365:
#line 6527 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 60 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 366:
#line 6556 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 60 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 367:
#line 6579 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 56 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 368:
#line 6596 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 56 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 369:
#line 6625 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 56 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 370:
#line 6648 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 148 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 371:
#line 6659 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 148 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 372:
#line 6682 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 148 );

    ;}
    break;

  case 373:
#line 6689 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 24 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 374:
#line 6700 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 24 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 375:
#line 6723 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 52 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 376:
#line 6740 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 7 );

    ;}
    break;

  case 377:
#line 6747 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 7 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 378:
#line 6758 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 61 );

    ;}
    break;

  case 379:
#line 6765 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 61 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 380:
#line 6776 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 61 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 381:
#line 6793 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 48 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 382:
#line 6804 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 48 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 383:
#line 6815 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 43 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 384:
#line 6826 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 43 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 385:
#line 6849 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 91 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 386:
#line 6866 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 91 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 387:
#line 6889 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 77 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 388:
#line 6918 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 77 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 389:
#line 6941 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 77 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 390:
#line 6958 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 77 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 391:
#line 6975 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 77 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 392:
#line 6986 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 15 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 393:
#line 7015 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 15 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 394:
#line 7038 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 15 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 395:
#line 7067 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 15 );

        (yyval.t)->addChild((yyvsp[(1) - (5)].t));

        (yyvsp[(1) - (5)].t)->parent= (yyval.t);

        (yyvsp[(1) - (5)].t)->nextSibbling= (yyvsp[(2) - (5)].t);

        (yyval.t)->addChild((yyvsp[(2) - (5)].t));

        (yyvsp[(2) - (5)].t)->parent= (yyval.t);

        (yyvsp[(2) - (5)].t)->nextSibbling= (yyvsp[(3) - (5)].t);

        (yyval.t)->addChild((yyvsp[(3) - (5)].t));

        (yyvsp[(3) - (5)].t)->parent= (yyval.t);

        (yyvsp[(3) - (5)].t)->nextSibbling= (yyvsp[(4) - (5)].t);

        (yyval.t)->addChild((yyvsp[(4) - (5)].t));

        (yyvsp[(4) - (5)].t)->parent= (yyval.t);

        (yyvsp[(4) - (5)].t)->nextSibbling= (yyvsp[(5) - (5)].t);

        (yyval.t)->addChild((yyvsp[(5) - (5)].t));

        (yyvsp[(5) - (5)].t)->parent= (yyval.t);

    ;}
    break;

  case 396:
#line 7102 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 15 );

        (yyval.t)->addChild((yyvsp[(1) - (5)].t));

        (yyvsp[(1) - (5)].t)->parent= (yyval.t);

        (yyvsp[(1) - (5)].t)->nextSibbling= (yyvsp[(2) - (5)].t);

        (yyval.t)->addChild((yyvsp[(2) - (5)].t));

        (yyvsp[(2) - (5)].t)->parent= (yyval.t);

        (yyvsp[(2) - (5)].t)->nextSibbling= (yyvsp[(3) - (5)].t);

        (yyval.t)->addChild((yyvsp[(3) - (5)].t));

        (yyvsp[(3) - (5)].t)->parent= (yyval.t);

        (yyvsp[(3) - (5)].t)->nextSibbling= (yyvsp[(4) - (5)].t);

        (yyval.t)->addChild((yyvsp[(4) - (5)].t));

        (yyvsp[(4) - (5)].t)->parent= (yyval.t);

        (yyvsp[(4) - (5)].t)->nextSibbling= (yyvsp[(5) - (5)].t);

        (yyval.t)->addChild((yyvsp[(5) - (5)].t));

        (yyvsp[(5) - (5)].t)->parent= (yyval.t);

    ;}
    break;

  case 397:
#line 7137 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 6 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 398:
#line 7148 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 6 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 399:
#line 7159 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 6 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 400:
#line 7170 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 6 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 401:
#line 7181 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 71 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 402:
#line 7192 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 71 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 403:
#line 7209 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 71 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 404:
#line 7226 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 71 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 405:
#line 7243 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 71 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 406:
#line 7260 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 51 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 407:
#line 7271 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 51 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 408:
#line 7288 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 51 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 409:
#line 7305 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 51 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 410:
#line 7322 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 58 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 411:
#line 7333 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 58 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 412:
#line 7350 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 58 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 413:
#line 7367 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 58 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 414:
#line 7384 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 58 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 415:
#line 7401 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 17 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 416:
#line 7412 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 17 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 417:
#line 7429 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 110 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 418:
#line 7440 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 110 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 419:
#line 7451 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 110 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 420:
#line 7462 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 110 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 421:
#line 7473 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 85 );

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 422:
#line 7484 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 55 );

    ;}
    break;

  case 423:
#line 7491 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 123 );

    ;}
    break;

  case 424:
#line 7498 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 87 );

    ;}
    break;

  case 425:
#line 7505 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 87 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 426:
#line 7516 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 135 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 427:
#line 7527 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 135 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 428:
#line 7544 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 118 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 429:
#line 7567 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 69 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 430:
#line 7578 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 69 );

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 431:
#line 7589 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 116 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 432:
#line 7600 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 141 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 433:
#line 7611 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 141 );

        (yyval.t)->addChild((yyvsp[(1) - (5)].t));

        (yyvsp[(1) - (5)].t)->parent= (yyval.t);

        (yyvsp[(1) - (5)].t)->nextSibbling= (yyvsp[(2) - (5)].t);

        (yyval.t)->addChild((yyvsp[(2) - (5)].t));

        (yyvsp[(2) - (5)].t)->parent= (yyval.t);

        (yyvsp[(2) - (5)].t)->nextSibbling= (yyvsp[(3) - (5)].t);

        (yyval.t)->addChild((yyvsp[(3) - (5)].t));

        (yyvsp[(3) - (5)].t)->parent= (yyval.t);

        (yyvsp[(3) - (5)].t)->nextSibbling= (yyvsp[(4) - (5)].t);

        (yyval.t)->addChild((yyvsp[(4) - (5)].t));

        (yyvsp[(4) - (5)].t)->parent= (yyval.t);

        (yyvsp[(4) - (5)].t)->nextSibbling= (yyvsp[(5) - (5)].t);

        (yyval.t)->addChild((yyvsp[(5) - (5)].t));

        (yyvsp[(5) - (5)].t)->parent= (yyval.t);

    ;}
    break;

  case 434:
#line 7646 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 45 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 435:
#line 7657 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 45 );

    ;}
    break;

  case 436:
#line 7664 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 41 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 437:
#line 7681 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 72 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 438:
#line 7698 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 103 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 439:
#line 7715 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 103 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 440:
#line 7726 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 35 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 441:
#line 7755 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 89 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 442:
#line 7778 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 105 );

    ;}
    break;

  case 443:
#line 7785 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 66 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 444:
#line 7796 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 66 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 445:
#line 7813 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 33 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 446:
#line 7824 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 99 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 447:
#line 7841 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 127 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 448:
#line 7858 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 30 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 449:
#line 7881 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 30 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 450:
#line 7892 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 30 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 451:
#line 7909 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 30 );

        (yyval.t)->addChild((yyvsp[(1) - (5)].t));

        (yyvsp[(1) - (5)].t)->parent= (yyval.t);

        (yyvsp[(1) - (5)].t)->nextSibbling= (yyvsp[(2) - (5)].t);

        (yyval.t)->addChild((yyvsp[(2) - (5)].t));

        (yyvsp[(2) - (5)].t)->parent= (yyval.t);

        (yyvsp[(2) - (5)].t)->nextSibbling= (yyvsp[(3) - (5)].t);

        (yyval.t)->addChild((yyvsp[(3) - (5)].t));

        (yyvsp[(3) - (5)].t)->parent= (yyval.t);

        (yyvsp[(3) - (5)].t)->nextSibbling= (yyvsp[(4) - (5)].t);

        (yyval.t)->addChild((yyvsp[(4) - (5)].t));

        (yyvsp[(4) - (5)].t)->parent= (yyval.t);

        (yyvsp[(4) - (5)].t)->nextSibbling= (yyvsp[(5) - (5)].t);

        (yyval.t)->addChild((yyvsp[(5) - (5)].t));

        (yyvsp[(5) - (5)].t)->parent= (yyval.t);

    ;}
    break;

  case 452:
#line 7944 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 30 );

        (yyval.t)->addChild((yyvsp[(1) - (5)].t));

        (yyvsp[(1) - (5)].t)->parent= (yyval.t);

        (yyvsp[(1) - (5)].t)->nextSibbling= (yyvsp[(2) - (5)].t);

        (yyval.t)->addChild((yyvsp[(2) - (5)].t));

        (yyvsp[(2) - (5)].t)->parent= (yyval.t);

        (yyvsp[(2) - (5)].t)->nextSibbling= (yyvsp[(3) - (5)].t);

        (yyval.t)->addChild((yyvsp[(3) - (5)].t));

        (yyvsp[(3) - (5)].t)->parent= (yyval.t);

        (yyvsp[(3) - (5)].t)->nextSibbling= (yyvsp[(4) - (5)].t);

        (yyval.t)->addChild((yyvsp[(4) - (5)].t));

        (yyvsp[(4) - (5)].t)->parent= (yyval.t);

        (yyvsp[(4) - (5)].t)->nextSibbling= (yyvsp[(5) - (5)].t);

        (yyval.t)->addChild((yyvsp[(5) - (5)].t));

        (yyvsp[(5) - (5)].t)->parent= (yyval.t);

    ;}
    break;

  case 453:
#line 7979 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 30 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 454:
#line 7990 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 30 );

        (yyval.t)->addChild((yyvsp[(1) - (8)].t));

        (yyvsp[(1) - (8)].t)->parent= (yyval.t);

        (yyvsp[(1) - (8)].t)->nextSibbling= (yyvsp[(2) - (8)].t);

        (yyval.t)->addChild((yyvsp[(2) - (8)].t));

        (yyvsp[(2) - (8)].t)->parent= (yyval.t);

        (yyvsp[(2) - (8)].t)->nextSibbling= (yyvsp[(3) - (8)].t);

        (yyval.t)->addChild((yyvsp[(3) - (8)].t));

        (yyvsp[(3) - (8)].t)->parent= (yyval.t);

        (yyvsp[(3) - (8)].t)->nextSibbling= (yyvsp[(4) - (8)].t);

        (yyval.t)->addChild((yyvsp[(4) - (8)].t));

        (yyvsp[(4) - (8)].t)->parent= (yyval.t);

        (yyvsp[(4) - (8)].t)->nextSibbling= (yyvsp[(5) - (8)].t);

        (yyval.t)->addChild((yyvsp[(5) - (8)].t));

        (yyvsp[(5) - (8)].t)->parent= (yyval.t);

        (yyvsp[(5) - (8)].t)->nextSibbling= (yyvsp[(6) - (8)].t);

        (yyval.t)->addChild((yyvsp[(6) - (8)].t));

        (yyvsp[(6) - (8)].t)->parent= (yyval.t);

        (yyvsp[(6) - (8)].t)->nextSibbling= (yyvsp[(7) - (8)].t);

        (yyval.t)->addChild((yyvsp[(7) - (8)].t));

        (yyvsp[(7) - (8)].t)->parent= (yyval.t);

        (yyvsp[(7) - (8)].t)->nextSibbling= (yyvsp[(8) - (8)].t);

        (yyval.t)->addChild((yyvsp[(8) - (8)].t));

        (yyvsp[(8) - (8)].t)->parent= (yyval.t);

    ;}
    break;

  case 455:
#line 8043 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 30 );

        (yyval.t)->addChild((yyvsp[(1) - (5)].t));

        (yyvsp[(1) - (5)].t)->parent= (yyval.t);

        (yyvsp[(1) - (5)].t)->nextSibbling= (yyvsp[(2) - (5)].t);

        (yyval.t)->addChild((yyvsp[(2) - (5)].t));

        (yyvsp[(2) - (5)].t)->parent= (yyval.t);

        (yyvsp[(2) - (5)].t)->nextSibbling= (yyvsp[(3) - (5)].t);

        (yyval.t)->addChild((yyvsp[(3) - (5)].t));

        (yyvsp[(3) - (5)].t)->parent= (yyval.t);

        (yyvsp[(3) - (5)].t)->nextSibbling= (yyvsp[(4) - (5)].t);

        (yyval.t)->addChild((yyvsp[(4) - (5)].t));

        (yyvsp[(4) - (5)].t)->parent= (yyval.t);

        (yyvsp[(4) - (5)].t)->nextSibbling= (yyvsp[(5) - (5)].t);

        (yyval.t)->addChild((yyvsp[(5) - (5)].t));

        (yyvsp[(5) - (5)].t)->parent= (yyval.t);

    ;}
    break;

  case 456:
#line 8078 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 143 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 457:
#line 8095 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 143 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 458:
#line 8106 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 65 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 459:
#line 8117 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 65 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 460:
#line 8128 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 65 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 461:
#line 8145 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 65 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 462:
#line 8162 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 65 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 463:
#line 8179 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 65 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 464:
#line 8202 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 65 );

        (yyval.t)->addChild((yyvsp[(1) - (6)].t));

        (yyvsp[(1) - (6)].t)->parent= (yyval.t);

        (yyvsp[(1) - (6)].t)->nextSibbling= (yyvsp[(2) - (6)].t);

        (yyval.t)->addChild((yyvsp[(2) - (6)].t));

        (yyvsp[(2) - (6)].t)->parent= (yyval.t);

        (yyvsp[(2) - (6)].t)->nextSibbling= (yyvsp[(3) - (6)].t);

        (yyval.t)->addChild((yyvsp[(3) - (6)].t));

        (yyvsp[(3) - (6)].t)->parent= (yyval.t);

        (yyvsp[(3) - (6)].t)->nextSibbling= (yyvsp[(4) - (6)].t);

        (yyval.t)->addChild((yyvsp[(4) - (6)].t));

        (yyvsp[(4) - (6)].t)->parent= (yyval.t);

        (yyvsp[(4) - (6)].t)->nextSibbling= (yyvsp[(5) - (6)].t);

        (yyval.t)->addChild((yyvsp[(5) - (6)].t));

        (yyvsp[(5) - (6)].t)->parent= (yyval.t);

        (yyvsp[(5) - (6)].t)->nextSibbling= (yyvsp[(6) - (6)].t);

        (yyval.t)->addChild((yyvsp[(6) - (6)].t));

        (yyvsp[(6) - (6)].t)->parent= (yyval.t);

    ;}
    break;

  case 465:
#line 8243 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 65 );

        (yyval.t)->addChild((yyvsp[(1) - (8)].t));

        (yyvsp[(1) - (8)].t)->parent= (yyval.t);

        (yyvsp[(1) - (8)].t)->nextSibbling= (yyvsp[(2) - (8)].t);

        (yyval.t)->addChild((yyvsp[(2) - (8)].t));

        (yyvsp[(2) - (8)].t)->parent= (yyval.t);

        (yyvsp[(2) - (8)].t)->nextSibbling= (yyvsp[(3) - (8)].t);

        (yyval.t)->addChild((yyvsp[(3) - (8)].t));

        (yyvsp[(3) - (8)].t)->parent= (yyval.t);

        (yyvsp[(3) - (8)].t)->nextSibbling= (yyvsp[(4) - (8)].t);

        (yyval.t)->addChild((yyvsp[(4) - (8)].t));

        (yyvsp[(4) - (8)].t)->parent= (yyval.t);

        (yyvsp[(4) - (8)].t)->nextSibbling= (yyvsp[(5) - (8)].t);

        (yyval.t)->addChild((yyvsp[(5) - (8)].t));

        (yyvsp[(5) - (8)].t)->parent= (yyval.t);

        (yyvsp[(5) - (8)].t)->nextSibbling= (yyvsp[(6) - (8)].t);

        (yyval.t)->addChild((yyvsp[(6) - (8)].t));

        (yyvsp[(6) - (8)].t)->parent= (yyval.t);

        (yyvsp[(6) - (8)].t)->nextSibbling= (yyvsp[(7) - (8)].t);

        (yyval.t)->addChild((yyvsp[(7) - (8)].t));

        (yyvsp[(7) - (8)].t)->parent= (yyval.t);

        (yyvsp[(7) - (8)].t)->nextSibbling= (yyvsp[(8) - (8)].t);

        (yyval.t)->addChild((yyvsp[(8) - (8)].t));

        (yyvsp[(8) - (8)].t)->parent= (yyval.t);

    ;}
    break;

  case 466:
#line 8296 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 65 );

        (yyval.t)->addChild((yyvsp[(1) - (10)].t));

        (yyvsp[(1) - (10)].t)->parent= (yyval.t);

        (yyvsp[(1) - (10)].t)->nextSibbling= (yyvsp[(2) - (10)].t);

        (yyval.t)->addChild((yyvsp[(2) - (10)].t));

        (yyvsp[(2) - (10)].t)->parent= (yyval.t);

        (yyvsp[(2) - (10)].t)->nextSibbling= (yyvsp[(3) - (10)].t);

        (yyval.t)->addChild((yyvsp[(3) - (10)].t));

        (yyvsp[(3) - (10)].t)->parent= (yyval.t);

        (yyvsp[(3) - (10)].t)->nextSibbling= (yyvsp[(4) - (10)].t);

        (yyval.t)->addChild((yyvsp[(4) - (10)].t));

        (yyvsp[(4) - (10)].t)->parent= (yyval.t);

        (yyvsp[(4) - (10)].t)->nextSibbling= (yyvsp[(5) - (10)].t);

        (yyval.t)->addChild((yyvsp[(5) - (10)].t));

        (yyvsp[(5) - (10)].t)->parent= (yyval.t);

        (yyvsp[(5) - (10)].t)->nextSibbling= (yyvsp[(6) - (10)].t);

        (yyval.t)->addChild((yyvsp[(6) - (10)].t));

        (yyvsp[(6) - (10)].t)->parent= (yyval.t);

        (yyvsp[(6) - (10)].t)->nextSibbling= (yyvsp[(7) - (10)].t);

        (yyval.t)->addChild((yyvsp[(7) - (10)].t));

        (yyvsp[(7) - (10)].t)->parent= (yyval.t);

        (yyvsp[(7) - (10)].t)->nextSibbling= (yyvsp[(8) - (10)].t);

        (yyval.t)->addChild((yyvsp[(8) - (10)].t));

        (yyvsp[(8) - (10)].t)->parent= (yyval.t);

        (yyvsp[(8) - (10)].t)->nextSibbling= (yyvsp[(9) - (10)].t);

        (yyval.t)->addChild((yyvsp[(9) - (10)].t));

        (yyvsp[(9) - (10)].t)->parent= (yyval.t);

        (yyvsp[(9) - (10)].t)->nextSibbling= (yyvsp[(10) - (10)].t);

        (yyval.t)->addChild((yyvsp[(10) - (10)].t));

        (yyvsp[(10) - (10)].t)->parent= (yyval.t);

    ;}
    break;

  case 467:
#line 8361 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 65 );

        (yyval.t)->addChild((yyvsp[(1) - (12)].t));

        (yyvsp[(1) - (12)].t)->parent= (yyval.t);

        (yyvsp[(1) - (12)].t)->nextSibbling= (yyvsp[(2) - (12)].t);

        (yyval.t)->addChild((yyvsp[(2) - (12)].t));

        (yyvsp[(2) - (12)].t)->parent= (yyval.t);

        (yyvsp[(2) - (12)].t)->nextSibbling= (yyvsp[(3) - (12)].t);

        (yyval.t)->addChild((yyvsp[(3) - (12)].t));

        (yyvsp[(3) - (12)].t)->parent= (yyval.t);

        (yyvsp[(3) - (12)].t)->nextSibbling= (yyvsp[(4) - (12)].t);

        (yyval.t)->addChild((yyvsp[(4) - (12)].t));

        (yyvsp[(4) - (12)].t)->parent= (yyval.t);

        (yyvsp[(4) - (12)].t)->nextSibbling= (yyvsp[(5) - (12)].t);

        (yyval.t)->addChild((yyvsp[(5) - (12)].t));

        (yyvsp[(5) - (12)].t)->parent= (yyval.t);

        (yyvsp[(5) - (12)].t)->nextSibbling= (yyvsp[(6) - (12)].t);

        (yyval.t)->addChild((yyvsp[(6) - (12)].t));

        (yyvsp[(6) - (12)].t)->parent= (yyval.t);

        (yyvsp[(6) - (12)].t)->nextSibbling= (yyvsp[(7) - (12)].t);

        (yyval.t)->addChild((yyvsp[(7) - (12)].t));

        (yyvsp[(7) - (12)].t)->parent= (yyval.t);

        (yyvsp[(7) - (12)].t)->nextSibbling= (yyvsp[(8) - (12)].t);

        (yyval.t)->addChild((yyvsp[(8) - (12)].t));

        (yyvsp[(8) - (12)].t)->parent= (yyval.t);

        (yyvsp[(8) - (12)].t)->nextSibbling= (yyvsp[(9) - (12)].t);

        (yyval.t)->addChild((yyvsp[(9) - (12)].t));

        (yyvsp[(9) - (12)].t)->parent= (yyval.t);

        (yyvsp[(9) - (12)].t)->nextSibbling= (yyvsp[(10) - (12)].t);

        (yyval.t)->addChild((yyvsp[(10) - (12)].t));

        (yyvsp[(10) - (12)].t)->parent= (yyval.t);

        (yyvsp[(10) - (12)].t)->nextSibbling= (yyvsp[(11) - (12)].t);

        (yyval.t)->addChild((yyvsp[(11) - (12)].t));

        (yyvsp[(11) - (12)].t)->parent= (yyval.t);

        (yyvsp[(11) - (12)].t)->nextSibbling= (yyvsp[(12) - (12)].t);

        (yyval.t)->addChild((yyvsp[(12) - (12)].t));

        (yyvsp[(12) - (12)].t)->parent= (yyval.t);

    ;}
    break;

  case 468:
#line 8438 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 65 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 469:
#line 8461 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 65 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 470:
#line 8490 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 65 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 471:
#line 8501 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 65 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 472:
#line 8512 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 122 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 473:
#line 8529 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 122 );

        (yyval.t)->addChild((yyvsp[(1) - (5)].t));

        (yyvsp[(1) - (5)].t)->parent= (yyval.t);

        (yyvsp[(1) - (5)].t)->nextSibbling= (yyvsp[(2) - (5)].t);

        (yyval.t)->addChild((yyvsp[(2) - (5)].t));

        (yyvsp[(2) - (5)].t)->parent= (yyval.t);

        (yyvsp[(2) - (5)].t)->nextSibbling= (yyvsp[(3) - (5)].t);

        (yyval.t)->addChild((yyvsp[(3) - (5)].t));

        (yyvsp[(3) - (5)].t)->parent= (yyval.t);

        (yyvsp[(3) - (5)].t)->nextSibbling= (yyvsp[(4) - (5)].t);

        (yyval.t)->addChild((yyvsp[(4) - (5)].t));

        (yyvsp[(4) - (5)].t)->parent= (yyval.t);

        (yyvsp[(4) - (5)].t)->nextSibbling= (yyvsp[(5) - (5)].t);

        (yyval.t)->addChild((yyvsp[(5) - (5)].t));

        (yyvsp[(5) - (5)].t)->parent= (yyval.t);

    ;}
    break;

  case 474:
#line 8564 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 57 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 475:
#line 8587 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 57 );

        (yyval.t)->addChild((yyvsp[(1) - (5)].t));

        (yyvsp[(1) - (5)].t)->parent= (yyval.t);

        (yyvsp[(1) - (5)].t)->nextSibbling= (yyvsp[(2) - (5)].t);

        (yyval.t)->addChild((yyvsp[(2) - (5)].t));

        (yyvsp[(2) - (5)].t)->parent= (yyval.t);

        (yyvsp[(2) - (5)].t)->nextSibbling= (yyvsp[(3) - (5)].t);

        (yyval.t)->addChild((yyvsp[(3) - (5)].t));

        (yyvsp[(3) - (5)].t)->parent= (yyval.t);

        (yyvsp[(3) - (5)].t)->nextSibbling= (yyvsp[(4) - (5)].t);

        (yyval.t)->addChild((yyvsp[(4) - (5)].t));

        (yyvsp[(4) - (5)].t)->parent= (yyval.t);

        (yyvsp[(4) - (5)].t)->nextSibbling= (yyvsp[(5) - (5)].t);

        (yyval.t)->addChild((yyvsp[(5) - (5)].t));

        (yyvsp[(5) - (5)].t)->parent= (yyval.t);

    ;}
    break;

  case 476:
#line 8622 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 57 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 477:
#line 8639 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 57 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 478:
#line 8668 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 81 );

    ;}
    break;

  case 479:
#line 8675 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 81 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 480:
#line 8686 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 132 );

    ;}
    break;

  case 481:
#line 8693 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 132 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 482:
#line 8704 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 117 );

    ;}
    break;

  case 483:
#line 8711 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 117 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 484:
#line 8722 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 18 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 485:
#line 8733 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 18 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 486:
#line 8756 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 90 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 487:
#line 8785 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 90 );

        (yyval.t)->addChild((yyvsp[(1) - (7)].t));

        (yyvsp[(1) - (7)].t)->parent= (yyval.t);

        (yyvsp[(1) - (7)].t)->nextSibbling= (yyvsp[(2) - (7)].t);

        (yyval.t)->addChild((yyvsp[(2) - (7)].t));

        (yyvsp[(2) - (7)].t)->parent= (yyval.t);

        (yyvsp[(2) - (7)].t)->nextSibbling= (yyvsp[(3) - (7)].t);

        (yyval.t)->addChild((yyvsp[(3) - (7)].t));

        (yyvsp[(3) - (7)].t)->parent= (yyval.t);

        (yyvsp[(3) - (7)].t)->nextSibbling= (yyvsp[(4) - (7)].t);

        (yyval.t)->addChild((yyvsp[(4) - (7)].t));

        (yyvsp[(4) - (7)].t)->parent= (yyval.t);

        (yyvsp[(4) - (7)].t)->nextSibbling= (yyvsp[(5) - (7)].t);

        (yyval.t)->addChild((yyvsp[(5) - (7)].t));

        (yyvsp[(5) - (7)].t)->parent= (yyval.t);

        (yyvsp[(5) - (7)].t)->nextSibbling= (yyvsp[(6) - (7)].t);

        (yyval.t)->addChild((yyvsp[(6) - (7)].t));

        (yyvsp[(6) - (7)].t)->parent= (yyval.t);

        (yyvsp[(6) - (7)].t)->nextSibbling= (yyvsp[(7) - (7)].t);

        (yyval.t)->addChild((yyvsp[(7) - (7)].t));

        (yyvsp[(7) - (7)].t)->parent= (yyval.t);

    ;}
    break;

  case 488:
#line 8832 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 29 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 489:
#line 8843 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 29 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 490:
#line 8866 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 138 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 491:
#line 8883 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 139 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 492:
#line 8900 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 139 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 493:
#line 8929 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 139 );

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 494:
#line 8940 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 133 );

    ;}
    break;

  case 495:
#line 8947 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 133 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 496:
#line 8958 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 133 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 497:
#line 8969 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 133 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 498:
#line 8992 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 54 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 499:
#line 9003 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 54 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 500:
#line 9026 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 101 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 501:
#line 9055 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 101 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 502:
#line 9084 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 101 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 503:
#line 9107 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 101 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 504:
#line 9136 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 101 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 505:
#line 9159 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 12 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 506:
#line 9188 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 12 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 507:
#line 9217 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 12 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 508:
#line 9240 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 12 );

        (yyval.t)->addChild((yyvsp[(1) - (4)].t));

        (yyvsp[(1) - (4)].t)->parent= (yyval.t);

        (yyvsp[(1) - (4)].t)->nextSibbling= (yyvsp[(2) - (4)].t);

        (yyval.t)->addChild((yyvsp[(2) - (4)].t));

        (yyvsp[(2) - (4)].t)->parent= (yyval.t);

        (yyvsp[(2) - (4)].t)->nextSibbling= (yyvsp[(3) - (4)].t);

        (yyval.t)->addChild((yyvsp[(3) - (4)].t));

        (yyvsp[(3) - (4)].t)->parent= (yyval.t);

        (yyvsp[(3) - (4)].t)->nextSibbling= (yyvsp[(4) - (4)].t);

        (yyval.t)->addChild((yyvsp[(4) - (4)].t));

        (yyvsp[(4) - (4)].t)->parent= (yyval.t);

    ;}
    break;

  case 509:
#line 9269 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 12 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 510:
#line 9292 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 5 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 511:
#line 9303 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 21 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 512:
#line 9320 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 64 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 513:
#line 9331 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 64 );

        (yyval.t)->addChild((yyvsp[(1) - (2)].t));

        (yyvsp[(1) - (2)].t)->parent= (yyval.t);

        (yyvsp[(1) - (2)].t)->nextSibbling= (yyvsp[(2) - (2)].t);

        (yyval.t)->addChild((yyvsp[(2) - (2)].t));

        (yyvsp[(2) - (2)].t)->parent= (yyval.t);

    ;}
    break;

  case 514:
#line 9348 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 152 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 515:
#line 9359 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 152 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 516:
#line 9382 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 63 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;

  case 517:
#line 9393 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 63 );

        (yyval.t)->addChild((yyvsp[(1) - (3)].t));

        (yyvsp[(1) - (3)].t)->parent= (yyval.t);

        (yyvsp[(1) - (3)].t)->nextSibbling= (yyvsp[(2) - (3)].t);

        (yyval.t)->addChild((yyvsp[(2) - (3)].t));

        (yyvsp[(2) - (3)].t)->parent= (yyval.t);

        (yyvsp[(2) - (3)].t)->nextSibbling= (yyvsp[(3) - (3)].t);

        (yyval.t)->addChild((yyvsp[(3) - (3)].t));

        (yyvsp[(3) - (3)].t)->parent= (yyval.t);

    ;}
    break;

  case 518:
#line 9416 "pt_c.y"
    {
        (yyval.t)= new NonTerminal( 111 );

        (yyval.t)->addChild((yyvsp[(1) - (1)].t));

        (yyvsp[(1) - (1)].t)->parent= (yyval.t);

    ;}
    break;


/* Line 1267 of yacc.c.  */
#line 12590 "pt_c.tab.cc"
      default: break;
    }
  YY_SYMBOL_PRINT ("-> $$ =", yyr1[yyn], &yyval, &yyloc);

  YYPOPSTACK (yylen);
  yylen = 0;
  YY_STACK_PRINT (yyss, yyssp);

  *++yyvsp = yyval;


  /* Now `shift' the result of the reduction.  Determine what state
     that goes to, based on the state we popped back to and the rule
     number reduced by.  */

  yyn = yyr1[yyn];

  yystate = yypgoto[yyn - YYNTOKENS] + *yyssp;
  if (0 <= yystate && yystate <= YYLAST && yycheck[yystate] == *yyssp)
    yystate = yytable[yystate];
  else
    yystate = yydefgoto[yyn - YYNTOKENS];

  goto yynewstate;


/*------------------------------------.
| yyerrlab -- here on detecting error |
`------------------------------------*/
yyerrlab:
  /* If not already recovering from an error, report this error.  */
  if (!yyerrstatus)
    {
      ++yynerrs;
#if ! YYERROR_VERBOSE
      yyerror (YY_("syntax error"));
#else
      {
	YYSIZE_T yysize = yysyntax_error (0, yystate, yychar);
	if (yymsg_alloc < yysize && yymsg_alloc < YYSTACK_ALLOC_MAXIMUM)
	  {
	    YYSIZE_T yyalloc = 2 * yysize;
	    if (! (yysize <= yyalloc && yyalloc <= YYSTACK_ALLOC_MAXIMUM))
	      yyalloc = YYSTACK_ALLOC_MAXIMUM;
	    if (yymsg != yymsgbuf)
	      YYSTACK_FREE (yymsg);
	    yymsg = (char *) YYSTACK_ALLOC (yyalloc);
	    if (yymsg)
	      yymsg_alloc = yyalloc;
	    else
	      {
		yymsg = yymsgbuf;
		yymsg_alloc = sizeof yymsgbuf;
	      }
	  }

	if (0 < yysize && yysize <= yymsg_alloc)
	  {
	    (void) yysyntax_error (yymsg, yystate, yychar);
	    yyerror (yymsg);
	  }
	else
	  {
	    yyerror (YY_("syntax error"));
	    if (yysize != 0)
	      goto yyexhaustedlab;
	  }
      }
#endif
    }



  if (yyerrstatus == 3)
    {
      /* If just tried and failed to reuse look-ahead token after an
	 error, discard it.  */

      if (yychar <= YYEOF)
	{
	  /* Return failure if at end of input.  */
	  if (yychar == YYEOF)
	    YYABORT;
	}
      else
	{
	  yydestruct ("Error: discarding",
		      yytoken, &yylval);
	  yychar = YYEMPTY;
	}
    }

  /* Else will try to reuse look-ahead token after shifting the error
     token.  */
  goto yyerrlab1;


/*---------------------------------------------------.
| yyerrorlab -- error raised explicitly by YYERROR.  |
`---------------------------------------------------*/
yyerrorlab:

  /* Pacify compilers like GCC when the user code never invokes
     YYERROR and the label yyerrorlab therefore never appears in user
     code.  */
  if (/*CONSTCOND*/ 0)
     goto yyerrorlab;

  /* Do not reclaim the symbols of the rule which action triggered
     this YYERROR.  */
  YYPOPSTACK (yylen);
  yylen = 0;
  YY_STACK_PRINT (yyss, yyssp);
  yystate = *yyssp;
  goto yyerrlab1;


/*-------------------------------------------------------------.
| yyerrlab1 -- common code for both syntax error and YYERROR.  |
`-------------------------------------------------------------*/
yyerrlab1:
  yyerrstatus = 3;	/* Each real token shifted decrements this.  */

  for (;;)
    {
      yyn = yypact[yystate];
      if (yyn != YYPACT_NINF)
	{
	  yyn += YYTERROR;
	  if (0 <= yyn && yyn <= YYLAST && yycheck[yyn] == YYTERROR)
	    {
	      yyn = yytable[yyn];
	      if (0 < yyn)
		break;
	    }
	}

      /* Pop the current state because it cannot handle the error token.  */
      if (yyssp == yyss)
	YYABORT;


      yydestruct ("Error: popping",
		  yystos[yystate], yyvsp);
      YYPOPSTACK (1);
      yystate = *yyssp;
      YY_STACK_PRINT (yyss, yyssp);
    }

  if (yyn == YYFINAL)
    YYACCEPT;

  *++yyvsp = yylval;


  /* Shift the error token.  */
  YY_SYMBOL_PRINT ("Shifting", yystos[yyn], yyvsp, yylsp);

  yystate = yyn;
  goto yynewstate;


/*-------------------------------------.
| yyacceptlab -- YYACCEPT comes here.  |
`-------------------------------------*/
yyacceptlab:
  yyresult = 0;
  goto yyreturn;

/*-----------------------------------.
| yyabortlab -- YYABORT comes here.  |
`-----------------------------------*/
yyabortlab:
  yyresult = 1;
  goto yyreturn;

#ifndef yyoverflow
/*-------------------------------------------------.
| yyexhaustedlab -- memory exhaustion comes here.  |
`-------------------------------------------------*/
yyexhaustedlab:
  yyerror (YY_("memory exhausted"));
  yyresult = 2;
  /* Fall through.  */
#endif

yyreturn:
  if (yychar != YYEOF && yychar != YYEMPTY)
     yydestruct ("Cleanup: discarding lookahead",
		 yytoken, &yylval);
  /* Do not reclaim the symbols of the rule which action triggered
     this YYABORT or YYACCEPT.  */
  YYPOPSTACK (yylen);
  YY_STACK_PRINT (yyss, yyssp);
  while (yyssp != yyss)
    {
      yydestruct ("Cleanup: popping",
		  yystos[*yyssp], yyvsp);
      YYPOPSTACK (1);
    }
#ifndef yyoverflow
  if (yyss != yyssa)
    YYSTACK_FREE (yyss);
#endif
#if YYERROR_VERBOSE
  if (yymsg != yymsgbuf)
    YYSTACK_FREE (yymsg);
#endif
  /* Make sure YYID is used.  */
  return YYID (yyresult);
}


#line 9428 "pt_c.y"



#include <stdio.h>

extern char yytext[];
extern int column;
extern int line;

void yyerror( char *s)
{
fflush(stdout);
fprintf(stderr,"%s: %d.%d\n",s,line,column);
}



