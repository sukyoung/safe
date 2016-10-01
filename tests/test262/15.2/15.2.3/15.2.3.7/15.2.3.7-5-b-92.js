  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperties(obj, {
      property : {
        configurable : NaN
      }
    });
    var hadOwnProperty = obj.hasOwnProperty("property");
    delete obj.property;
    return obj.hasOwnProperty("property") && hadOwnProperty;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  