  function testcase() 
  {
    var strObj = new String();
    var preCheck = Object.isExtensible(strObj);
    Object.preventExtensions(strObj);
    try
{      Object.defineProperty(strObj, "0", {
        value : "c"
      });
      return false;}
    catch (e)
{      return e instanceof TypeError && preCheck && ! strObj.hasOwnProperty("0") && typeof strObj[0] === "undefined";}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  