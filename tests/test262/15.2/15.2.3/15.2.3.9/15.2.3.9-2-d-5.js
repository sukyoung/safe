  function testcase() 
  {
    var numObj = new Number(3);
    Object.freeze(numObj);
    return Object.isFrozen(numObj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  