  function testcase() 
  {
    var errObj = new Error();
    var preCheck = Object.isExtensible(errObj);
    Object.seal(errObj);
    return preCheck && Object.isSealed(errObj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  