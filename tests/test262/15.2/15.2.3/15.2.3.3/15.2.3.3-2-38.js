  function testcase() 
  {
    var obj = {
      "1" : 1
    };
    var desc = Object.getOwnPropertyDescriptor(obj, [1, ]);
    return desc.value === 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  