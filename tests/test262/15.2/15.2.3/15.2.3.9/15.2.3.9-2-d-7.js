  function testcase() 
  {
    var regObj = new RegExp();
    Object.freeze(regObj);
    return Object.isFrozen(regObj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  