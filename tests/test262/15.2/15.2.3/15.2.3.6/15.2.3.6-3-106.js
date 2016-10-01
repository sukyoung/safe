  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "property", {
      configurable : NaN
    });
    var beforeDeleted = obj.hasOwnProperty("property");
    delete obj.property;
    var afterDeleted = obj.hasOwnProperty("property") && typeof (obj.property) === "undefined";
    return beforeDeleted === true && afterDeleted === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  