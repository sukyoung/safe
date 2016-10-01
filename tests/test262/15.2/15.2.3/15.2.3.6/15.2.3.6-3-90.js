  function testcase() 
  {
    var obj = {
      
    };
    var numObj = new Number(- 2);
    numObj.configurable = true;
    Object.defineProperty(obj, "property", numObj);
    var beforeDeleted = obj.hasOwnProperty("property");
    delete obj.property;
    var afterDeleted = obj.hasOwnProperty("property");
    return beforeDeleted === true && afterDeleted === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  