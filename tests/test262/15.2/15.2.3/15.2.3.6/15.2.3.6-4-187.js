  function testcase() 
  {
    var arrObj = [1, 2, 3, ];
    Object.defineProperty(arrObj, "length", {
      writable : false
    });
    try
{      Object.defineProperty(arrObj, 1, {
        value : "abc"
      });
      return true;}
    catch (e)
{      return false;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  