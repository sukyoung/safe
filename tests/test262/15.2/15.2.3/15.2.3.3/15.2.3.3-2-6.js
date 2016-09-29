  function testcase() 
  {
    var obj = {
      "true" : 1
    };
    var desc = Object.getOwnPropertyDescriptor(obj, true);
    return desc.value === 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  