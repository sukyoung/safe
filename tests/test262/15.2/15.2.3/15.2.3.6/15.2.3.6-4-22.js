  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "foo", {
      value : 11,
      configurable : false
    });
    try
{      Object.defineProperty(obj, "foo", {
        value : 12,
        configurable : true
      });
      return false;}
    catch (e)
{      return e instanceof TypeError && obj.foo === 11;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  