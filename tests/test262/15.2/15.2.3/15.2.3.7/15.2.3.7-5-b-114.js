  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperties(obj, {
      property : {
        value : "ownDataProperty"
      }
    });
    return obj.property === "ownDataProperty";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  