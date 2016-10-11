  var value = 1;
  var floatValues = new Array(1076);
  for(var power = 0;power <= 1075;power++)
  {
    floatValues[power] = value;
    value = value * 0.5;
  }
  {
    var __result1 = floatValues[1075] !== 0;
    var __expect1 = false;
  }
  {
    var __result2 = floatValues[1074] !== 4.9406564584124654417656879286822e-324;
    var __expect2 = false;
  }
  for(var index = 1074;index > 0;index--)
  {
    {
      var __result3 = floatValues[index] === 0;
      var __expect3 = false;
    }
    {
      var __result4 = floatValues[index - 1] !== (floatValues[index] * 2);
      var __expect4 = false;
    }
  }
  {
    var __result5 = ! (1.797693134862315708145274237317e+308 < Infinity);
    var __expect5 = false;
  }
  {
    var __result6 = ! (1.797693134862315808e+308 === + Infinity);
    var __expect6 = false;
  }
  