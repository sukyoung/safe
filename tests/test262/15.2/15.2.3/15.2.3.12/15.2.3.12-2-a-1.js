  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "foo", {
      value : 12,
      writable : true,
      configurable : false
    });
    Object.preventExtensions(obj);
    return ! Object.isFrozen(obj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  