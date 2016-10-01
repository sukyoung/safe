  function testcase() 
  {
    var obj = {
      
    };
    Object.freeze(obj);
    return ! Object.isExtensible(obj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  