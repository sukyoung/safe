  if (- 0 - - 0 !== 0)
  {
    $ERROR('#1.1: -0 - -0 === 0. Actual: ' + (- 0 - - 0));
  }
  else
  {
    {
      var __result1 = 1 / (- 0 - - 0) !== Number.POSITIVE_INFINITY;
      var __expect1 = false;
    }
  }
  if (0 - - 0 !== 0)
  {
    $ERROR('#2.1: 0 - -0 === 0. Actual: ' + (0 - - 0));
  }
  else
  {
    {
      var __result2 = 1 / (0 - - 0) !== Number.POSITIVE_INFINITY;
      var __expect2 = false;
    }
  }
  if (- 0 - 0 !== - 0)
  {
    $ERROR('#3.1: -0 - 0 === 0. Actual: ' + (- 0 - 0));
  }
  else
  {
    {
      var __result3 = 1 / (- 0 - 0) !== Number.NEGATIVE_INFINITY;
      var __expect3 = false;
    }
  }
  if (0 - 0 !== 0)
  {
    $ERROR('#4.1: 0 - 0 === 0. Actual: ' + (0 - 0));
  }
  else
  {
    {
      var __result4 = 1 / (0 - 0) !== Number.POSITIVE_INFINITY;
      var __expect4 = false;
    }
  }
  