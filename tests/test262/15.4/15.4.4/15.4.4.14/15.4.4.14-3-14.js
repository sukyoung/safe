  function testcase() 
  {
    var objOne = {
      0 : true,
      1 : true,
      length : "Infinity"
    };
    var objTwo = {
      0 : true,
      1 : true,
      length : "+Infinity"
    };
    var objThree = {
      0 : true,
      1 : true,
      length : "-Infinity"
    };
    return Array.prototype.indexOf.call(objOne, true) === - 1 && Array.prototype.indexOf.call(objTwo, true) === - 1 && Array.prototype.indexOf.call(objThree, true) === - 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  