  function DateValue(year, month, date, hours, minutes, seconds, ms) 
  {
    return new Date(year, month, date, hours, minutes, seconds, ms).valueOf();
  }
  {
    var __result1 = ! isNaN(DateValue(1899, 11));
    var __expect1 = false;
  }
  {
    var __result2 = ! isNaN(DateValue(1899, 12));
    var __expect2 = false;
  }
  {
    var __result3 = ! isNaN(DateValue(1900, 0));
    var __expect3 = false;
  }
  {
    var __result4 = ! isNaN(DateValue(1969, 11));
    var __expect4 = false;
  }
  {
    var __result5 = ! isNaN(DateValue(1969, 12));
    var __expect5 = false;
  }
  {
    var __result6 = ! isNaN(DateValue(1970, 0));
    var __expect6 = false;
  }
  {
    var __result7 = ! isNaN(DateValue(1999, 11));
    var __expect7 = false;
  }
  {
    var __result8 = ! isNaN(DateValue(1999, 12));
    var __expect8 = false;
  }
  {
    var __result9 = ! isNaN(DateValue(2000, 0));
    var __expect9 = false;
  }
  {
    var __result10 = ! isNaN(DateValue(2099, 11));
    var __expect10 = false;
  }
  {
    var __result11 = ! isNaN(DateValue(2099, 12));
    var __expect11 = false;
  }
  {
    var __result12 = ! isNaN(DateValue(2100, 0));
    var __expect12 = false;
  }
  