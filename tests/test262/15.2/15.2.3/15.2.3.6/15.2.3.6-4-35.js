  function testcase() 
  {
    var str = new String("abc");
    Object.defineProperty(str, "foo", {
      value : 12,
      configurable : false
    });
    try
{      Object.defineProperty(str, "foo", {
        value : 11,
        configurable : true
      });
      return false;}
    catch (e)
{      return e instanceof TypeError && str.foo === 12;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  