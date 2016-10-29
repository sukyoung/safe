  var count = 0;
  var knock = (function () 
  {
    count++;
  });
  knock();
  {
    var __result1 = count !== 1;
    var __expect1 = false;
  }
  this['knock']();
  {
    var __result2 = count !== 2;
    var __expect2 = false;
  }
  