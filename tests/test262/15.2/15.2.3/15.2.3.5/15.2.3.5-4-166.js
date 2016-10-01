  function testcase() 
  {
    var arr = [1, 2, 3, ];
    arr.value = "ArrValue";
    var newObj = Object.create({
      
    }, {
      prop : arr
    });
    return newObj.prop === "ArrValue";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  