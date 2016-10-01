  function testcase() 
  {
    var arr = [];
    try
{      Object.defineProperties(arr, {
        length : {
          value : - 4294967294.5
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
  