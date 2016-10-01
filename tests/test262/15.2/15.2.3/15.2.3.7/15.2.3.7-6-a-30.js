  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperties(obj, {
      prop : {
        value : 1001
      }
    });
    delete obj.prop;
    return obj.hasOwnProperty("prop") && obj.prop === 1001;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  