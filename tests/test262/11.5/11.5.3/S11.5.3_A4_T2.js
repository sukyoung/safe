  if (1 % 1 !== 0)
  {
    $ERROR('#1.1: 1 % 1 === 0. Actual: ' + (1 % 1));
  }
  else
  {
    {
      var __result1 = 1 / (1 % 1) !== Number.POSITIVE_INFINITY;
      var __expect1 = false;
    }
  }
  if (- 1 % - 1 !== - 0)
  {
    $ERROR('#2.1: -1 % -1 === 0. Actual: ' + (- 1 % - 1));
  }
  else
  {
    {
      var __result2 = 1 / (- 1 % - 1) !== Number.NEGATIVE_INFINITY;
      var __expect2 = false;
    }
  }
  if (- 1 % 1 !== - 0)
  {
    $ERROR('#3.1: -1 % 1 === 0. Actual: ' + (- 1 % 1));
  }
  else
  {
    {
      var __result3 = 1 / (- 1 % 1) !== Number.NEGATIVE_INFINITY;
      var __expect3 = false;
    }
  }
  if (1 % - 1 !== 0)
  {
    $ERROR('#4.1: 1 % -1 === 0. Actual: ' + (1 % - 1));
  }
  else
  {
    {
      var __result4 = 1 / (1 % - 1) !== Number.POSITIVE_INFINITY;
      var __expect4 = false;
    }
  }
  {
    var __result5 = 101 % 51 !== 50;
    var __expect5 = false;
  }
  {
    var __result6 = 101 % - 51 !== 50;
    var __expect6 = false;
  }
  {
    var __result7 = - 101 % 51 !== - 50;
    var __expect7 = false;
  }
  {
    var __result8 = - 101 % - 51 !== - 50;
    var __expect8 = false;
  }
  