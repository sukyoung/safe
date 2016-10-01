  function testcase() 
  {
    var obj = {
      
    };
    var props = {
      
    };
    Object.defineProperty(props, "prop", {
      value : {
        
      },
      enumerable : false
    });
    Object.defineProperties(obj, props);
    return ! obj.hasOwnProperty("prop");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  