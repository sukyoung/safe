  function testcase() 
  {
    var o = new Object();
    o.x = 1;
    var d;
    with (o)
    {
      d = delete x;
    }
    if (d === true && o.x === undefined)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  