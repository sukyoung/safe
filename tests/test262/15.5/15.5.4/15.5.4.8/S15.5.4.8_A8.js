  {
    var __result1 = ! (String.prototype.lastIndexOf.hasOwnProperty('length'));
    var __expect1 = false;
  }
  {
    var __result2 = String.prototype.lastIndexOf.propertyIsEnumerable('length');
    var __expect2 = false;
  }
  count = 0;
  for (p in String.prototype.lastIndexOf)
  {
    if (p === "length")
      count++;
  }
  {
    var __result3 = count !== 0;
    var __expect3 = false;
  }
  