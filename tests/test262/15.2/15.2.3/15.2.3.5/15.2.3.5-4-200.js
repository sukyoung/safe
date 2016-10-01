  function testcase() 
  {
    var errorObj = new Error();
    errorObj.writable = true;
    var newObj = Object.create({
      
    }, {
      prop : errorObj
    });
    var beforeWrite = (newObj.hasOwnProperty("prop") && typeof (newObj.prop) === "undefined");
    newObj.prop = "isWritable";
    var afterWrite = (newObj.prop === "isWritable");
    return beforeWrite === true && afterWrite === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  