  function testcase() 
  {
    var newObj = Object.create({
      
    }, {
      prop : {
        value : 100
      }
    });
    var beforeWrite = (newObj.prop === 100);
    newObj.prop = "isWritable";
    var afterWrite = (newObj.prop === 100);
    return beforeWrite === true && afterWrite === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  