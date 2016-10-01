  var str1 = new String("h");
  var str2 = new String("\x68");
  {
    var __result1 = str1.localeCompare(str2) !== 0;
    var __expect1 = false;
  }
  var str2 = new String("\u0068");
  {
    var __result2 = str1.localeCompare(str2) !== 0;
    var __expect2 = false;
  }
  var str2 = new String("h");
  {
    var __result3 = str1.localeCompare(str2) !== 0;
    var __expect3 = false;
  }
  