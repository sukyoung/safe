  global = this;
  function testcase() 
  {
    var e = Object.isExtensible(this);
    if (e === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  