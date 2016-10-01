  function testcase() 
  {
    var arr = [0, 1, ];
    var preCheck = Object.isExtensible(arr);
    Object.seal(arr);
    return preCheck && Object.isSealed(arr);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  