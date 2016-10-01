  function testcase() 
  {
    var desc = new RegExp();
    Object.defineProperty(desc, "foo", {
      value : 12,
      configurable : false
    });
    try
{      Object.defineProperty(desc, "foo", {
        value : 11,
        configurable : true
      });
      return false;}
    catch (e)
{      return e instanceof TypeError && desc.foo === 12;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  