  var object = {
    valueOf : (function () 
    {
      return 0;
    }),
    toString : (function () 
    {
      return 1;
    })
  };
  {
    var __result1 = String(object) !== "1";
    var __expect1 = false;
  }
  var object = {
    valueOf : (function () 
    {
      return 0;
    }),
    toString : (function () 
    {
      return {
        
      };
    })
  };
  {
    var __result2 = String(object) !== "0";
    var __expect2 = false;
  }
  