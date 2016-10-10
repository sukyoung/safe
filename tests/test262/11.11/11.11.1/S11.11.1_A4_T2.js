  if ((- 1 && - 0) !== 0)
  {
    $ERROR('#1.1: (-1 && -0) === 0');
  }
  else
  {
    {
      var __result1 = (1 / (- 1 && - 0)) !== Number.NEGATIVE_INFINITY;
      var __expect1 = false;
    }
  }
  if ((- 1 && 0) !== 0)
  {
    $ERROR('#2.1: (-1 && 0) === 0');
  }
  else
  {
    {
      var __result2 = (1 / (- 1 && 0)) !== Number.POSITIVE_INFINITY;
      var __expect2 = false;
    }
  }
  {
    var __result3 = (isNaN(0.1 && NaN)) !== true;
    var __expect3 = false;
  }
  var y = new Number(0);
  {
    var __result4 = (new Number(- 1) && y) !== y;
    var __expect4 = false;
  }
  var y = new Number(NaN);
  {
    var __result5 = (new Number(0) && y) !== y;
    var __expect5 = false;
  }
  var y = new Number(- 1);
  {
    var __result6 = (new Number(NaN) && y) !== y;
    var __expect6 = false;
  }
  