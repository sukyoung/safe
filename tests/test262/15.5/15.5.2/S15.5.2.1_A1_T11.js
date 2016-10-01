  function __obj() 
  {
    
  }
  ;
  __obj.valueOf = (function () 
  {
    return true;
  });
  __obj.toString = (function () 
  {
    return {
      
    };
  });
  var __str = new String(__obj);
  {
    var __result1 = typeof __str !== "object";
    var __expect1 = false;
  }
  {
    var __result2 = __str.constructor !== String;
    var __expect2 = false;
  }
  {
    var __result3 = __str != "true";
    var __expect3 = false;
  }
  