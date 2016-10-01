  function testcase() 
  {
    var obj = {
      
    };
    var arr = [1, 2, 3, ];
    arr.value = "Array";
    Object.defineProperties(obj, {
      property : arr
    });
    return obj.property === "Array";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  