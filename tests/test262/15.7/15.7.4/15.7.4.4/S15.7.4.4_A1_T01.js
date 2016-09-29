  {
    var __result1 = Number.prototype.valueOf() !== 0;
    var __expect1 = false;
  }
  {
    var __result2 = (new Number()).valueOf() !== 0;
    var __expect2 = false;
  }
  {
    var __result3 = (new Number(0)).valueOf() !== 0;
    var __expect3 = false;
  }
  {
    var __result4 = (new Number(- 1)).valueOf() !== - 1;
    var __expect4 = false;
  }
  {
    var __result5 = (new Number(1)).valueOf() !== 1;
    var __expect5 = false;
  }
  {
    var __result6 = ! isNaN((new Number(Number.NaN)).valueOf());
    var __expect6 = false;
  }
  {
    var __result7 = (new Number(Number.POSITIVE_INFINITY)).valueOf() !== Number.POSITIVE_INFINITY;
    var __expect7 = false;
  }
  {
    var __result8 = (new Number(Number.NEGATIVE_INFINITY)).valueOf() !== Number.NEGATIVE_INFINITY;
    var __expect8 = false;
  }
  