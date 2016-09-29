  function testcase() 
  {
    var obj = {
      "null" : 1
    };
    var desc = Object.getOwnPropertyDescriptor(obj, null);
    return desc.value === 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  