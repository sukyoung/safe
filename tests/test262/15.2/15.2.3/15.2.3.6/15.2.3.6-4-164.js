  function testcase() 
  {
    var arrObj = [0, 1, ];
    Object.defineProperty(arrObj, "length", {
      writable : false
    });
    try
{      Object.defineProperty(arrObj, "length", {
        value : 0
      });
      return false;}
    catch (e)
{      return e instanceof TypeError;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  