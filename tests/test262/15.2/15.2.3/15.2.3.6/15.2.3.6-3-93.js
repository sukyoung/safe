  function testcase() 
  {
    var obj = {
      
    };
    var regObj = new RegExp();
    regObj.configurable = true;
    Object.defineProperty(obj, "property", regObj);
    var beforeDeleted = obj.hasOwnProperty("property");
    delete obj.property;
    var afterDeleted = obj.hasOwnProperty("property");
    return beforeDeleted === true && afterDeleted === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  