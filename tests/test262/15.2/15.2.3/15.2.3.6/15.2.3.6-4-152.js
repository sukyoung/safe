  function testcase() 
  {
    var arrObj = [];
    try
{      Object.defineProperty(arrObj, "length", {
        value : 123.5
      });
      return false;}
    catch (e)
{      return e instanceof RangeError;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  