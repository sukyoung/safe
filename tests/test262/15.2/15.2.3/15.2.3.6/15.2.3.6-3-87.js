  function testcase() 
  {
    var obj = {
      
    };
    var arrObj = [1, 2, 3, ];
    arrObj.configurable = true;
    Object.defineProperty(obj, "property", arrObj);
    var beforeDeleted = obj.hasOwnProperty("property");
    delete obj.property;
    var afterDeleted = obj.hasOwnProperty("property");
    return beforeDeleted === true && afterDeleted === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  