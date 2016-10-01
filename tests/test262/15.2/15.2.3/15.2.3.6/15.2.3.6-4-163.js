  function testcase() 
  {
    var arrObj = [];
    Object.defineProperty(arrObj, "length", {
      writable : false
    });
    try
{      Object.defineProperty(arrObj, "length", {
        value : 0
      });
      return true;}
    catch (e)
{      return false;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  