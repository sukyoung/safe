  var obj = {
    
  };
  obj.join = Array.prototype.join;
  obj.length = 4.5;
  {
    var __result1 = obj.join() !== ",,,";
    var __expect1 = false;
  }
  obj[0] = undefined;
  obj[1] = 1;
  obj[2] = null;
  {
    var __result2 = obj.join() !== ",1,,";
    var __expect2 = false;
  }
  {
    var __result3 = obj.length !== 4.5;
    var __expect3 = false;
  }
  var obj = {
    
  };
  obj.join = Array.prototype.join;
  var x = new Number(4.5);
  obj.length = x;
  {
    var __result4 = obj.join() !== ",,,";
    var __expect4 = false;
  }
  obj[0] = undefined;
  obj[1] = 1;
  obj[2] = null;
  {
    var __result5 = obj.join() !== ",1,,";
    var __expect5 = false;
  }
  {
    var __result6 = obj.length !== x;
    var __expect6 = false;
  }
  