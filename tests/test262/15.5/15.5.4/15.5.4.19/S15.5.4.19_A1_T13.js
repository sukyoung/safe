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
  __obj.toLocaleUpperCase = String.prototype.toLocaleUpperCase;
  {
    var __result1 = __obj.toLocaleUpperCase() !== "1";
    var __expect1 = false;
  }
  {
    var __result2 = __obj.toLocaleUpperCase().length !== 1;
    var __expect2 = false;
  }
  