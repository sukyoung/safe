  {
    var __result1 = ! (String.prototype.concat.hasOwnProperty('length'));
    var __expect1 = false;
  }
  {
    var __result2 = String.prototype.concat.propertyIsEnumerable('length');
    var __expect2 = false;
  }
  count = 0;
  for (p in String.prototype.concat)
  {
    if (p === "length")
      count++;
  }
  {
    var __result3 = count !== 0;
    var __expect3 = false;
  }
  