  function testcase() 
  {
    var accessed = false;
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
    }
    var proto = {
      
    };
    Object.defineProperty(proto, "length", {
      set : (function () 
      {
        
      }),
      configurable : true
    });
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var child = new Con();
    child[0] = 11;
    child[1] = 12;
    Array.prototype.forEach.call(child, callbackfn);
    return ! accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  