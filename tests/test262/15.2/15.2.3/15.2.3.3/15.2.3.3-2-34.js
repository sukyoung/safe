  function testcase() 
  {
    var obj = {
      "undefined" : 1
    };
    var desc = Object.getOwnPropertyDescriptor(obj, "undefined");
    return desc.value === 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  