  var obj = {
    
  };
  obj["0"] = 0;
  obj["3"] = 3;
  obj.shift = Array.prototype.shift;
  obj.length = 4;
  var shift = obj.shift();
  {
    var __result1 = shift !== 0;
    var __expect1 = false;
  }
  {
    var __result2 = obj.length !== 3;
    var __expect2 = false;
  }
  var shift = obj.shift();
  {
    var __result3 = shift !== undefined;
    var __expect3 = false;
  }
  {
    var __result4 = obj.length !== 2;
    var __expect4 = false;
  }
  obj.length = 1;
  var shift = obj.shift();
  {
    var __result5 = shift !== undefined;
    var __expect5 = false;
  }
  {
    var __result6 = obj.length !== 0;
    var __expect6 = false;
  }
  