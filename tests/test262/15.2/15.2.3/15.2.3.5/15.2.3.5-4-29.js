  function testcase() 
  {
    var props = [];
    props.prop = {
      value : {
        
      },
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
  