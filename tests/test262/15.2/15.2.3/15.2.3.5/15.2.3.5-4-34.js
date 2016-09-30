  function testcase() 
  {
    var props = new Date();
    props.prop = {
      value : 12,
      enumerable : true
    };
    var newObj = Object.create({
      
    }, props);
    return newObj.hasOwnProperty("prop");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  