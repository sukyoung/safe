  function testcase() 
  {
    foo.prototype = Function.prototype;
    function foo() 
    {
      
    }
    var f = new foo();
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
  