  function testcase() 
  {
    var o = {
      foo : 42
    };
    with (o)
    {
      var foo = "set in with";
    }
    return o.foo === "set in with";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  