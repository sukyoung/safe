  var obj = new Number(- 1);
  var x = new Array(obj);
  {
    var __result1 = x.length !== 1;
    var __expect1 = false;
  }
  {
    var __result2 = x[0] !== obj;
    var __expect2 = false;
  }
  var obj = new Number(4294967296);
  var x = new Array(obj);
  {
    var __result3 = x.length !== 1;
    var __expect3 = false;
  }
  {
    var __result4 = x[0] !== obj;
    var __expect4 = false;
  }
  var obj = new Number(4294967297);
  var x = new Array(obj);
  {
    var __result5 = x.length !== 1;
    var __expect5 = false;
  }
  {
    var __result6 = x[0] !== obj;
    var __expect6 = false;
  }
  