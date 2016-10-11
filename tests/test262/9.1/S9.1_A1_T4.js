  var object = {
    valueOf : (function () 
    {
      return - 2;
    }),
    toString : (function () 
    {
      return "-2";
    })
  };
  {
    var __result1 = "-1" < object;
    var __expect1 = false;
  }
  var object = {
    valueOf : (function () 
    {
      return "-2";
    }),
    toString : (function () 
    {
      return - 2;
    })
  };
  {
    var __result2 = object < "-1";
    var __expect2 = false;
  }
  