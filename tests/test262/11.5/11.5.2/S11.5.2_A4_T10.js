  {
    var __result1 = Number.MIN_VALUE / 2.1 !== 0;
    var __expect1 = false;
  }
  if (Number.MIN_VALUE / - 2.1 !== - 0)
  {
    $ERROR('#2.1: Number.MIN_VALUE / -2.1 === 0. Actual: ' + (Number.MIN_VALUE / - 2.1));
  }
  else
  {
    {
      var __result2 = 1 / (Number.MIN_VALUE / - 2.1) !== Number.NEGATIVE_INFINITY;
      var __expect2 = false;
    }
  }
  {
    var __result3 = Number.MIN_VALUE / 2.0 !== 0;
    var __expect3 = false;
  }
  if (Number.MIN_VALUE / - 2.0 !== - 0)
  {
    $ERROR('#4.1: Number.MIN_VALUE / -2.0 === -0. Actual: ' + (Number.MIN_VALUE / - 2.0));
  }
  else
  {
    {
      var __result4 = 1 / (Number.MIN_VALUE / - 2.0) !== Number.NEGATIVE_INFINITY;
      var __expect4 = false;
    }
  }
  {
    var __result5 = Number.MIN_VALUE / 1.9 !== Number.MIN_VALUE;
    var __expect5 = false;
  }
  {
    var __result6 = Number.MIN_VALUE / - 1.9 !== - Number.MIN_VALUE;
    var __expect6 = false;
  }
  {
    var __result7 = Number.MIN_VALUE / 1.1 !== Number.MIN_VALUE;
    var __expect7 = false;
  }
  {
    var __result8 = Number.MIN_VALUE / - 1.1 !== - Number.MIN_VALUE;
    var __expect8 = false;
  }
  