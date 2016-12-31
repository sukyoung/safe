  function testcase() 
  {
    var obj = {
      
    };
    var attr = {
      configurable : @Global
    };
    Object.defineProperty(obj, "property", attr);
    var beforeDeleted = obj.hasOwnProperty("property");
    delete obj.property;
    var afterDeleted = obj.hasOwnProperty("property");
    return beforeDeleted === true && afterDeleted === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
