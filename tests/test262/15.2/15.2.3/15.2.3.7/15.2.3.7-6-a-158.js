  function testcase() 
  {
    var arr = [];
    Object.defineProperty(arr, "length", {
      writable : false
    });
    try
{      Object.defineProperties(arr, {
        length : {
          value : 12
        }
      });
      return false;}
    catch (e)
{      return e instanceof TypeError && arr.length === 0;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  