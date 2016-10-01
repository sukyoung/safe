  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(RegExp.prototype, "toString");
    if (desc.value === RegExp.prototype.toString && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  