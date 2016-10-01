  function testcase() 
  {
    var obj = {
      "false" : 1
    };
    var desc = Object.getOwnPropertyDescriptor(obj, false);
    return desc.value === 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  