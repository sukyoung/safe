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
  __obj.toUpperCase = String.prototype.toUpperCase;
  {
    var __result1 = __obj.toUpperCase() !== "1";
    var __expect1 = false;
  }
  {
    var __result2 = __obj.toUpperCase().length !== 1;
    var __expect2 = false;
  }
  