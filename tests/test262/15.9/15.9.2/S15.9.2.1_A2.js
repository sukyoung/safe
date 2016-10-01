  function isEqual(d1, d2) 
  {
    if (d1 === d2)
    {
      return true;
    }
    else
      if (Math.abs(Date.parse(d1) - Date.parse(d2)) <= 1000)
      {
        return true;
      }
      else
      {
        return false;
      }
  }
  {
    var __result1 = ! isEqual(Date(), (new Date()).toString());
    var __expect1 = false;
  }
  {
    var __result2 = ! isEqual(Date(1), (new Date()).toString());
    var __expect2 = false;
  }
  {
    var __result3 = ! isEqual(Date(1970, 1), (new Date()).toString());
    var __expect3 = false;
  }
  {
    var __result4 = ! isEqual(Date(1970, 1, 1), (new Date()).toString());
    var __expect4 = false;
  }
  {
    var __result5 = ! isEqual(Date(1970, 1, 1, 1), (new Date()).toString());
    var __expect5 = false;
  }
  {
    var __result6 = ! isEqual(Date(1970, 1, 1, 1), (new Date()).toString());
    var __expect6 = false;
  }
  {
    var __result7 = ! isEqual(Date(1970, 1, 1, 1, 0), (new Date()).toString());
    var __expect7 = false;
  }
  {
    var __result8 = ! isEqual(Date(1970, 1, 1, 1, 0, 0), (new Date()).toString());
    var __expect8 = false;
  }
  {
    var __result9 = ! isEqual(Date(1970, 1, 1, 1, 0, 0, 0), (new Date()).toString());
    var __expect9 = false;
  }
  {
    var __result10 = ! isEqual(Date(Number.NaN), (new Date()).toString());
    var __expect10 = false;
  }
  {
    var __result11 = ! isEqual(Date(Number.POSITIVE_INFINITY), (new Date()).toString());
    var __expect11 = false;
  }
  {
    var __result12 = ! isEqual(Date(Number.NEGATIVE_INFINITY), (new Date()).toString());
    var __expect12 = false;
  }
  {
    var __result13 = ! isEqual(Date(undefined), (new Date()).toString());
    var __expect13 = false;
  }
  {
    var __result14 = ! isEqual(Date(null), (new Date()).toString());
    var __expect14 = false;
  }
  