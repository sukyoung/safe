  var __obj = {
    toString : (function () 
    {
      return "\u0041B";
    })
  };
  var __obj2 = {
    valueOf : (function () 
    {
      return NaN;
    })
  };
  var __str = "ABB\u0041BABAB";
  with (__str)
  {
    {
      var __result1 = lastIndexOf(__obj, __obj2) !== 7;
      var __expect1 = false;
    }
  }
  var x;
  