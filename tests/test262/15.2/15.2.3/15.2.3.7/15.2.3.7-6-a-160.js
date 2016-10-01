  function testcase() 
  {
    var arr = [0, 1, ];
    Object.defineProperty(arr, "length", {
      writable : false
    });
    try
{      Object.defineProperties(arr, {
        length : {
          value : 0
        }
      });
      return false;}
    catch (e)
{      return e instanceof TypeError && arr.length === 2 && arr[0] === 0 && arr[1] === 1;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  