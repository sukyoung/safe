  function testcase() 
  {
    var fun = (function () 
    {
      
    });
    Object.defineProperty(fun, "foo", {
      value : 12,
      configurable : false
    });
    try
{      Object.defineProperty(fun, "foo", {
        value : 11,
        configurable : true
      });
      return false;}
    catch (e)
{      return e instanceof TypeError && fun.foo === 12;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  