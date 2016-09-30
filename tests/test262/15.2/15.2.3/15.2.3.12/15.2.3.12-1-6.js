  function testcase() 
  {
    var sparseArr = [0, 1, ];
    sparseArr[10000] = 10000;
    sparseArr = Object.freeze(sparseArr);
    return Object.isFrozen(sparseArr);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  