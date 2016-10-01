  function testcase() 
  {
    var b = Object.isFrozen(URIError);
    if (b === false)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  