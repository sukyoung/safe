  function testcase() 
  {
    var arr = [];
    try
{      Object.defineProperties(arr, {
        length : {
          enumerable : true
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
  