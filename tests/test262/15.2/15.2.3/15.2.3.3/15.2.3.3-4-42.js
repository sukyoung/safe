  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Array.prototype, "reverse");
    if (desc.value === Array.prototype.reverse && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  