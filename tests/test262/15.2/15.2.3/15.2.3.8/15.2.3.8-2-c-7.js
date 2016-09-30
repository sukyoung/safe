  function testcase() 
  {
    var regObj = new RegExp();
    var preCheck = Object.isExtensible(regObj);
    Object.seal(regObj);
    return preCheck && Object.isSealed(regObj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  