  var obj = {
    
  };
  obj.join = Array.prototype.join;
  obj.length = NaN;
  {
    var __result1 = obj.join() !== "";
    var __expect1 = false;
  }
  {
    var __result2 = isNaN(obj.length) !== true;
    var __expect2 = false;
  }
  obj.length = Number.POSITIVE_INFINITY;
  {
    var __result3 = obj.join() !== "";
    var __expect3 = false;
  }
  {
    var __result4 = obj.length !== Number.POSITIVE_INFINITY;
    var __expect4 = false;
  }
  obj.length = Number.NEGATIVE_INFINITY;
  {
    var __result5 = obj.join() !== "";
    var __expect5 = false;
  }
  {
    var __result6 = obj.length !== Number.NEGATIVE_INFINITY;
    var __expect6 = false;
  }
  obj.length = - 0;
  {
    var __result7 = obj.join() !== "";
    var __expect7 = false;
  }
  if (obj.length !== - 0)
  {
    $ERROR('#8: var obj = {}; obj.length = -0; obj.join = Array.prototype.join; obj.join(); obj.length === 0. Actual: ' + (obj.length));
  }
  else
  {
    {
      var __result8 = 1 / obj.length !== Number.NEGATIVE_INFINITY;
      var __expect8 = false;
    }
  }
  obj.length = 0.5;
  {
    var __result9 = obj.join() !== "";
    var __expect9 = false;
  }
  {
    var __result10 = obj.length !== 0.5;
    var __expect10 = false;
  }
  var x = new Number(0);
  obj.length = x;
  {
    var __result11 = obj.join() !== "";
    var __expect11 = false;
  }
  {
    var __result12 = obj.length !== x;
    var __expect12 = false;
  }
  