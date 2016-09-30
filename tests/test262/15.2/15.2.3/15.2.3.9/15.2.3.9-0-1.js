  function testcase() 
  {
    var f = Object.freeze;
    if (typeof (f) === "function")
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  