  var __var = "OUT";
  (function () 
  {
    var __var = "IN";
    (function () 
    {
      __var = "INNER_SPACE";
    })();
    (function () 
    {
      var __var = "INNER_SUN";
    })();
    {
      var __result1 = __var !== "INNER_SPACE";
      var __expect1 = false;
    }
  })();
  {
    var __result2 = __var !== "OUT";
    var __expect2 = false;
  }
  (function () 
  {
    __var = "IN";
    (function () 
    {
      __var = "INNERED";
    })();
    (function () 
    {
      var __var = "INNAGER";
    })();
    {
      var __result3 = __var !== "INNERED";
      var __expect3 = false;
    }
  })();
  {
    var __result4 = __var !== "INNERED";
    var __expect4 = false;
  }
  