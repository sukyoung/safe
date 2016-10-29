  function testcase() 
  {
    function foo() 
    {
      
    }
    var f = new foo();
    f.bind = Function.prototype.bind;
    try
{      f.bind();}
    catch (e)
{      if (e instanceof TypeError)
      {
        return true;
      }}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  