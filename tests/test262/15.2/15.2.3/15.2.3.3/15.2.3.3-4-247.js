  function testcase() 
  {
    var obj = {
      "property" : 100
    };
    var desc = Object.getOwnPropertyDescriptor(obj, "property");
    return desc instanceof Object;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  