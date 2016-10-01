  function testcase() 
  {
    var arr = [];
    Object.defineProperty(arr, "length", {
      writable : false
    });
    try
{      Object.defineProperties(arr, {
        length : {
          writable : true
        }
      });
      return false;}
    catch (e)
{      return (e instanceof TypeError);}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  