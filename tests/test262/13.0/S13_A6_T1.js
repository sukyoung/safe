  function __func() 
  {
    return 1;
  }
  ;
  var __store__func = __func;
  var __1 = __func();
  function __func() 
  {
    return 'A';
  }
  ;
  var __A = __func();
  {
    var __result1 = __store__func !== __func;
    var __expect1 = false;
  }
  {
    var __result2 = __1 !== __A;
    var __expect2 = false;
  }
  