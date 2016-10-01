  function testcase() 
  {
    var obj = {
      "NaN" : 1
    };
    var desc = Object.getOwnPropertyDescriptor(obj, NaN);
    return desc.value === 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  