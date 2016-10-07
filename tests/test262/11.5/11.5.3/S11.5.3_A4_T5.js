  {
    var __result1 = 1 % Number.NEGATIVE_INFINITY !== 1;
    var __expect1 = false;
  }
  {
    var __result2 = 1 % Number.POSITIVE_INFINITY !== 1;
    var __expect2 = false;
  }
  {
    var __result3 = - 1 % Number.POSITIVE_INFINITY !== - 1;
    var __expect3 = false;
  }
  {
    var __result4 = - 1 % Number.NEGATIVE_INFINITY !== - 1;
    var __expect4 = false;
  }
  if (0 % Number.POSITIVE_INFINITY !== 0)
  {
    $ERROR('#5.1: 0 % Infinity === 0. Actual: ' + (0 % Infinity));
  }
  else
  {
    {
      var __result5 = 1 / (0 % Number.POSITIVE_INFINITY) !== Number.POSITIVE_INFINITY;
      var __expect5 = false;
    }
  }
  if (0 % Number.NEGATIVE_INFINITY !== 0)
  {
    $ERROR('#6.1: 0 % -Infinity === 0. Actual: ' + (0 % - Infinity));
  }
  else
  {
    {
      var __result6 = 1 / (0 % Number.NEGATIVE_INFINITY) !== Number.POSITIVE_INFINITY;
      var __expect6 = false;
    }
  }
  if (- 0 % Number.POSITIVE_INFINITY !== - 0)
  {
    $ERROR('#7.1: -0 % Infinity === 0. Actual: ' + (- 0 % Infinity));
  }
  else
  {
    {
      var __result7 = 1 / (- 0 % Number.POSITIVE_INFINITY) !== Number.NEGATIVE_INFINITY;
      var __expect7 = false;
    }
  }
  if (- 0 % Number.NEGATIVE_INFINITY !== - 0)
  {
    $ERROR('#8.1: -0 % -Infinity === 0. Actual: ' + (- 0 % - Infinity));
  }
  else
  {
    {
      var __result8 = 1 / (- 0 % Number.NEGATIVE_INFINITY) !== Number.NEGATIVE_INFINITY;
      var __expect8 = false;
    }
  }
  {
    var __result9 = Number.MAX_VALUE % Number.NEGATIVE_INFINITY !== Number.MAX_VALUE;
    var __expect9 = false;
  }
  {
    var __result10 = Number.MAX_VALUE % Number.POSITIVE_INFINITY !== Number.MAX_VALUE;
    var __expect10 = false;
  }
  {
    var __result11 = - Number.MAX_VALUE % Number.POSITIVE_INFINITY !== - Number.MAX_VALUE;
    var __expect11 = false;
  }
  {
    var __result12 = - Number.MAX_VALUE % Number.NEGATIVE_INFINITY !== - Number.MAX_VALUE;
    var __expect12 = false;
  }
  {
    var __result13 = Number.MIN_VALUE % Number.NEGATIVE_INFINITY !== Number.MIN_VALUE;
    var __expect13 = false;
  }
  {
    var __result14 = Number.MIN_VALUE % Number.POSITIVE_INFINITY !== Number.MIN_VALUE;
    var __expect14 = false;
  }
  {
    var __result15 = - Number.MIN_VALUE % Number.POSITIVE_INFINITY !== - Number.MIN_VALUE;
    var __expect15 = false;
  }
  {
    var __result16 = - Number.MIN_VALUE % Number.NEGATIVE_INFINITY !== - Number.MIN_VALUE;
    var __expect16 = false;
  }
  