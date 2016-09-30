  function testcase() 
  {
    var errorObj = new Error();
    errorObj.value = "ErrorValue";
    var newObj = Object.create({
      
    }, {
      prop : errorObj
    });
    return newObj.prop === "ErrorValue";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  