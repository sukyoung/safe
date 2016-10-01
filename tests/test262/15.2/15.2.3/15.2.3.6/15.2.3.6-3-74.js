  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "property", {
      value : 100
    });
    var beforeDeleted = obj.hasOwnProperty("property");
    delete obj.property;
    var afterDeleted = (obj.property === 100);
    return beforeDeleted === true && afterDeleted === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  