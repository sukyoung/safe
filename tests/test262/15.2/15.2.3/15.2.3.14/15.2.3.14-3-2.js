  function testcase() 
  {
    function foo() 
    {
      
    }
    foo.x = 1;
    var a = Object.keys(foo);
    if (a.length === 1 && a[0] === 'x')
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  