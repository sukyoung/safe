  x = "1";
  x += undefined;
  {
    var __result1 = x !== "1undefined";
    var __expect1 = false;
  }
  x = undefined;
  x += "1";
  {
    var __result2 = x !== "undefined1";
    var __expect2 = false;
  }
  x = new String("1");
  x += undefined;
  {
    var __result3 = x !== "1undefined";
    var __expect3 = false;
  }
  x = undefined;
  x += new String("1");
  {
    var __result4 = x !== "undefined1";
    var __expect4 = false;
  }
  