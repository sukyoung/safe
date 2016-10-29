  var x = 1 + (function (t) 
  {
    return {
      a : t
    };
  })(2 + 3).a;
  {
    var __result1 = x !== 6;
    var __expect1 = false;
  }
  