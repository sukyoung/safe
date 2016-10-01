  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    var proto = {
      
    };
    Object.defineProperty(proto, "enumerable", {
      value : false
    });
    var ConstructFun = (function () 
    {
      
    });
    ConstructFun.prototype = proto;
    var child = new ConstructFun();
    Object.defineProperty(child, "enumerable", {
      value : true
    });
    Object.defineProperty(obj, "property", child);
    for(var prop in obj)
    {
      if (prop === "property")
      {
        accessed = true;
      }
    }
    return accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  