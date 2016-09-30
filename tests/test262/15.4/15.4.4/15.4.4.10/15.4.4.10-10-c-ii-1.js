  function testcase() 
  {
    var arrObj = [1, 2, 3, ];
    try
{      Object.defineProperty(Array.prototype, "0", {
        value : "test",
        writable : false,
        configurable : true
      });
      var newArr = arrObj.slice(0, 1);
      return newArr.hasOwnProperty("0") && newArr[0] === 1 && typeof newArr[1] === "undefined";}
    finally
{      delete Array.prototype[0];}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  