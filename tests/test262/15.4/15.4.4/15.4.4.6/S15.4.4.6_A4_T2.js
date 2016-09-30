  Array.prototype[1] = - 1;
  var x = [0, 1, ];
  x.length = 2;
  var pop = x.pop();
  {
    var __result1 = pop !== 1;
    var __expect1 = false;
  }
  {
    var __result2 = x[1] !== - 1;
    var __expect2 = false;
  }
  Object.prototype[1] = - 1;
  Object.prototype.length = 2;
  Object.prototype.pop = Array.prototype.pop;
  x = {
    0 : 0,
    1 : 1
  };
  var pop = x.pop();
  {
    var __result3 = pop !== 1;
    var __expect3 = false;
  }
  {
    var __result4 = x[1] !== - 1;
    var __expect4 = false;
  }
  {
    var __result5 = x.length !== 1;
    var __expect5 = false;
  }
  delete x.length;
  {
    var __result6 = x.length !== 2;
    var __expect6 = false;
  }
  