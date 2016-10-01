  Array.prototype[1] = 1;
  var x = [0, ];
  x.length = 2;
  {
    var __result1 = x.join() !== "0,1";
    var __expect1 = false;
  }
  Object.prototype[1] = 1;
  Object.prototype.length = 2;
  Object.prototype.join = Array.prototype.join;
  x = {
    0 : 0
  };
  {
    var __result2 = x.join() !== "0,1";
    var __expect2 = false;
  }
  