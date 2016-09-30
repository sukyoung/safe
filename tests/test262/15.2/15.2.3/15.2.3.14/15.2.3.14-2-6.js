  function testcase() 
  {
    var o = {
      x : 1,
      y : 2
    };
    var a = Object.keys(o);
    if (Object.isFrozen(a) === false)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  