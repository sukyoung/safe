  var __obj = {
    toString : (function () 
    {
      return "\u0041";
    })
  };
  var __obj2 = {
    toString : (function () 
    {
      return true;
    })
  };
  var __obj3 = {
    toString : (function () 
    {
      return 42;
    })
  };
  var __str = "lego";
  with (__str)
  {
    {
      var __result1 = concat(__obj, __obj2, __obj3, x) !== "legoAtrue42undefined";
      var __expect1 = false;
    }
  }
  var x;
  