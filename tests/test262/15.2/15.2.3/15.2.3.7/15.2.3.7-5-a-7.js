  function testcase() 
  {
    var obj = {
      
    };
    var props = (function () 
    {
      
    });
    Object.defineProperty(props, "prop", {
      value : {
        value : 7
      },
      enumerable : true
    });
    Object.defineProperties(obj, props);
    return obj.hasOwnProperty("prop") && obj.prop === 7;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  