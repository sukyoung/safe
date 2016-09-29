  function testcase() 
  {
    var obj = {
      "Infinity" : 1
    };
    var desc = Object.getOwnPropertyDescriptor(obj, + Infinity);
    return desc.value === 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  