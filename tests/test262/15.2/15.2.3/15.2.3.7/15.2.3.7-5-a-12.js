  function testcase() 
  {
    var obj = {
      
    };
    try
{      Math.prop = {
        value : 12
      };
      Object.defineProperties(obj, Math);
      return obj.hasOwnProperty("prop") && obj.prop === 12;}
    finally
{      delete Math.prop;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  