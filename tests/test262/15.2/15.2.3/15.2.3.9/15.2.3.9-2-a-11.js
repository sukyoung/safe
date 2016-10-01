  function testcase() 
  {
    var argObj = (function () 
    {
      return arguments;
    })(1, 2, 3);
    Object.freeze(argObj);
    var desc = Object.getOwnPropertyDescriptor(argObj, "0");
    delete argObj[0];
    return argObj[0] === 1 && desc.configurable === false && desc.writable === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  