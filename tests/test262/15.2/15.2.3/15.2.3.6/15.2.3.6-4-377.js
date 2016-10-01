  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "prop", {
      value : 2010,
      writable : false,
      enumerable : false,
      configurable : false
    });
    var beforeDelete = obj.hasOwnProperty("prop");
    delete obj.prop;
    var afterDelete = obj.hasOwnProperty("prop");
    return beforeDelete && obj.prop === 2010 && afterDelete;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  