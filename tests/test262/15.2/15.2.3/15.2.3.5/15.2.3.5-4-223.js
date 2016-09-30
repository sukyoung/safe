  function testcase() 
  {
    var newObj = Object.create({
      
    }, {
      prop : {
        writable : new RegExp()
      }
    });
    var hasProperty = newObj.hasOwnProperty("prop");
    newObj.prop = 121;
    return hasProperty && newObj.prop === 121;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  