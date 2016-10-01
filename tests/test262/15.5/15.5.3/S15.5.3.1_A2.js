  {
    var __result1 = ! (String.hasOwnProperty('prototype'));
    var __expect1 = false;
  }
  {
    var __result2 = String.propertyIsEnumerable('prototype');
    var __expect2 = false;
  }
  var count = 0;
  for (p in String)
  {
    if (p === "prototype")
      count++;
  }
  {
    var __result3 = count !== 0;
    var __expect3 = false;
  }
  