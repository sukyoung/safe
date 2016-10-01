  var obj = {
    
  };
  obj.join = Array.prototype.join;
  if (obj.length !== undefined)
  {
    $ERROR('#0: var obj = {}; obj.length === undefined. Actual: ' + (obj.length));
  }
  else
  {
    {
      var __result1 = obj.join() !== "";
      var __expect1 = false;
    }
    {
      var __result2 = obj.length !== undefined;
      var __expect2 = false;
    }
  }
  obj.length = undefined;
  {
    var __result3 = obj.join() !== "";
    var __expect3 = false;
  }
  {
    var __result4 = obj.length !== undefined;
    var __expect4 = false;
  }
  obj.length = null;
  {
    var __result5 = obj.join() !== "";
    var __expect5 = false;
  }
  {
    var __result6 = obj.length !== null;
    var __expect6 = false;
  }
  