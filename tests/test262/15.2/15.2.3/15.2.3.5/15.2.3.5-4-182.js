  function testcase() 
  {
    var proto = {
      writable : false
    };
    var ConstructFun = (function () 
    {
      
    });
    ConstructFun.prototype = proto;
    var descObj = new ConstructFun();
    descObj.writable = true;
    var newObj = Object.create({
      
    }, {
      prop : descObj
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
  