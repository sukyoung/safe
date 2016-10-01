  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "property", {
      configurable : Math
    });
    var beforeDeleted = obj.hasOwnProperty("property");
    delete obj.property;
    var afterDeleted = obj.hasOwnProperty("property");
    return beforeDeleted === true && afterDeleted === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  