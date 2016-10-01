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
    var propertyDefineCorrect = obj.hasOwnProperty("prop");
    var desc1 = Object.getOwnPropertyDescriptor(obj, "prop");
    Object.defineProperty(obj, "prop", {
      configurable : false
    });
    var desc2 = Object.getOwnPropertyDescriptor(obj, "prop");
    return propertyDefineCorrect && desc1.configurable === true && obj.prop === 2010 && desc2.configurable === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  