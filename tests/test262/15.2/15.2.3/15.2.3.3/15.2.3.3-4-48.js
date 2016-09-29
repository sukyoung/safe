  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Array.prototype, "shift");
    if (desc.value === Array.prototype.shift && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  