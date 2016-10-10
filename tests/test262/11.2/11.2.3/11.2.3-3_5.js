  function testcase() 
  {
    var fooCalled = false;
    function foo() 
    {
      fooCalled = true;
    }
    var o = {
      
    };
    try
{      o.bar(foo());
      throw new Exception("o.bar does not exist!");}
    catch (e)
{      return (e instanceof TypeError) && (fooCalled === true);}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  