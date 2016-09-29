  function testcase() 
  {
    var obj = {
      
    };
    Object.preventExtensions(obj);
    return ! Object.isExtensible(obj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  