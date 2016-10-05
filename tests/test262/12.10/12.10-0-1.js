  function testcase() 
  {
    var o = {
      
    };
    var f = (function () 
    {
      return foo;
    });
    with (o)
    {
      var foo = "12.10-0-1";
    }
    return f() === "12.10-0-1";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  