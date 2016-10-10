  {
    var __result1 = Number.MIN_VALUE * 0.1 !== 0;
    var __expect1 = false;
  }
  if (- 0.1 * Number.MIN_VALUE !== - 0)
  {
    $ERROR('#2.1: -0.1 * Number.MIN_VALUE === -0. Actual: ' + (- 0.1 * Number.MIN_VALUE));
  }
  else
  {
    {
      var __result2 = 1 / (- 0.1 * Number.MIN_VALUE) !== Number.NEGATIVE_INFINITY;
      var __expect2 = false;
    }
  }
  {
    var __result3 = Number.MIN_VALUE * 0.5 !== 0;
    var __expect3 = false;
  }
  if (- 0.5 * Number.MIN_VALUE !== - 0)
  {
    $ERROR('#4.1: -0.5 * Number.MIN_VALUE === -0. Actual: ' + (- 0.5 * Number.MIN_VALUE));
  }
  else
  {
    {
      var __result4 = 1 / (- 0.5 * Number.MIN_VALUE) !== Number.NEGATIVE_INFINITY;
      var __expect4 = false;
    }
  }
  {
    var __result5 = Number.MIN_VALUE * 0.51 !== Number.MIN_VALUE;
    var __expect5 = false;
  }
  {
    var __result6 = - 0.51 * Number.MIN_VALUE !== - Number.MIN_VALUE;
    var __expect6 = false;
  }
  {
    var __result7 = Number.MIN_VALUE * 0.9 !== Number.MIN_VALUE;
    var __expect7 = false;
  }
  {
    var __result8 = - 0.9 * Number.MIN_VALUE !== - Number.MIN_VALUE;
    var __expect8 = false;
  }
  