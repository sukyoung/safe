  if (Number(false) !== + 0)
  {
    $ERROR('#1.1: Number(false) === 0. Actual: ' + (Number(false)));
  }
  else
  {
    {
      var __result1 = 1 / Number(false) !== Number.POSITIVE_INFINITY;
      var __expect1 = false;
    }
  }
  {
    var __result2 = Number(true) !== 1;
    var __expect2 = false;
  }
  