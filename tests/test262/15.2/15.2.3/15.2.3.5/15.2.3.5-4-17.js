  function testcase() 
  {
    var props = {
      
    };
    Object.defineProperty(props, "prop", {
      value : {
        
      },
      enumerable : false
    });
    var newObj = Object.create({
      
    }, props);
    return ! newObj.hasOwnProperty("prop");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  