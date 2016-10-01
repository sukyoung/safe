  function testcase() 
  {
    var arr = [];
    Object.defineProperty(arr, "length", {
      writable : false
    });
    try
{      Object.defineProperties(arr, {
        length : {
          value : 0
        }
      });
      return true && arr.length === 0;}
    catch (e)
{      return false;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  