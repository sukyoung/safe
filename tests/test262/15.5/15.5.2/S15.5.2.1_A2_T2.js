  var __str__obj = new String("shocking blue");
  {
    var __result1 = __str__obj["__custom__prop"] !== undefined;
    var __expect1 = false;
  }
  String.prototype.__custom__prop = "bor";
  {
    var __result2 = __str__obj["__custom__prop"] !== "bor";
    var __expect2 = false;
  }
  