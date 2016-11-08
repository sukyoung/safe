  function testcase() 
  {
    var arr = new Array(10);
    try
{      arr.map(foo);}
    catch (e)
{      if (e instanceof ReferenceError)
        return true;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  