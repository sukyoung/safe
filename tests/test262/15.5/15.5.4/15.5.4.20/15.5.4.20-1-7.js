  function testcase() 
  {
    try
{      if (String.prototype.trim.call("abc") === "abc")
        return true;}
    catch (e)
{      }

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  