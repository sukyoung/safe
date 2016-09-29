  var __string = new String('this is a string object');
  {
    var __result1 = __string.slice(NaN, Infinity) !== "this is a string object";
    var __expect1 = false;
  }
  