  {
    var __result1 = 1 * 1 !== 1;
    var __expect1 = false;
  }
  {
    var __result2 = 1 * - 1 !== - 1;
    var __expect2 = false;
  }
  {
    var __result3 = - 1 * 1 !== - 1;
    var __expect3 = false;
  }
  {
    var __result4 = - 1 * - 1 !== 1;
    var __expect4 = false;
  }
  if (0 * 0 !== 0)
  {
    $ERROR('#5.1: 0 * 0 === 0. Actual: ' + (0 * 0));
  }
  else
  {
    {
      var __result5 = 1 / (0 * 0) !== Number.POSITIVE_INFINITY;
      var __expect5 = false;
    }
  }
  if (0 * - 0 !== - 0)
  {
    $ERROR('#6.1: 0 * -0 === 0. Actual: ' + (0 * - 0));
  }
  else
  {
    {
      var __result6 = 1 / (0 * - 0) !== Number.NEGATIVE_INFINITY;
      var __expect6 = false;
    }
  }
  if (- 0 * 0 !== - 0)
  {
    $ERROR('#7.1: -0 * 0 === 0. Actual: ' + (- 0 * 0));
  }
  else
  {
    {
      var __result7 = 1 / (- 0 * 0) !== Number.NEGATIVE_INFINITY;
      var __expect7 = false;
    }
  }
  if (- 0 * - 0 !== 0)
  {
    $ERROR('#8.1: -0 * -0 === 0. Actual: ' + (- 0 * - 0));
  }
  else
  {
    {
      var __result8 = 1 / (- 0 * - 0) !== Number.POSITIVE_INFINITY;
      var __expect8 = false;
    }
  }
  