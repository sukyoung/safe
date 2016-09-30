  function testcase() 
  {
    var arrObj = [0, 1, ];
    Object.freeze(arrObj);
    return Object.isFrozen(arrObj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  