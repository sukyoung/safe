  function testcase() 
  {
    var o = {
      
    };
    Object.preventExtensions(o);
    try
{      var desc = {
        value : 1
      };
      Object.defineProperty(o, "foo", desc);}
    catch (e)
{      if (e instanceof TypeError && (o.hasOwnProperty("foo") === false))
      {
        return true;
      }}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  