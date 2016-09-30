  function testcase() 
  {
    var boolObj = new Boolean(false);
    var preCheck = Object.isExtensible(boolObj);
    Object.seal(boolObj);
    return preCheck && Object.isSealed(boolObj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  