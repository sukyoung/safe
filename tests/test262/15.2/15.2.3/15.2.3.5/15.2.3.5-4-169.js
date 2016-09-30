  function testcase() 
  {
    var numObj = new Number(123);
    numObj.value = "NumValue";
    var newObj = Object.create({
      
    }, {
      prop : numObj
    });
    return newObj.prop === "NumValue";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  