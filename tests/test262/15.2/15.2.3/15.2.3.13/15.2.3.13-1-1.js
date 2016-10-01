  function testcase() 
  {
    try
{      Object.isExtensible(undefined);}
    catch (e)
{      return (e instanceof TypeError);}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  