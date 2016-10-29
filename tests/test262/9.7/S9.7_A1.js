  if (String.fromCharCode(Number.NaN).charCodeAt(0) !== + 0)
  {
    $ERROR('#1.1: String.fromCharCode(Number.NaN).charCodeAt(0) === 0. Actual: ' + (String.fromCharCode(Number.NaN).charCodeAt(0)));
  }
  else
  {
    var __result1 = 1 / String.fromCharCode(Number.NaN).charCodeAt(0) !== Number.POSITIVE_INFINITY;
    var __expect1 = false;
  }
  if (String.fromCharCode(Number("abc")).charCodeAt(0) !== + 0)
  {
    $ERROR('#2.1: String.fromCharCode(Number("abc")).charCodeAt(0) === 0. Actual: ' + (String.fromCharCode(Number("abc")).charCodeAt(0)));
  }
  else
  {
    var __result2 = 1 / String.fromCharCode(0).charCodeAt(0) !== Number.POSITIVE_INFINITY;
    var __expect2 = false;
  }
  if (String.fromCharCode(0).charCodeAt(0) !== + 0)
  {
    $ERROR('#3.1: String.fromCharCode(0).charCodeAt(0) === 0. Actual: ' + (String.fromCharCode(0).charCodeAt(0)));
  }
  else
  {
    var __result3 = 1 / String.fromCharCode(0).charCodeAt(0) !== Number.POSITIVE_INFINITY;
    var __expect3 = false;
  }
  if (String.fromCharCode(- 0).charCodeAt(0) !== + 0)
  {
    $ERROR("#4.1: String.fromCharCode(-0).charCodeAt(0) === +0");
  }
  else
  {
    var __result4 = 1 / String.fromCharCode(- 0).charCodeAt(0) !== Number.POSITIVE_INFINITY;
    var __expect4 = false;
  }
  if (String.fromCharCode(Number.POSITIVE_INFINITY).charCodeAt(0) !== + 0)
  {
    $ERROR('#5.1: String.fromCharCode(Number.POSITIVE_INFINITY).charCodeAt(0) === 0. Actual: ' + (String.fromCharCode(Number.POSITIVE_INFINITY).charCodeAt(0)));
  }
  else
  {
    var __result5 = 1 / String.fromCharCode(Number.POSITIVE_INFINITY).charCodeAt(0) !== Number.POSITIVE_INFINITY;
    var __expect5 = false;
  }
  if (String.fromCharCode(Number.NEGATIVE_INFINITY).charCodeAt(0) !== + 0)
  {
    $ERROR("#6.1: String.fromCharCode(Number.NEGATIVE_INFINITY).charCodeAt(0) === +0");
  }
  else
  {
    var __result6 = 1 / String.fromCharCode(Number.NEGATIVE_INFINITY).charCodeAt(0) !== Number.POSITIVE_INFINITY;
    var __expect6 = false;
  }
  