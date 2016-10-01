  function testcase() 
  {
    var arr = [];
    Object.defineProperty(arr, 0, {
      value : "ownDataProperty",
      configurable : false
    });
    try
{      Object.defineProperties(arr, {
        "0" : {
          value : "abc",
          configurable : true
        }
      });
      return false;}
    catch (e)
{      return e instanceof TypeError && arr[0] === "ownDataProperty";}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  