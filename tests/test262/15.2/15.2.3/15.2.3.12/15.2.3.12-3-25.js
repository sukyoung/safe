  function testcase() 
  {
    var b = Object.isFrozen(TypeError);
    if (b === false)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  