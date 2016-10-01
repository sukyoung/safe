  function testcase() 
  {
    var o = new Object();
    o[12] = 13;
    var b = Array.isArray(o);
    if (b === false)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  