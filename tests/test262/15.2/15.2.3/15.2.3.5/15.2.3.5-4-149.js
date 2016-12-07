  function testcase() 
  {
    var newObj = Object.create({
      
    }, {
      prop : {
        configurable : @Global
      }
    });
    var beforeDeleted = newObj.hasOwnProperty("prop");
    delete newObj.prop;
    var afterDeleted = newObj.hasOwnProperty("prop");
    return beforeDeleted === true && afterDeleted === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
