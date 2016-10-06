  var x = "1";
  {
    var __result1 = -- x !== 1 - 1;
    var __expect1 = false;
  }
  var x = "x";
  {
    var __result2 = isNaN(-- x) !== true;
    var __expect2 = false;
  }
  var x = new String("-1");
  {
    var __result3 = -- x !== - 1 - 1;
    var __expect3 = false;
  }
  