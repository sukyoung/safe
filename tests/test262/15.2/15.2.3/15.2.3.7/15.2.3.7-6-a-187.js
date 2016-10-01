  function testcase() 
  {
    try
{      Object.defineProperty(Array.prototype, "0", {
        value : 11,
        configurable : true
      });
      var arr = [];
      Object.defineProperties(arr, {
        "0" : {
          configurable : false
        }
      });
      return arr.hasOwnProperty("0") && typeof arr[0] === "undefined" && Array.prototype[0] === 11;}
    finally
{      delete Array.prototype[0];}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  