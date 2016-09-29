  function testcase() 
  {
    var arg = (function () 
    {
      return arguments;
    })("ownProperty", true);
    var desc = Object.getOwnPropertyDescriptor(arg, "0");
    return desc.value === "ownProperty" && desc.writable === true && desc.enumerable === true && desc.configurable === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  