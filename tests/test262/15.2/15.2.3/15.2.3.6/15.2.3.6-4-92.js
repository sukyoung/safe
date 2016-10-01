  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "foo", {
      value : false,
      writable : false,
      configurable : false
    });
    try
{      Object.defineProperty(obj, "foo", {
        value : false
      });
      var desc = Object.getOwnPropertyDescriptor(obj, 'foo');
      return (
          desc.value === false &&
          desc.writable === false &&
          desc.enumerable === false &&
          desc.configurable === false
          );
}
    catch (e)
{      return false;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
