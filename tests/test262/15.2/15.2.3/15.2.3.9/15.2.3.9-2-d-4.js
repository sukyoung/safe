  function testcase() 
  {
    var boolObj = new Boolean(false);
    Object.freeze(boolObj);
    return Object.isFrozen(boolObj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  