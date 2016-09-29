  function testcase() 
  {
    try
{      if (String.prototype.trim.call(true) == "true")
        return true;}
    catch (e)
{      }

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  