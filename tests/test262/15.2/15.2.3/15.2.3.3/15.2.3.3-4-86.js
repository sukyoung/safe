  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Boolean.prototype, "valueOf");
    if (desc.value === Boolean.prototype.valueOf && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  