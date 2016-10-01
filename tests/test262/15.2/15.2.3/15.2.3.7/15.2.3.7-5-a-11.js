  function testcase() 
  {
    var obj = {
      
    };
    var props = new Number(- 9);
    Object.defineProperty(props, "prop", {
      value : {
        value : 12
      },
      enumerable : true
    });
    Object.defineProperties(obj, props);
    return obj.hasOwnProperty("prop") && obj.prop === 12;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  