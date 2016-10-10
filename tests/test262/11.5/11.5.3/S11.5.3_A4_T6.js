  if (0 % 1 !== 0)
  {
    $ERROR('#1.1: 0 % 1 === 0. Actual: ' + (0 % 1));
  }
  else
  {
    {
      var __result1 = 1 / (0 % 1) !== Number.POSITIVE_INFINITY;
      var __expect1 = false;
    }
  }
  if (0 % - 1 !== 0)
  {
    $ERROR('#2.1: 0 % -1 === 0. Actual: ' + (0 % - 1));
  }
  else
  {
    {
      var __result2 = 1 / (0 % - 1) !== Number.POSITIVE_INFINITY;
      var __expect2 = false;
    }
  }
  if (- 0 % 1 !== - 0)
  {
    $ERROR('#3.1: -0 % 1 === 0. Actual: ' + (- 0 % 1));
  }
  else
  {
    {
      var __result3 = 1 / (- 0 % 1) !== Number.NEGATIVE_INFINITY;
      var __expect3 = false;
    }
  }
  if (- 0 % - 1 !== - 0)
  {
    $ERROR('#4.1: -0 % -1 === 0. Actual: ' + (- 0 % - 1));
  }
  else
  {
    {
      var __result4 = 1 / (- 0 % - 1) !== Number.NEGATIVE_INFINITY;
      var __expect4 = false;
    }
  }
  if (0 % Number.MAX_VALUE !== 0)
  {
    $ERROR('#5.1: 0 % Number.MAX_VALUE === 0. Actual: ' + (0 % Number.MAX_VALUE));
  }
  else
  {
    {
      var __result5 = 1 / (0 % Number.MAX_VALUE) !== Number.POSITIVE_INFINITY;
      var __expect5 = false;
    }
  }
  if (0 % Number.MIN_VALUE !== 0)
  {
    $ERROR('#6.1: 0 % Number.MIN_VALUE === 0. Actual: ' + (0 % Number.MIN_VALUE));
  }
  else
  {
    {
      var __result6 = 1 / (0 % Number.MIN_VALUE) !== Number.POSITIVE_INFINITY;
      var __expect6 = false;
    }
  }
  if (- 0 % Number.MAX_VALUE !== - 0)
  {
    $ERROR('#7.1: -0 % Number.MAX_VALUE === 0. Actual: ' + (- 0 % Number.MAX_VALUE));
  }
  else
  {
    {
      var __result7 = 1 / (- 0 % Number.MAX_VALUE) !== Number.NEGATIVE_INFINITY;
      var __expect7 = false;
    }
  }
  if (- 0 % Number.MIN_VALUE !== - 0)
  {
    $ERROR('#8.1: -0 % Number.MIN_VALUE === 0. Actual: ' + (- 0 % Number.MIN_VALUE));
  }
  else
  {
    {
      var __result8 = 1 / (- 0 % Number.MIN_VALUE) !== Number.NEGATIVE_INFINITY;
      var __expect8 = false;
    }
  }
  