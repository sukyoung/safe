  function testcase() 
  {
    var regObj = new RegExp();
    regObj.value = "RegExpValue";
    var newObj = Object.create({
      
    }, {
      prop : regObj
    });
    return newObj.prop === "RegExpValue";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  