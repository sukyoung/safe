  function testcase() 
  {
    var arr = [];
    Object.preventExtensions(arr);
    try
{      Object.defineProperties(arr, {
        "0" : {
          value : 1
        }
      });
      return false;}
    catch (e)
{      return (e instanceof TypeError) && (arr.hasOwnProperty("0") === false);}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  