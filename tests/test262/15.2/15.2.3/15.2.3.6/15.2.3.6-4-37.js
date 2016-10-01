  function testcase() 
  {
    var obj = new Number(- 2);
    Object.defineProperty(obj, "foo", {
      value : 12,
      configurable : false
    });
    try
{      Object.defineProperty(obj, "foo", {
        value : 11,
        configurable : true
      });
      return false;}
    catch (e)
{      return e instanceof TypeError && obj.foo === 12;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  