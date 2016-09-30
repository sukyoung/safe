  function testcase() 
  {
    var newObj = Object.create({
      
    }, {
      prop : {
        
      }
    });
    return newObj.hasOwnProperty("prop") && typeof (newObj.prop) === "undefined";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  