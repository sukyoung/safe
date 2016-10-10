  var date = new Date();
  {
    var __result1 = date + date !== date.toString() + date.toString();
    var __expect1 = false;
  }
  var date = new Date();
  {
    var __result2 = date + 0 !== date.toString() + "0";
    var __expect2 = false;
  }
  var date = new Date();
  {
    var __result3 = date + true !== date.toString() + "true";
    var __expect3 = false;
  }
  var date = new Date();
  {
    var __result4 = date + new Object() !== date.toString() + "[object Object]";
    var __expect4 = false;
  }
  