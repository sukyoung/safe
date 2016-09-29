  function testcase() 
  {
    var str = new String("123");
    var desc = Object.getOwnPropertyDescriptor(str, "2");
    return desc.value === "3";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  