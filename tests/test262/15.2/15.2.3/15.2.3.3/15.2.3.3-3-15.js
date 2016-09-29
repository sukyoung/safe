  function testcase() 
  {
    var obj = (function (a, b) 
    {
      return a + b;
    });
    obj[1] = "ownProperty";
    var desc = Object.getOwnPropertyDescriptor(obj, "1");
    return desc.value === "ownProperty";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  