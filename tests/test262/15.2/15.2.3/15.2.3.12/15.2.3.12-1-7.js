  function testcase() 
  {
    var obj = Object.freeze({
      0 : 0,
      1 : 1,
      1000 : 1000
    });
    return Object.isFrozen(obj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  