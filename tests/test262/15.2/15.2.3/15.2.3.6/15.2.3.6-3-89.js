  function testcase() 
  {
    var obj = {
      
    };
    var boolObj = new Boolean(true);
    boolObj.configurable = true;
    Object.defineProperty(obj, "property", boolObj);
    var beforeDeleted = obj.hasOwnProperty("property");
    delete obj.property;
    var afterDeleted = obj.hasOwnProperty("property");
    return beforeDeleted === true && afterDeleted === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  