  function testcase() 
  {
    var o = {
      
    };
    Object.defineProperty(o, "foo", {
      value : 42,
      configurable : true,
      enumerable : true
    });
    return o.hasOwnProperty("foo");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  