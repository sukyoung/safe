  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "prop", {
      value : 11,
      configurable : false
    });
    try
{      Object.defineProperties(obj, {
        prop : {
          value : 12,
          configurable : true
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
  