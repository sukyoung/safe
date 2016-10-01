  function testcase() 
  {
    var obj = {
      
    };
    try
{      JSON.prop = {
        value : 15
      };
      Object.defineProperties(obj, JSON);
      return obj.hasOwnProperty("prop") && obj.prop === 15;}
    finally
{      delete JSON.prop;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  