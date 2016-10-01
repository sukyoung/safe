  function testcase() 
  {
    var newObj = Object.create({
      
    }, {
      foo : {
        
      }
    });
    return newObj.hasOwnProperty("foo");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  