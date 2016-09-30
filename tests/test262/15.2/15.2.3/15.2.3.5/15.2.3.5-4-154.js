  function testcase() 
  {
    var newObj = Object.create({
      
    }, {
      prop : {
        value : "ownDataProperty"
      }
    });
    return newObj.prop === "ownDataProperty";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  