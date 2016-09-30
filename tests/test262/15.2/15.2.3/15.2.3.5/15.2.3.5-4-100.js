  function testcase() 
  {
    var newObj = Object.create({
      
    }, {
      prop : {
        value : "ownDataProperty"
      }
    });
    var result1 = newObj.hasOwnProperty("prop");
    delete newObj.prop;
    var result2 = newObj.hasOwnProperty("prop");
    return result1 === true && result2 === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  