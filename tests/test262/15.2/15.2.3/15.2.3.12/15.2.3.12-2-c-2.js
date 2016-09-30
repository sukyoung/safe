  function testcase() 
  {
    var obj = {
      
    };
    function get_func() 
    {
      return 10;
    }
    function set_func() 
    {
      
    }
    Object.defineProperty(obj, "foo", {
      get : get_func,
      set : set_func,
      configurable : true
    });
    Object.preventExtensions(obj);
    return ! Object.isFrozen(obj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  