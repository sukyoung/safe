  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    var proto = {
      enumerable : true
    };
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var descObj = new Con();
    Object.defineProperties(obj, {
      prop : descObj
    });
    for(var property in obj)
    {
      if (property === "prop")
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
  