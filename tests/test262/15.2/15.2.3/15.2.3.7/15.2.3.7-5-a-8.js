  function testcase() 
  {
    var obj = {
      
    };
    var props = [];
    var descObj = {
      value : 8
    };
    Object.defineProperty(props, "prop", {
      value : descObj,
      enumerable : true
    });
    Object.defineProperties(obj, props);
    return obj.hasOwnProperty("prop") && obj.prop === 8;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  