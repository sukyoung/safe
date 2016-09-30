  function testcase() 
  {
    var errObj = new SyntaxError();
    Object.freeze(errObj);
    return Object.isFrozen(errObj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  