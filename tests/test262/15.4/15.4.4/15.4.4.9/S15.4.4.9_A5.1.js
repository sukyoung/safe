  {
    var __result1 = Array.prototype.shift.propertyIsEnumerable('length') !== false;
    var __expect1 = false;
  }
  var result = true;
  for(var p in Array.shift)
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
  