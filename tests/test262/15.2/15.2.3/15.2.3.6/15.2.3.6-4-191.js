  function testcase() 
  {
    try
{      Object.defineProperty(Array.prototype, "0", {
        value : 11,
        configurable : true
      });
      var arrObj = [];
      Object.defineProperty(arrObj, "0", {
        configurable : false
      });
      return arrObj.hasOwnProperty("0") && Array.prototype[0] === 11 && typeof arrObj[0] === "undefined";}
    finally
{      delete Array.prototype[0];}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  