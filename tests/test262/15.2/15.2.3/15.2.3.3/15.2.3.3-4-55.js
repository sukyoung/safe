  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Array.prototype, "some");
    if (desc.value === Array.prototype.some && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  