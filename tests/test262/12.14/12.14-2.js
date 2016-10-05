  function testcase() 
  {
    function capturedFoo() 
    {
      return foo;
    }
    ;
    foo = "prior to throw";
    try
{      throw new Error();}
    catch (foo)
{      var foo = "initializer in catch";
      return capturedFoo() !== "initializer in catch";}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  