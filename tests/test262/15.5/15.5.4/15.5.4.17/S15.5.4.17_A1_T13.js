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
  __obj.toLocaleLowerCase = String.prototype.toLocaleLowerCase;
  {
    var __result1 = __obj.toLocaleLowerCase() !== "1";
    var __expect1 = false;
  }
  {
    var __result2 = __obj.toLocaleLowerCase().length !== 1;
    var __expect2 = false;
  }
  