  function testcase() 
  {
    var obj = {
      
    };
    var props = new String();
    Object.defineProperty(props, "prop", {
      value : {
        value : 9
      },
      enumerable : true
    });
    Object.defineProperties(obj, props);
    return obj.hasOwnProperty("prop") && obj.prop === 9;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  