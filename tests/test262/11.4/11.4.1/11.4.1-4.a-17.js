  function testcase() 
  {
    function foo(a, b) 
    {
      var d = delete arguments[0];
      return (d === true && arguments[0] === undefined);
    }
    if (foo(1, 2) === true)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  