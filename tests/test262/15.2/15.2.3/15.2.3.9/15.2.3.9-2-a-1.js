  function testcase() 
  {
    var obj = {
      
    };
    obj.foo = 10;
    Object.freeze(obj);
    var desc = Object.getOwnPropertyDescriptor(obj, "foo");
    delete obj.foo;
    return obj.foo === 10 && desc.configurable === false && desc.writable === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  