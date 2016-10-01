  function testcase() 
  {
    var obj = {
      
    };
    obj.foo = 10;
    var preCheck = Object.isExtensible(obj);
    Object.seal(obj);
    delete obj.foo;
    return preCheck && obj.foo === 10;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  