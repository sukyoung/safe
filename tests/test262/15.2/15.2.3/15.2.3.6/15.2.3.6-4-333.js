  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "prop", {
      value : 2010,
      writable : true,
      enumerable : true,
      configurable : false
    });
    var propertyDefineCorrect = (obj.prop === 2010);
    obj.prop = 1001;
    return propertyDefineCorrect && obj.prop === 1001;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  