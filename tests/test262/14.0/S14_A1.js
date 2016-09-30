  var THERE = "I'm there";
  var HERE = "I'm here";
  {
    var __result1 = __func !== undefined;
    var __expect1 = false;
  }
  if (true)
  {
    var __func = (function () 
    {
      return HERE;
    });
  }
  else
  {
    var __func = (function () 
    {
      return THERE;
    });
  }
  ;
  {
    var __result2 = __func() !== HERE;
    var __expect2 = false;
  }
  