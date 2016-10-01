  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(ReferenceError.prototype, "constructor");
    if (desc.value === ReferenceError.prototype.constructor && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  