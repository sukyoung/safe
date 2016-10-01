  var __string = new String('this is a string object');
  {
    var __result1 = __string.substring(NaN, Infinity) !== "this is a string object";
    var __expect1 = false;
  }
  