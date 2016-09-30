  function testcase() 
  {
    var strObj = new String("a");
    Object.freeze(strObj);
    return Object.isFrozen(strObj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  