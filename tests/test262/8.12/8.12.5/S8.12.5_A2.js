  var _map = {
    1 : "one",
    two : 2
  };
  _map[1] = "uno";
  {
    var __result1 = _map[1] !== "uno";
    var __expect1 = false;
  }
  _map["1"] = 1;
  {
    var __result2 = _map[1] !== 1;
    var __expect2 = false;
  }
  _map["two"] = "two";
  {
    var __result3 = _map["two"] !== "two";
    var __expect3 = false;
  }
  _map.two = "duo";
  {
    var __result4 = _map.two !== "duo";
    var __expect4 = false;
  }
  