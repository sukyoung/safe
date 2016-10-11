  function testcase() 
  {
    try
{      arguments.callee;
      return true;}
    catch (e)
{      }

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  