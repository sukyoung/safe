  var x1 = "abc";
  {
    var __result1 = String(x1) !== x1;
    var __expect1 = false;
  }
  var x2 = "abc";
  {
    var __result2 = typeof String(x2) !== typeof x2;
    var __expect2 = false;
  }
  