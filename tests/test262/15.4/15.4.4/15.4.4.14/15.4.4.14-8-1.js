  function testcase() 
  {
    var a = new Array(1, 2, 3);
    if (a.indexOf(2, - 1) === - 1 && a.indexOf(2, - 2) === 1 && a.indexOf(1, - 3) === 0 && a.indexOf(1, - 5.3) === 0)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  