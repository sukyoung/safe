  function testcase() 
  {
    var obj = {
      
    };
    var desc = {
      value : NaN
    };
    Object.defineProperty(obj, "foo", desc);
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
    var verifyValue = false;
    obj.prop = "overrideData";
    verifyValue = obj.foo !== obj.foo && isNaN(obj.foo);
    var verifyConfigurable = false;
    delete obj.foo;
    verifyConfigurable = obj.hasOwnProperty("foo");
    return verifyConfigurable && ! verifyEnumerable && verifyValue;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  