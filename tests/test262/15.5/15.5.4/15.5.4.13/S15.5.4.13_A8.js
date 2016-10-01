  {
    var __result1 = ! (String.prototype.slice.hasOwnProperty('length'));
    var __expect1 = false;
  }
  {
    var __result2 = String.prototype.slice.propertyIsEnumerable('length');
    var __expect2 = false;
  }
  var count = 0;
  for (p in String.prototype.slice)
  {
    if (p === "length")
      count++;
  }
  {
    var __result3 = count !== 0;
    var __expect3 = false;
  }
  