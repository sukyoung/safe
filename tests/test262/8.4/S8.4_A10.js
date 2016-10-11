  var __str = "\u0041A\u0042B\u0043C";
  {
    var __result1 = __str !== 'AABBCC';
    var __expect1 = false;
  }
  ;
  var __str__ = "\u0041\u0042\u0043" + 'ABC';
  {
    var __result2 = __str__ !== 'ABCABC';
    var __expect2 = false;
  }
  ;
  var str__ = "ABC" + '\u0041\u0042\u0043';
  {
    var __result3 = str__ !== "ABCABC";
    var __expect3 = false;
  }
  ;
  