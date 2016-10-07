  if (Number.MIN_VALUE - Number.MIN_VALUE !== + 0)
  {
    $ERROR('#1.1: Number.MIN_VALUE - Number.MIN_VALUE === 0. Actual: ' + (Number.MIN_VALUE - Number.MIN_VALUE));
  }
  else
  {
    {
      var __result1 = 1 / (Number.MIN_VALUE - Number.MIN_VALUE) !== Number.POSITIVE_INFINITY;
      var __expect1 = false;
    }
  }
  if (- Number.MAX_VALUE - - Number.MAX_VALUE !== + 0)
  {
    $ERROR('#2.2: -Number.MAX_VALUE - -Number.MAX_VALUE === 0. Actual: ' + (- Number.MAX_VALUE - - Number.MAX_VALUE));
  }
  else
  {
    {
      var __result2 = 1 / (- Number.MAX_VALUE - - Number.MAX_VALUE) !== Number.POSITIVE_INFINITY;
      var __expect2 = false;
    }
  }
  if (1 / Number.MAX_VALUE - 1 / Number.MAX_VALUE !== + 0)
  {
    $ERROR('#3.1: 1 / Number.MAX_VALUE - 1 / Number.MAX_VALUE === 0. Actual: ' + (1 / Number.MAX_VALUE - 1 / Number.MAX_VALUE));
  }
  else
  {
    {
      var __result3 = 1 / (1 / Number.MAX_VALUE - 1 / Number.MAX_VALUE) !== Number.POSITIVE_INFINITY;
      var __expect3 = false;
    }
  }
  