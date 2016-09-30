  function testcase() 
  {
    var obj = {
      prop : 12
    };
    var preCheck = Object.isExtensible(obj);
    Object.preventExtensions(obj);
    obj.prop = - 1;
    return preCheck && obj.prop === - 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  