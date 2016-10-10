  function testcase() 
  {
    function foo(a, b) 
    {
      return (delete arguments.callee);
    }
    var d = delete arguments.callee;
    if (d === true && arguments.callee === undefined)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  