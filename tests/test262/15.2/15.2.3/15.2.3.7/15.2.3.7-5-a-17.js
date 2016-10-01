  function testcase() 
  {
    var obj = {
      
    };
    var arg;
    (function fun() 
    {
      arg = arguments;
    })();
    Object.defineProperty(arg, "prop", {
      value : {
        value : 17
      },
      enumerable : true
    });
    Object.defineProperties(obj, arg);
    return obj.hasOwnProperty("prop") && obj.prop === 17;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  