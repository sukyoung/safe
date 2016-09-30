  function testcase() 
  {
    var obj = [2, ];
    obj.len = 200;
    Object.preventExtensions(obj);
    return ! Object.isFrozen(obj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  