  function testcase() 
  {
    var arrObj = [1, 2, 3, ];
    Object.defineProperty(arrObj, "length", {
      writable : false
    });
    try
{      Object.defineProperty(arrObj, 4, {
        value : "abc"
      });
      return false;}
    catch (e)
{      return e instanceof TypeError;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  