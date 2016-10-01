  function testcase() 
  {
    var numObj = new Number(3);
    var preCheck = Object.isExtensible(numObj);
    Object.seal(numObj);
    return preCheck && Object.isSealed(numObj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  