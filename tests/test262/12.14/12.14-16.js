var __globalObject = @Global;
function fnGlobalObject() {
	return __globalObject;
}

  function testcase() 
  {
    try
{      throw (function () 
      {
        this._12_14_16_foo = "test";
      });
      return false;}
    catch (e)
{      var obj = {
        
      };
      obj.test = (function () 
      {
        this._12_14_16_foo = "test1";
      });
      e = obj.test;
      e();
      return fnGlobalObject()._12_14_16_foo === "test1";}

    finally
{      delete fnGlobalObject()._12_14_16_foo;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
