  if ((- 0 && - 1) !== 0)
  {
    $ERROR('#1.1: (-0 && -1) === 0');
  }
  else
  {
    {
      var __result1 = (1 / (- 0 && - 1)) !== Number.NEGATIVE_INFINITY;
      var __expect1 = false;
    }
  }
  if ((0 && new Number(- 1)) !== 0)
  {
    $ERROR('#2.1: (0 && new Number(-1)) === 0');
  }
  else
  {
    {
      var __result2 = (1 / (0 && new Number(- 1))) !== Number.POSITIVE_INFINITY;
      var __expect2 = false;
    }
  }
  {
    var __result3 = (isNaN(NaN && 1)) !== true;
    var __expect3 = false;
  }
  