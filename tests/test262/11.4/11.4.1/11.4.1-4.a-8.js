  function testcase() 
  {
    try
{      var o = JSON;
      var d = delete JSON;
      if (d === true)
      {
        return true;
      }}
    finally
{      JSON = o;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  