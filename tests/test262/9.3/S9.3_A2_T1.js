  if (Number(null) !== 0)
  {
    $ERROR('#1.1: Number(null) === 0. Actual: ' + (Number(null)));
  }
  else
  {
    {
      var __result1 = 1 / Number(null) !== Number.POSITIVE_INFINITY;
      var __expect1 = false;
    }
  }
  