  var __count = 0;
  this["beep"] = (function () 
  {
    __count++;
  });
  beep();
  {
    var __result1 = __count !== 1;
    var __expect1 = false;
  }
  this["beep"]();
  {
    var __result2 = __count !== 2;
    var __expect2 = false;
  }
  