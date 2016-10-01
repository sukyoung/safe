  {
    var __result1 = ! (Error.hasOwnProperty('prototype'));
    var __expect1 = false;
  }
  {
    var __result2 = Error.propertyIsEnumerable('prototype');
    var __expect2 = false;
  }
  cout = 0;
  for (p in Error)
  {
    if (p === "prototype")
      cout++;
  }
  {
    var __result3 = cout !== 0;
    var __expect3 = false;
  }
  