  var __obj = {
    toString : (function () 
    {
      return {
        
      };
    }),
    valueOf : (function () 
    {
      return 1;
    })
  };
  __obj.toLowerCase = String.prototype.toLowerCase;
  {
    var __result1 = __obj.toLowerCase() !== "1";
    var __expect1 = false;
  }
  {
    var __result2 = __obj.toLowerCase().length !== 1;
    var __expect2 = false;
  }
  