  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "prop", {
      value : 2010,
      writable : true,
      enumerable : false,
      configurable : true
    });
    var beforeDelete = obj.hasOwnProperty("prop");
    delete obj.prop;
    var afterDelete = obj.hasOwnProperty("prop");
    return beforeDelete && ! afterDelete;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  