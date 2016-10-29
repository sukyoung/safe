  var x = 1 + (function f(t) 
  {
    return {
      a : (function () 
      {
        return t + 1;
      })
    };
  })(2 + 3).a();
  {
    var __result1 = x !== 7;
    var __expect1 = false;
  }
  