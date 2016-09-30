  function testcase() 
  {
    var arrObj = [1, 2, 3, ];
    var newArrObj = arrObj.splice(- 2, 1);
    return newArrObj.length === 1 && newArrObj[0] === 2;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  