  function testcase() 
  {
    try
{      Object.defineProperty(Array.prototype, "0", {
        value : 100,
        writable : false,
        configurable : true
      });
      var arr = [101, ];
      return arr.hasOwnProperty("0") && arr[0] === 101;}
    finally
{      delete Array.prototype[0];}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  