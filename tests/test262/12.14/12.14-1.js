  function testcase() 
  {
    foo = "prior to throw";
    try
{      throw new Error();}
    catch (foo)
{      var foo = "initializer in catch";}

    return foo === "prior to throw";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  