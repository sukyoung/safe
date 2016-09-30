  function testcase() 
  {
    var obj = {
      
    };
    var preCheck = Object.isExtensible(obj);
    Object.seal(obj);
    return preCheck && ! Object.isExtensible(obj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  