  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(TypeError.prototype, "constructor");
    if (desc.value === TypeError.prototype.constructor && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  