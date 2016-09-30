  {
    var __result1 = typeof Date() !== "string";
    var __expect1 = false;
  }
  {
    var __result2 = typeof Date(1) !== "string";
    var __expect2 = false;
  }
  {
    var __result3 = typeof Date(1970, 1) !== "string";
    var __expect3 = false;
  }
  {
    var __result4 = typeof Date(1970, 1, 1) !== "string";
    var __expect4 = false;
  }
  {
    var __result5 = typeof Date(1970, 1, 1, 1) !== "string";
    var __expect5 = false;
  }
  {
    var __result6 = typeof Date(1970, 1, 1, 1) !== "string";
    var __expect6 = false;
  }
  {
    var __result7 = typeof Date(1970, 1, 1, 1, 0) !== "string";
    var __expect7 = false;
  }
  {
    var __result8 = typeof Date(1970, 1, 1, 1, 0, 0) !== "string";
    var __expect8 = false;
  }
  {
    var __result9 = typeof Date(1970, 1, 1, 1, 0, 0, 0) !== "string";
    var __expect9 = false;
  }
  {
    var __result10 = typeof Date(Number.NaN) !== "string";
    var __expect10 = false;
  }
  {
    var __result11 = typeof Date(Number.POSITIVE_INFINITY) !== "string";
    var __expect11 = false;
  }
  {
    var __result12 = typeof Date(Number.NEGATIVE_INFINITY) !== "string";
    var __expect12 = false;
  }
  {
    var __result13 = typeof Date(undefined) !== "string";
    var __expect13 = false;
  }
  {
    var __result14 = typeof Date(null) !== "string";
    var __expect14 = false;
  }
  