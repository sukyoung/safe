  function testcase() 
  {
    var arr = [];
    try
{      Object.defineProperties(arr, {
        length : {
          value : 123.5
        }
      });
      return false;}
    catch (e)
{      return e instanceof RangeError && arr.length === 0;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  