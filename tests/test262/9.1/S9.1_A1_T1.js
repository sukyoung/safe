  var object = {
    valueOf : (function () 
    {
      return "1";
    }),
    toString : (function () 
    {
      return 0;
    })
  };
  {
    var __result1 = Number(object) !== 1;
    var __expect1 = false;
  }
  var object = {
    valueOf : (function () 
    {
      return {
        
      };
    }),
    toString : (function () 
    {
      return "0";
    })
  };
  {
    var __result2 = Number(object) !== 0;
    var __expect2 = false;
  }
  