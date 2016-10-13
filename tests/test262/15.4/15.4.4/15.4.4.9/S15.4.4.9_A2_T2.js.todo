// TODO [[DefineOwnProperty]] for Array object
//  var obj = {
//    
//  };
//  obj.shift = Array.prototype.shift;
//  obj.length = NaN;
//  var shift = obj.shift();
//  {
//    var __result1 = shift !== undefined;
//    var __expect1 = false;
//  }
//  {
//    var __result2 = obj.length !== 0;
//    var __expect2 = false;
//  }
//  obj.length = Number.POSITIVE_INFINITY;
//  var shift = obj.shift();
//  {
//    var __result3 = shift !== undefined;
//    var __expect3 = false;
//  }
//  {
//    var __result4 = obj.length !== 0;
//    var __expect4 = false;
//  }
//  obj.length = Number.NEGATIVE_INFINITY;
//  var shift = obj.shift();
//  {
//    var __result5 = shift !== undefined;
//    var __expect5 = false;
//  }
//  {
//    var __result6 = obj.length !== 0;
//    var __expect6 = false;
//  }
//  obj.length = - 0;
//  var shift = obj.shift();
//  {
//    var __result7 = shift !== undefined;
//    var __expect7 = false;
//  }
//  if (obj.length !== 0)
//  {
//    $ERROR('#8: var obj = {}; obj.length = -0; obj.shift = Array.prototype.shift; obj.shift(); obj.length === 0. Actual: ' + (obj.length));
//  }
//  else
//  {
//    {
//      var __result8 = 1 / obj.length !== Number.POSITIVE_INFINITY;
//      var __expect8 = false;
//    }
//  }
//  obj.length = 0.5;
//  var shift = obj.shift();
//  {
//    var __result9 = shift !== undefined;
//    var __expect9 = false;
//  }
//  {
//    var __result10 = obj.length !== 0;
//    var __expect10 = false;
//  }
//  obj.length = new Number(0);
//  var shift = obj.shift();
//  {
//    var __result11 = shift !== undefined;
//    var __expect11 = false;
//  }
//  {
//    var __result12 = obj.length !== 0;
//    var __expect12 = false;
//  }
//  
