  function testcase() 
  {
    var o = {
      
    };
    var d1 = {
      value : 101
    };
    Object.defineProperty(o, "foo", d1);
    var desc = {
      value : 101,
      writable : true
    };
    try
{      Object.defineProperty(o, "foo", desc);}
    catch (e)
{      if (e instanceof TypeError)
      {
        var d2 = Object.getOwnPropertyDescriptor(o, "foo");
        if (d2.value === 101 && d2.writable === false && d2.enumerable === false && d2.configurable === false)
        {
          return true;
        }
      }}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  