  {
    var __result1 = (true ? false : true) !== false;
    var __expect1 = false;
  }
  {
    var __result2 = (false ? false : true) !== true;
    var __expect2 = false;
  }
  var x = new Boolean(true);
  var y = new Boolean(false);
  {
    var __result3 = (x ? y : true) !== y;
    var __expect3 = false;
  }
  var z = new Boolean(true);
  {
    var __result4 = (false ? false : z) !== z;
    var __expect4 = false;
  }
  var x = new Boolean(true);
  var y = new Boolean(false);
  var z = new Boolean(true);
  {
    var __result5 = (x ? y : z) !== y;
    var __expect5 = false;
  }
  var x = false;
  var y = new Boolean(false);
  var z = new Boolean(true);
  {
    var __result6 = (x ? y : z) !== z;
    var __expect6 = false;
  }
  