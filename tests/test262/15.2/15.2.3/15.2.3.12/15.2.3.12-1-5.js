  function testcase() 
  {
    var obj = Object.freeze([0, 1, 2, ]);
    return Object.isFrozen(obj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  