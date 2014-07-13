// big object + field access test


var g = {
    "glossary": {
        "title": "example glossary",
        "GlossDiv": {
	    	"title": "S",
	    	"GlossList": {
                	"GlossEntry": {
		    			"ID": "SGML",
		    			"SortAs": "SGML",
		    			"GlossTerm": "Standard Generalized Markup Language",
		    			"Acronym": "SGML",
		    			"Abbrev": "ISO 8879:1986",
		    			"GlossDef": {
                        		"para": "A meta-markup language, used to create markup languages such as DocBook.",
                        		"GlossSeeAlso": ["GML", "XML"]
		    			},
		    			"GlossSee": "markup"
                	}
	    	}
        }
    }
}


var dak = g.glossary.title;
var qwerty = dak + "hat"
//dumpValue(dak);
var __result1 = dak;  // for SAFE
var __expect1 = "example glossary";  // for SAFE

//dumpValue(qwerty);
var __result2 = qwerty;  // for SAFE
var __expect2 = "example glossaryhat";  // for SAFE

//dumpObject(g);
//dumpObject(g.glossary);
//dumpObject(g["glossary"]);
//dumpObject(g.glossary["GlossDiv"]);
var __result3 = g.glossary["GlossDiv"].title;  // for SAFE
var __expect3 = "S";  // for SAFE

var __result4 = g.glossary["GlossDiv"].GlossList.GlossEntry.ID;  // for SAFE
var __expect4 = "SGML";  // for SAFE

var __result5 = g.glossary["GlossDiv"].GlossList.GlossEntry.SortAs;  // for SAFE
var __expect5 = "SGML";  // for SAFE

var __result6 = g.glossary["GlossDiv"].GlossList.GlossEntry.GlossTerm;  // for SAFE
var __expect6 = "Standard Generalized Markup Language";  // for SAFE

var __result7 = g.glossary["GlossDiv"].GlossList.GlossEntry.Acronym;  // for SAFE
var __expect7 = "SGML";  // for SAFE

var __result8 = g.glossary["GlossDiv"].GlossList.GlossEntry.Abbrev;  // for SAFE
var __expect8 = "ISO 8879:1986";  // for SAFE

var __result9 = g.glossary["GlossDiv"].GlossList.GlossEntry.GlossDef.para;  // for SAFE
var __expect9 = "A meta-markup language, used to create markup languages such as DocBook.";  // for SAFE

var __result10 = g.glossary["GlossDiv"].GlossList.GlossEntry.GlossDef.GlossSeeAlso[0];  // for SAFE
var __expect10 = "GML";  // for SAFE

var __result11 = g.glossary["GlossDiv"].GlossList.GlossEntry.GlossDef.GlossSeeAlso[1];  // for SAFE
var __expect11 = "XML";  // for SAFE

var __result12 = g.glossary["GlossDiv"].GlossList.GlossEntry.GlossSee;  // for SAFE
var __expect12 = "markup";  // for SAFE
