  function testcase() 
  {
    var obj = {
      
    };
    var arrObj = [1, 2, 3, ];
    arrObj.value = "Array";
    Object.defineProperty(obj, "property", arrObj);
    return obj.property === "Array";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  