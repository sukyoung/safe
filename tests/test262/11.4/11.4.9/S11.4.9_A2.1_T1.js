  {
    var __result1 = ! true !== false;
    var __expect1 = false;
  }
  {
    var __result2 = ! (! true) !== true;
    var __expect2 = false;
  }
  var x = true;
  {
    var __result3 = ! x !== false;
    var __expect3 = false;
  }
  var x = true;
  {
    var __result4 = ! (! x) !== true;
    var __expect4 = false;
  }
  var object = new Object();
  object.prop = true;
  {
    var __result5 = ! object.prop !== false;
    var __expect5 = false;
  }
  