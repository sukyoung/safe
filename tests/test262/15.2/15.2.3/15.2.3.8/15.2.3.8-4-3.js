  function testcase() 
  {
    var obj = {
      
    };
    obj.foo = 10;
    var preCheck = Object.isExtensible(obj);
    Object.preventExtensions(obj);
    Object.seal(obj);
    return preCheck && Object.isSealed(obj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  