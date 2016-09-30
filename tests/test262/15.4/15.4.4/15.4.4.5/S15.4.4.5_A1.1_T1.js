  var x = new Array();
  {
    var __result1 = x.join() !== "";
    var __expect1 = false;
  }
  x = [];
  x[0] = 1;
  x.length = 0;
  {
    var __result2 = x.join() !== "";
    var __expect2 = false;
  }
  