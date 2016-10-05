  {
    var __result1 = "abc123".charAt(5) !== "3";
    var __expect1 = false;
  }
  {
    var __result2 = "abc123"["charAt"](0) !== "a";
    var __expect2 = false;
  }
  {
    var __result3 = "abc123".length !== 6;
    var __expect3 = false;
  }
  {
    var __result4 = "abc123"["length"] !== 6;
    var __expect4 = false;
  }
  {
    var __result5 = new String("abc123").length !== 6;
    var __expect5 = false;
  }
  {
    var __result6 = new String("abc123")["charAt"](2) !== "c";
    var __expect6 = false;
  }
  