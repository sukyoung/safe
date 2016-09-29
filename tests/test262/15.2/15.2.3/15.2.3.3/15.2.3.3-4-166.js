  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(RegExp.prototype, "test");
    if (desc.value === RegExp.prototype.test && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  