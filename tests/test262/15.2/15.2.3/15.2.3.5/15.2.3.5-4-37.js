  function testcase() 
  {
    var props = new Error("test");
    (Object.getOwnPropertyNames(props)).forEach((function (name) 
    {
      props[name] = {
        value : 11,
        configurable : true
      };
    }));
    props.prop15_2_3_5_4_37 = {
      value : 12,
      enumerable : true
    };
    var newObj = Object.create({
      
    }, props);
    return newObj.hasOwnProperty("prop15_2_3_5_4_37");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  