  function testcase() 
  {
    var arrObj = [];
    Object.preventExtensions(arrObj);
    try
{      var desc = {
        value : 1
      };
      Object.defineProperty(arrObj, "0", desc);
      return false;}
    catch (e)
{      return e instanceof TypeError && (arrObj.hasOwnProperty("0") === false);}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  