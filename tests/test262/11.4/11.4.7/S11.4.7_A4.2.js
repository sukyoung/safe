  var x = 0;
  x = - x;
  if (x !== - 0)
  {
    $ERROR('#1.1: var x = 0; x = -x; x === 0. Actual: ' + (x));
  }
  else
  {
    {
      var __result1 = 1 / x !== Number.NEGATIVE_INFINITY;
      var __expect1 = false;
    }
  }
  var x = - 0;
  x = - x;
  if (x !== 0)
  {
    $ERROR('#2.1: var x = -0; x = -x; x === 0. Actual: ' + (x));
  }
  else
  {
    {
      var __result2 = 1 / x !== Number.POSITIVE_INFINITY;
      var __expect2 = false;
    }
  }
  