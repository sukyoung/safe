  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "foo1", {
      value : 20,
      writable : false,
      enumerable : false,
      configurable : false
    });
    function get_func() 
    {
      return 10;
    }
    function set_func() 
    {
      
    }
    Object.defineProperty(obj, "foo2", {
      get : get_func,
      set : set_func,
      configurable : false
    });
    Object.preventExtensions(obj);
    return Object.isFrozen(obj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  