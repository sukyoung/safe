  var obj = {
    
  };
  obj.push = Array.prototype.push;
  if (obj.length !== undefined)
  {
    $ERROR('#0: var obj = {}; obj.length === undefined. Actual: ' + (obj.length));
  }
  else
  {
    var push = obj.push(- 1);
    {
      var __result1 = push !== 1;
      var __expect1 = false;
    }
    {
      var __result2 = obj.length !== 1;
      var __expect2 = false;
    }
    {
      var __result3 = obj["0"] !== - 1;
      var __expect3 = false;
    }
  }
  obj.length = undefined;
  var push = obj.push(- 4);
  {
    var __result4 = push !== 1;
    var __expect4 = false;
  }
  {
    var __result5 = obj.length !== 1;
    var __expect5 = false;
  }
  {
    var __result6 = obj["0"] !== - 4;
    var __expect6 = false;
  }
  obj.length = null;
  var push = obj.push(- 7);
  {
    var __result7 = push !== 1;
    var __expect7 = false;
  }
  {
    var __result8 = obj.length !== 1;
    var __expect8 = false;
  }
  {
    var __result9 = obj["0"] !== - 7;
    var __expect9 = false;
  }
  