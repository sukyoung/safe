  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    Object.defineProperty(obj, "foo", {
      value : NaN,
      writable : false,
      configurable : false
    });
    Object.defineProperties(obj, {
      foo : {
        value : NaN
      }
    });
    var verifyEnumerable = false;
    for(var p in obj)
    {
      if (p === "foo")
      {
        verifyEnumerable = true;
      }
    }
    obj.prop = "overrideData";
    var verifyValue = false;
    verifyValue = obj.foo !== obj.foo && isNaN(obj.foo);
    var desc = Object.getOwnPropertyDescriptor(obj, "foo");
    var verifyConfigurable = false;
    delete obj.foo;
    verifyConfigurable = obj.hasOwnProperty("foo");
    return verifyValue && ! verifyEnumerable && verifyConfigurable;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  