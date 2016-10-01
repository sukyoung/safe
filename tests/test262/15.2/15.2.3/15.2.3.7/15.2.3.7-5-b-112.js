  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperties(obj, {
      property : {
        value : 300
      }
    });
    return obj.property === 300;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  