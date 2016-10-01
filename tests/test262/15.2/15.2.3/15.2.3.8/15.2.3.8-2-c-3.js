  function testcase() 
  {
    var strObj = new String("a");
    var preCheck = Object.isExtensible(strObj);
    Object.seal(strObj);
    return preCheck && Object.isSealed(strObj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  