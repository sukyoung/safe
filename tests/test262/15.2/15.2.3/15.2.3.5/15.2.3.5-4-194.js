  function testcase() 
  {
    var booleanObj = new Boolean(false);
    booleanObj.writable = true;
    var newObj = Object.create({
      
    }, {
      prop : booleanObj
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
  