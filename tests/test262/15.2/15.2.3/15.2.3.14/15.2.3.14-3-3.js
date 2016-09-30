  function testcase() 
  {
    var o = [1, 2, ];
    var a = Object.keys(o);
    if (a.length === 2 && a[0] === '0' && a[1] === '1')
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  