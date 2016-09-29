  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Array.prototype, "splice");
    if (desc.value === Array.prototype.splice && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  