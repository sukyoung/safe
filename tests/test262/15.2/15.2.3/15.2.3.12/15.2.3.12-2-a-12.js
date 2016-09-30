  function testcase() 
  {
    var obj = new String("abc");
    obj.len = 100;
    Object.preventExtensions(obj);
    return ! Object.isFrozen(obj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  