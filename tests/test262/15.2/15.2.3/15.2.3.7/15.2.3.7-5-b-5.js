  function testcase() 
  {
    var obj = {
      
    };
    try
{      Object.defineProperties(obj, {
        prop : "abc"
      });
      return false;}
    catch (e)
{      return e instanceof TypeError && ! obj.hasOwnProperty("prop");}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  