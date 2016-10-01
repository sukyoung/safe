  {
    var __result1 = ! (String.prototype.substring.hasOwnProperty('length'));
    var __expect1 = false;
  }
  {
    var __result2 = String.prototype.substring.propertyIsEnumerable('length');
    var __expect2 = false;
  }
  var count = 0;
  for(var p in String.prototype.substring)
  {
    if (p === "length")
      count++;
  }
  {
    var __result3 = count !== 0;
    var __expect3 = false;
  }
  