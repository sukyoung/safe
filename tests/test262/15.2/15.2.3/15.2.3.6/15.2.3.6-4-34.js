  function testcase() 
  {
    var arrObj = [];
    Object.defineProperty(arrObj, "foo", {
      value : 12,
      configurable : false
    });
    try
{      Object.defineProperty(arrObj, "foo", {
        value : 11,
        configurable : true
      });
      return false;}
    catch (e)
{      return e instanceof TypeError && arrObj.foo === 12;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  