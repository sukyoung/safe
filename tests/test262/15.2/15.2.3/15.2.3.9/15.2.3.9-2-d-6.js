  function testcase() 
  {
    var dateObj = new Date();
    Object.freeze(dateObj);
    return Object.isFrozen(dateObj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  