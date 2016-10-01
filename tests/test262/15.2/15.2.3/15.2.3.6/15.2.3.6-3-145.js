  function testcase() 
  {
    var obj = {
      
    };
    var dateObj = new Date();
    dateObj.value = "Date";
    Object.defineProperty(obj, "property", dateObj);
    return obj.property === "Date";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  