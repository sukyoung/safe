var __result1 = false;
  if (- 0 / 1 !== - 0)
  {
var __result1 = true;
  }
  else
  {
    {
      var __result1 = 1 / (- 0 / 1) !== Number.NEGATIVE_INFINITY;
      var __expect1 = false;
    }
  }

var __result2 = false;
  if (- 0 / - 1 !== + 0)
  {
var __result2 = true;
  }
  else
  {
    {
      var __result2 = 1 / (- 0 / - 1) !== Number.POSITIVE_INFINITY;
      var __expect2 = false;
    }
  }

var __result3 = false;
  if (+ 0 / 1 !== + 0)
  {
      var __result3 = true;
  }
  else
  {
    {
      var __result3 = 1 / (+ 0 / - 1) !== Number.NEGATIVE_INFINITY;
      var __expect3 = false;
    }
  }

var __result4 = false;
  if (+ 0 / - 1 !== - 0)
  {
      var __result4 = true;
  }
  else
  {
    {
      var __result4 = 1 / (+ 0 / - 1) !== Number.NEGATIVE_INFINITY;
      var __expect4 = false;
    }
  }

var __result5 = false;
  if (+ 0 / - Number.MAX_VALUE !== - 0)
  {
      var __result5 = true;
  }
  else
  {
    {
      var __result5 = 1 / (+ 0 / - Number.MAX_VALUE) !== Number.NEGATIVE_INFINITY;
      var __expect5 = false;
    }
  }

var __result6 = false;
  if (- 0 / Number.MIN_VALUE !== - 0)
  {
      var __result6 = true;
  }
  else
  {
    {
      var __result6 = 1 / (- 0 / Number.MIN_VALUE) !== Number.NEGATIVE_INFINITY;
      var __expect6 = false;
    }
  }
