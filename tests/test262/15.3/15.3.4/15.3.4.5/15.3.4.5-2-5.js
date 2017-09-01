  function testcase() 
  {
    var bbc = Boolean.bind(null);
    var b = bbc(true);
    if (b === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  