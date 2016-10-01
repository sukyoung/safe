  function testcase() 
  {
    var obj = {
      
    };
    var props = new Date();
    Object.defineProperty(props, "prop", {
      value : {
        value : 13
      },
      enumerable : true
    });
    Object.defineProperties(obj, props);
    return obj.hasOwnProperty("prop") && obj.prop === 13;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  