  function testcase() 
  {
    var descObj = {
      writable : ""
    };
    var newObj = Object.create({
      
    }, {
      prop : descObj
    });
    var hasProperty = newObj.hasOwnProperty("prop") && typeof newObj.prop === "undefined";
    newObj.prop = 121;
    return hasProperty && typeof newObj.prop === "undefined";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  