  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(RegExp.prototype, "constructor");
    if (desc.value === RegExp.prototype.constructor && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  