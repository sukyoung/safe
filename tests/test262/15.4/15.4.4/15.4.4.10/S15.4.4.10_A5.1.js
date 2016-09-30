  {
    var __result1 = Array.prototype.slice.propertyIsEnumerable('length') !== false;
    var __expect1 = false;
  }
  var result = true;
  for(var p in Array.slice)
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
  