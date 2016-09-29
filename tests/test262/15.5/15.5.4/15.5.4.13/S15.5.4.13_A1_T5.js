  __func.valueOf = (function () 
  {
    return "gnulluna";
  });
  __func.toString = (function () 
  {
    return __func;
  });
  Function.prototype.slice = String.prototype.slice;
  {
    var __result1 = __func.slice(null, new Function().slice(__func, 5).length) !== "gnull";
    var __expect1 = false;
  }
  function __func() 
  {
    
  }
  ;
  