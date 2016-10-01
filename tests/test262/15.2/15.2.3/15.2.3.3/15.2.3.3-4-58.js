  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Array.prototype, "filter");
    if (desc.value === Array.prototype.filter && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  