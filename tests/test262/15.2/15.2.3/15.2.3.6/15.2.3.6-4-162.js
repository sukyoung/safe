  function testcase() 
  {
    var arrObj = [];
    Object.defineProperty(arrObj, "length", {
      writable : false
    });
    try
{      Object.defineProperty(arrObj, "length", {
        value : 12
      });
      return false;}
    catch (e)
{      return e instanceof TypeError;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  