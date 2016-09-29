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
      var __result1 = slice(__obj, (function () 
      {
        return slice(0, 1);
      })()) !== "BBB";
      var __expect1 = false;
    }
  }
  var x;
  