  var p_zero = + 0;
  var n_zero = - 0;
  {
    var __result1 = (p_zero == n_zero) !== true;
    var __expect1 = false;
  }
  {
    var __result2 = (n_zero == 0) !== true;
    var __expect2 = false;
  }
  {
    var __result3 = (p_zero == - 0) !== true;
    var __expect3 = false;
  }
  {
    var __result4 = (p_zero === 0) !== true;
    var __expect4 = false;
  }
  {
    var __result5 = (n_zero === - 0) !== true;
    var __expect5 = false;
  }
  