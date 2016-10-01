  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperties(obj, {
      prop : {
        
      }
    });
    var result1 = obj.hasOwnProperty("prop");
    delete obj.prop;
    var result2 = obj.hasOwnProperty("prop");
    return result1 === true && result2 === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  