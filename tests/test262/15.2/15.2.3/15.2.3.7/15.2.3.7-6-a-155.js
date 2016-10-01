  function testcase() 
  {
    var arr = [0, , 2, ];
    try
{      Object.defineProperties(arr, {
        length : {
          value : 5
        }
      });
      return arr.length === 5 && arr[0] === 0 && ! arr.hasOwnProperty("1") && arr[2] === 2;}
    catch (e)
{      return false;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  