  function testcase() 
  {
    var argObj = (function () 
    {
      return arguments;
    })();
    Object.defineProperty(argObj, "foo", {
      value : 12,
      configurable : false
    });
    try
{      Object.defineProperty(argObj, "foo", {
        value : 11,
        configurable : true
      });
      return false;}
    catch (e)
{      return e instanceof TypeError && argObj.foo === 12;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  