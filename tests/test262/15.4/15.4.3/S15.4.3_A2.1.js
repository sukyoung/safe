  {
    var __result1 = Array.propertyIsEnumerable('length') !== false;
    var __expect1 = false;
  }
  result = true;
  for (p in Array)
  {
    if (p === "length")
    {
      result = false;
    }
  }
  {
    var __result2 = result !== true;
    var __expect2 = false;
  }
  