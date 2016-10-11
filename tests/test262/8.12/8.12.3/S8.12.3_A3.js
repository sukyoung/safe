  var __map = {
    shape : "cube",
    5 : "five",
    "6" : "six"
  };
  {
    var __result1 = __map.shape !== "cube";
    var __expect1 = false;
  }
  {
    var __result2 = __map["shape"] !== "cube";
    var __expect2 = false;
  }
  {
    var __result3 = __map["5"] !== "five";
    var __expect3 = false;
  }
  {
    var __result4 = __map[5] !== "five";
    var __expect4 = false;
  }
  {
    var __result5 = __map["6"] !== "six";
    var __expect5 = false;
  }
  {
    var __result6 = __map[6] !== "six";
    var __expect6 = false;
  }
  