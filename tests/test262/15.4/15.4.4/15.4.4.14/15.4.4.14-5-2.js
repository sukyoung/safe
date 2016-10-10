  function testcase() 
  {
    var a = new Array(1, 2, 3);
    if (a.indexOf(3, 0.49) === 2 && a.indexOf(1, 0.51) === 0 && a.indexOf(1, 1.51) === - 1)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  