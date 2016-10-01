  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperties(obj, {
      prop : {
        value : 1001
      }
    });
    obj.prop = 1002;
    return obj.hasOwnProperty("prop") && obj.prop === 1001;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  