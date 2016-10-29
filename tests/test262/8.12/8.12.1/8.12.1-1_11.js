  function testcase() 
  {
    var o = {
      
    };
    Object.defineProperty(o, "foo", {
      value : 42,
      writable : true,
      enumerable : true,
      configurable : true
    });
    return o.hasOwnProperty("foo");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  