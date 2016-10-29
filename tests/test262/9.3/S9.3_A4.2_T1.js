  {
    var __result1 = isNaN(Number(Number.NaN)) !== true;
    var __expect1 = false;
  }
  if (Number(+ 0) !== + 0)
  {
    $ERROR('#2.1: Number(+0) === 0. Actual: ' + (Number(+ 0)));
  }
  else
  {
    {
      var __result2 = 1 / Number(+ 0) !== Number.POSITIVE_INFINITY;
      var __expect2 = false;
    }
  }
  if (Number(- 0) !== - 0)
  {
    $ERROR('#3.1: Number(-0) === 0. Actual: ' + (Number(- 0)));
  }
  else
  {
    {
      var __result3 = 1 / Number(- 0) !== Number.NEGATIVE_INFINITY;
      var __expect3 = false;
    }
  }
  {
    var __result4 = Number(Number.POSITIVE_INFINITY) !== Number.POSITIVE_INFINITY;
    var __expect4 = false;
  }
  {
    var __result5 = Number(Number.NEGATIVE_INFINITY) !== Number.NEGATIVE_INFINITY;
    var __expect5 = false;
  }
  {
    var __result6 = Number(Number.MAX_VALUE) !== Number.MAX_VALUE;
    var __expect6 = false;
  }
  {
    var __result7 = Number(Number.MIN_VALUE) !== Number.MIN_VALUE;
    var __expect7 = false;
  }
  