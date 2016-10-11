  {
    var __result1 = Object(true).valueOf() !== true;
    var __expect1 = false;
  }
  {
    var __result2 = typeof Object(true) !== "object";
    var __expect2 = false;
  }
  {
    var __result3 = Object(true).constructor.prototype !== Boolean.prototype;
    var __expect3 = false;
  }
  {
    var __result4 = Object(false).valueOf() !== false;
    var __expect4 = false;
  }
  {
    var __result5 = typeof Object(false) !== "object";
    var __expect5 = false;
  }
  {
    var __result6 = Object(false).constructor.prototype !== Boolean.prototype;
    var __expect6 = false;
  }
  