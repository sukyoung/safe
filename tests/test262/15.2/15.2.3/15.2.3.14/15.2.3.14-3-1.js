  function testcase() 
  {
    var o = {
      x : 1,
      y : 2
    };
    var a = Object.keys(o);
    if (a.length === 2 && a[0] === 'x' && a[1] === 'y')
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  