  function testcase() 
  {
    var dateObj = new Date();
    dateObj.value = "DateValue";
    var newObj = Object.create({
      
    }, {
      prop : dateObj
    });
    return newObj.prop === "DateValue";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  