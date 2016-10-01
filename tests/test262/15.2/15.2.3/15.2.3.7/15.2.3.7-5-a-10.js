  function testcase() 
  {
    var obj = {
      
    };
    var props = new Boolean(false);
    Object.defineProperty(props, "prop", {
      value : {
        value : 10
      },
      enumerable : true
    });
    Object.defineProperties(obj, props);
    return obj.hasOwnProperty("prop") && obj.prop === 10;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  