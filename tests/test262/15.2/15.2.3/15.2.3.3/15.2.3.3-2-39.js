  function testcase() 
  {
    var obj = {
      "Hello" : 1
    };
    var desc = Object.getOwnPropertyDescriptor(obj, new String("Hello"));
    return desc.value === 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  