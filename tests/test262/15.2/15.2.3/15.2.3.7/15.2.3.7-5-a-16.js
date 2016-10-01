  function testcase() 
  {
    var obj = {
      
    };
    var props = new Error("test");
    var obj1 = {
      value : 11
    };
    props.message = obj1;
    props.name = obj1;
    props.description = obj1;
    props.prop = {
      value : 16
    };
    Object.defineProperties(obj, props);
    return obj.hasOwnProperty("prop") && obj.prop === 16;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  