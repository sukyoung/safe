  {
    var __result1 = Array.propertyIsEnumerable('join') !== false;
    var __expect1 = false;
  }
  var result = true;
  for(var p in Array)
  {
    if (p === "join")
    {
      result = false;
    }
  }
  {
    var __result2 = result !== true;
    var __expect2 = false;
  }
  