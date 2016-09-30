  function testcase() 
  {
    var booleanObj = new Boolean(false);
    booleanObj.value = "BooleanValue";
    var newObj = Object.create({
      
    }, {
      prop : booleanObj
    });
    return newObj.prop === "BooleanValue";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  