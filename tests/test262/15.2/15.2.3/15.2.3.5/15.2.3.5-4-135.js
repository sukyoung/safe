  function testcase() 
  {
    var newObj = Object.create({
      
    }, {
      prop : {
        configurable : ""
      }
    });
    var beforeDeleted = newObj.hasOwnProperty("prop");
    delete newObj.prop;
    var afterDeleted = newObj.hasOwnProperty("prop");
    return beforeDeleted === true && afterDeleted === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  