  {
    var __result1 = ! (String.hasOwnProperty('prototype'));
    var __expect1 = false;
  }
  delete String.prototype;
  {
    var __result2 = ! (String.hasOwnProperty('prototype'));
    var __expect2 = false;
  }
  