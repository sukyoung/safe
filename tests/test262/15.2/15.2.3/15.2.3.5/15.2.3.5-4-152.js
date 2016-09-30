  function testcase() 
  {
    var newObj = Object.create({
      
    }, {
      prop : {
        value : 100
      }
    });
    return newObj.prop === 100;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  