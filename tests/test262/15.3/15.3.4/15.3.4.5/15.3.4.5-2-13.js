  function testcase() 
  {
    try
{      Function.prototype.bind.call(5);
      return false;}
    catch (e)
{      return (e instanceof TypeError);}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  