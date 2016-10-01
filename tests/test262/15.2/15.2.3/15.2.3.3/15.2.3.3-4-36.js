  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Function.prototype, "apply");
    if (desc.value === Function.prototype.apply && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  