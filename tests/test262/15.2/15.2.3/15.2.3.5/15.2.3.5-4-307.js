  function testcase() 
  {
    var newObj = Object.create({
      
    }, {
      prop : {
        value : 1001,
        configurable : true,
        enumerable : true
      }
    });
    var hasProperty = newObj.hasOwnProperty("prop");
    newObj.prop = 12;
    return hasProperty && newObj.prop === 1001;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  