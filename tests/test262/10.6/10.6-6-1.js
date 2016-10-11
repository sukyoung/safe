  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(arguments, "length");
    return desc !== undefined;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  