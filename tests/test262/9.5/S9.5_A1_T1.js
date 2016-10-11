  if ((Number.NaN << 0) !== + 0)
  {
    $ERROR('#1.1: (Number.NaN << 0) === 0. Actual: ' + (Number.NaN << 0));
  }
  else
  {
    var __result1 = 1 / (Number.NaN << 0) !== Number.POSITIVE_INFINITY;
    var __expect1 = false;
  }
  if ((Number("abc") << 0) !== + 0)
  {
    $ERROR('#2.1: (Number("abc") << 0) === 0. Actual: ' + (Number("abc") << 0));
  }
  else
  {
    var __result2 = 1 / (0 << 0) !== Number.POSITIVE_INFINITY;
    var __expect2 = false;
  }
  if ((0 << 0) !== + 0)
  {
    $ERROR('#3.1: (0 << 0) === 0. Actual: ' + (0 << 0));
  }
  else
  {
    var __result3 = 1 / (0 << 0) !== Number.POSITIVE_INFINITY;
    var __expect3 = false;
  }
  if ((- 0 << 0) !== + 0)
  {
    $ERROR("#4.1: (-0 << 0) === 0");
  }
  else
  {
    var __result4 = 1 / (- 0 << 0) !== Number.POSITIVE_INFINITY;
    var __expect4 = false;
  }
  if ((Number.POSITIVE_INFINITY << 0) !== + 0)
  {
    $ERROR('#5.1: (Number.POSITIVE_INFINITY << 0) === 0. Actual: ' + (Number.POSITIVE_INFINITY << 0));
  }
  else
  {
    var __result5 = 1 / (Number.POSITIVE_INFINITY << 0) !== Number.POSITIVE_INFINITY;
    var __expect5 = false;
  }
  if ((Number.NEGATIVE_INFINITY << 0) !== + 0)
  {
    $ERROR("#6.1: (Number.NEGATIVE_INFINITY << 0) === 0");
  }
  else
  {
    var __result6 = 1 / (Number.NEGATIVE_INFINITY << 0) !== Number.POSITIVE_INFINITY;
    var __expect6 = false;
  }
  