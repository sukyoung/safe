  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "foo", {
      value : NaN,
      writable : false,
      configurable : false
    });
    Object.defineProperty(obj, "foo", {
      value : NaN,
      writable : false,
      configurable : false
    });
    if (! isNaN(obj.foo))
    {
      return false;
    }
    obj.foo = "verifyValue";
    if (obj.foo === "verifyValue")
    {
      return false;
    }
    for(var prop in obj)
    {
      if (obj.hasOwnProperty(prop) && prop === "foo")
      {
        return false;
      }
    }
    delete obj.foo;
    if (! obj.hasOwnProperty("foo"))
    {
      return false;
    }
    return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  