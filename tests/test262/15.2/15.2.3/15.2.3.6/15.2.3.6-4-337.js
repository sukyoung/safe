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
    var propertyDefineCorrect = obj.hasOwnProperty("prop");
    var desc1 = Object.getOwnPropertyDescriptor(obj, "prop");
    try
{      Object.defineProperty(obj, "prop", {
        enumerable : false
      });
      return false;}
    catch (e)
{      var desc2 = Object.getOwnPropertyDescriptor(obj, "prop");
      return propertyDefineCorrect && desc1.enumerable === true && obj.prop === 2010 && desc2.enumerable === true && e instanceof TypeError;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  