  function testcase() 
  {
    var obj = {
      "undefined" : 1
    };
    var desc1 = Object.getOwnPropertyDescriptor(obj, undefined);
    var desc2 = Object.getOwnPropertyDescriptor(obj, "undefined");
    return desc1.value === 1 && desc2.value === 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  