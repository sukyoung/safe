  var obj = {
    
  };
  obj.pop = Array.prototype.pop;
  if (obj.length !== undefined)
  {
    $ERROR('#0: var obj = {}; obj.length === undefined. Actual: ' + (obj.length));
  }
  else
  {
    var pop = obj.pop();
    {
      var __result1 = pop !== undefined;
      var __expect1 = false;
    }
    {
      var __result2 = obj.length !== 0;
      var __expect2 = false;
    }
  }
  obj.length = undefined;
  var pop = obj.pop();
  {
    var __result3 = pop !== undefined;
    var __expect3 = false;
  }
  {
    var __result4 = obj.length !== 0;
    var __expect4 = false;
  }
  obj.length = null;
  var pop = obj.pop();
  {
    var __result5 = pop !== undefined;
    var __expect5 = false;
  }
  {
    var __result6 = obj.length !== 0;
    var __expect6 = false;
  }
  