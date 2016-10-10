  function testcase() 
  {
    var o = {
      foo : 1
    };
    var catchAccessed = false;
    try
{      throw o;}
    catch (expObj)
{      catchAccessed = (expObj.foo == 1);}

    try
{      expObj;}
    catch (e)
{      return catchAccessed && e instanceof ReferenceError;}

    return false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  