  function testcase() 
  {
    var o = {
      
    };
    var desc = {
      value : 1,
      configurable : true
    };
    Object.defineProperty(o, "foo", desc);
    var d = delete o.foo;
    if (d === true && o.hasOwnProperty("foo") === false)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  