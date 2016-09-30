  function testcase() 
  {
    var obj = (function () 
    {
      
    });
    Object.defineProperty(obj, "property", {
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
  