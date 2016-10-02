  var x = (function __func() 
  {
    return arguments[0] + "-" + arguments[1];
  })("Obi", "Wan");
  {
    var __result1 = x !== "Obi-Wan";
    var __expect1 = false;
  }
  {
    var __result2 = typeof __func !== 'undefined';
    var __expect2 = false;
  }
  