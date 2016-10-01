  var obj = {
    
  };
  obj.shift = Array.prototype.shift;
  if (obj.length !== undefined)
  {
    $ERROR('#0: var obj = {}; obj.length === undefined. Actual: ' + (obj.length));
  }
  else
  {
    var shift = obj.shift();
    {
      var __result1 = shift !== undefined;
      var __expect1 = false;
    }
    {
      var __result2 = obj.length !== 0;
      var __expect2 = false;
    }
  }
  obj.length = undefined;
  var shift = obj.shift();
  {
    var __result3 = shift !== undefined;
    var __expect3 = false;
  }
  {
    var __result4 = obj.length !== 0;
    var __expect4 = false;
  }
  obj.length = null;
  var shift = obj.shift();
  {
    var __result5 = shift !== undefined;
    var __expect5 = false;
  }
  {
    var __result6 = obj.length !== 0;
    var __expect6 = false;
  }
  