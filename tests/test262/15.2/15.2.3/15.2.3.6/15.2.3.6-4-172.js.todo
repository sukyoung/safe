  function testcase() 
  {
    var arrObj = [0, 1, ];
    try
{      Object.defineProperty(arrObj, "1", {
        configurable : false
      });
      Array.prototype[1] = 2;
      Object.defineProperty(arrObj, "length", {
        value : 1
      });
      return false;}
    catch (e)
{      return e instanceof TypeError && arrObj.length === 2 && arrObj.hasOwnProperty("1");}

    finally
{      delete Array.prototype[1];}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  