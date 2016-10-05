  function testcase() 
  {
    var a = 1;
    var o = {
      a : 2
    };
    try
{      with (o)
      {
        a = 3;
        throw 1;
        a = 4;
      }}
    catch (e)
{      }

    if (a === 1 && o.a === 3)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  