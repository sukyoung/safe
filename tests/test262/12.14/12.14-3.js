  function testcase() 
  {
    try
{      throw new Error();}
    catch (e)
{      var foo = "declaration in catch";}

    return foo === "declaration in catch";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  