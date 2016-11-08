  var myComparefn = (function (x, y) 
  {
    if (x === undefined)
      return - 1;
    if (y === undefined)
      return 1;
    return 0;
  });
  var x = new Array(2);
  x[1] = 1;
  x.sort(myComparefn);
  {
    var __result1 = x.length !== 2;
    var __expect1 = false;
  }
  {
    var __result2 = x[0] !== 1;
    var __expect2 = false;
  }
  {
    var __result3 = x[1] !== undefined;
    var __expect3 = false;
  }
  var x = new Array(2);
  x[0] = 1;
  x.sort(myComparefn);
  {
    var __result4 = x.length !== 2;
    var __expect4 = false;
  }
  {
    var __result5 = x[0] !== 1;
    var __expect5 = false;
  }
  {
    var __result6 = x[1] !== undefined;
    var __expect6 = false;
  }
  