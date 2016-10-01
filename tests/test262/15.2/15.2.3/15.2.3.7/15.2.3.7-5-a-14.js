  function testcase() 
  {
    var obj = {
      
    };
    var props = new RegExp();
    Object.defineProperty(props, "prop", {
      value : {
        value : 14
      },
      enumerable : true
    });
    Object.defineProperties(obj, props);
    return obj.hasOwnProperty("prop") && obj.prop === 14;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  