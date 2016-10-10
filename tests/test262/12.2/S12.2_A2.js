  {
    var __result1 = delete (__variable);
    var __expect1 = false;
  }
  {
    var __result2 = delete (this["__variable"]);
    var __expect2 = false;
  }
  var __variable;
  var __variable = "defined";
  {
    var __result3 = delete (__variable) | delete (this["__variable"]);
    var __expect3 = 0;
  }
  {
    var __result4 = (__variable !== "defined") | (this["__variable"] !== "defined");
    var __expect4 = 0;
  }
