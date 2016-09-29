  var pairs = [["o\u0308", "ö", ], ["ä\u0323", "a\u0323\u0308", ], ["a\u0308\u0323", "a\u0323\u0308", ], ["ạ\u0308", "a\u0323\u0308", ], ["ä\u0306", "a\u0308\u0306", ], ["ă\u0308", "a\u0306\u0308", ], ["\u1111\u1171\u11B6", "퓛", ], ["Å", "Å", ], ["Å", "A\u030A", ], ["x\u031B\u0323", "x\u0323\u031B", ], ["ự", "ụ\u031B", ], ["ự", "u\u031B\u0323", ], ["ự", "ư\u0323", ], ["ự", "u\u0323\u031B", ], ["Ç", "C\u0327", ], ["q\u0307\u0323", "q\u0323\u0307", ], ["가", "\u1100\u1161", ], ["Å", "A\u030A", ], ["Ω", "Ω", ], ["Å", "A\u030A", ], ["ô", "o\u0302", ], ["ṩ", "s\u0323\u0307", ], ["ḋ\u0323", "d\u0323\u0307", ], ["ḋ\u0323", "ḍ\u0307", ], ["q\u0307\u0323", "q\u0323\u0307", ], ];
  var i;
  for (i = 0;i < pairs.length;i++)
  {
    var pair = pairs[i];
    {
      var __result1 = pair[0].localeCompare(pair[1]) !== 0;
      var __expect1 = false;
    }
  }
  function toU(s) 
  {
    var result = "";
    var escape = "\\u0000";
    var i;
    for (i = 0;i < s.length;i++)
    {
      var hex = s.charCodeAt(i).toString(16);
      result += escape.substring(0, escape.length - hex.length) + hex;
    }
    return result;
  }
  