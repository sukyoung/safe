  function testcase() 
  {
    var obj = {
      property : "ownDataProperty"
    };
    var desc = Object.getOwnPropertyDescriptor(obj, "propertyNonExist");
    return typeof desc === "undefined";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  