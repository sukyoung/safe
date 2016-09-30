  function testcase() 
  {
    var b = Object.isSealed(Array.prototype);
    if (b === false)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  