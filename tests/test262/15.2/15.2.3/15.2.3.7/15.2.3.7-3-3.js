  function testcase() 
  {
    var obj = {
      
    };
    var proto = {
      
    };
    Object.defineProperty(proto, "prop", {
      value : {
        
      },
      enumerable : true
    });
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var child = new Con();
    Object.defineProperties(obj, child);
    return ! obj.hasOwnProperty("prop");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  