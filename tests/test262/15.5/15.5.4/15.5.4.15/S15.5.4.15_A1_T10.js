  var __obj = {
    valueOf : (function () 
    {
      return 2;
    })
  };
  var __str = "\u0035ABBBABAB";
  with (__str)
  {
    {
      var __result1 = substring(__obj, (function () 
      {
        return substring(0, 1);
      })()) !== "BBB";
      var __expect1 = false;
    }
  }
  var x;
  