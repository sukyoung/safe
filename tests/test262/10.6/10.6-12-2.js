  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(arguments, "callee");
    if (desc.configurable === true && desc.enumerable === false && desc.writable === true && desc.hasOwnProperty('get') == false && desc.hasOwnProperty('put') == false)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  