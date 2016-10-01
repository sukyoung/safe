  function testcase() 
  {
    var obj = {
      
    };
    obj.foo = 10;
    Object.freeze(obj);
    Object.freeze(obj);
    return Object.isFrozen(obj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  