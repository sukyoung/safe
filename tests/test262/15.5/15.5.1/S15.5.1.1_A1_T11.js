  var __str = String(1 / 0);
  {
    var __result1 = typeof __str !== "string";
    var __expect1 = false;
  }
  {
    var __result2 = __str !== "Infinity";
    var __expect2 = false;
  }
  __str = String(- 1 / 0);
  {
    var __result3 = typeof __str !== "string";
    var __expect3 = false;
  }
  {
    var __result4 = __str !== "-Infinity";
    var __expect4 = false;
  }
  __str = String(Infinity);
  {
    var __result5 = typeof __str !== "string";
    var __expect5 = false;
  }
  {
    var __result6 = __str !== "Infinity";
    var __expect6 = false;
  }
  __str = String(- Infinity);
  {
    var __result7 = typeof __str !== "string";
    var __expect7 = false;
  }
  {
    var __result8 = __str !== "-Infinity";
    var __expect8 = false;
  }
  __str = String(Number.POSITIVE_INFINITY);
  {
    var __result9 = typeof __str !== "string";
    var __expect9 = false;
  }
  {
    var __result10 = __str !== "Infinity";
    var __expect10 = false;
  }
  __str = String(Number.NEGATIVE_INFINITY);
  {
    var __result11 = typeof __str !== "string";
    var __expect11 = false;
  }
  {
    var __result12 = __str !== "-Infinity";
    var __expect12 = false;
  }
  