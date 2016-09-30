  function testcase() 
  {
    var newObj = Object.create({
      
    }, undefined);
    return (newObj instanceof Object);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  