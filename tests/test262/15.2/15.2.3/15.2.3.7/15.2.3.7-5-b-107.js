  function testcase() 
  {
    var obj = {
      
    };
    var func = (function (a, b, c) 
    {
      return arguments;
    });
    var args = func(1, true, "a");
    Object.defineProperties(obj, {
      property : {
        configurable : args
      }
    });
    var preCheck = obj.hasOwnProperty("property");
    delete obj.property;
    return preCheck && ! obj.hasOwnProperty("property");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  