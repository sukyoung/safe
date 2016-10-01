  function testcase() 
  {
    var arrObj = [];
    try
{      Object.defineProperty(arrObj, "length", {
        value : 4294967296
      });
      return false;}
    catch (e)
{      return e instanceof RangeError;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  