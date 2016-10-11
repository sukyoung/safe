  if (Number("") !== 0)
  {
    $ERROR('#1.1: Number("") === 0. Actual: ' + (Number("")));
  }
  else
  {
    {
      var __result1 = 1 / Number("") !== Number.POSITIVE_INFINITY;
      var __expect1 = false;
    }
  }
  