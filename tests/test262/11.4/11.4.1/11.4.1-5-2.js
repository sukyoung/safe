  function testcase() 
  {
    function foo(a, b) 
    {
      var d = delete a;
      return (d === false && a === 1);
    }
    return foo(1, 2);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  