  function f1() 
  {
    return arguments.length;
  }
  {
    var __result1 = ! (f1() === 0);
    var __expect1 = false;
  }
  {
    var __result2 = ! (f1(0) === 1);
    var __expect2 = false;
  }
  {
    var __result3 = ! (f1(0, 1) === 2);
    var __expect3 = false;
  }
  {
    var __result4 = ! (f1(0, 1, 2) === 3);
    var __expect4 = false;
  }
  {
    var __result5 = ! (f1(0, 1, 2, 3) === 4);
    var __expect5 = false;
  }
  var f2 = (function () 
  {
    return arguments.length;
  });
  {
    var __result6 = ! (f2() === 0);
    var __expect6 = false;
  }
  {
    var __result7 = ! (f2(0) === 1);
    var __expect7 = false;
  }
  {
    var __result8 = ! (f2(0, 1) === 2);
    var __expect8 = false;
  }
  {
    var __result9 = ! (f2(0, 1, 2) === 3);
    var __expect9 = false;
  }
  {
    var __result10 = ! (f2(0, 1, 2, 3) === 4);
    var __expect10 = false;
  }
  