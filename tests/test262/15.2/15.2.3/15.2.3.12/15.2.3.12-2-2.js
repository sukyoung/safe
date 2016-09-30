  function testcase() 
  {
    var proto = {
      
    };
    function get_func() 
    {
      return 10;
    }
    function set_func() 
    {
      
    }
    Object.defineProperty(proto, "Father", {
      get : get_func,
      set : set_func,
      configurable : true
    });
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var child = new Con();
    Object.preventExtensions(child);
    return Object.isFrozen(child);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  