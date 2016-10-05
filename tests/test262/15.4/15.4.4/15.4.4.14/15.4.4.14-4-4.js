  function testcase() 
  {
    var i = Array.prototype.lastIndexOf.call({
      length : 0
    }, 1);
    if (i === - 1)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  