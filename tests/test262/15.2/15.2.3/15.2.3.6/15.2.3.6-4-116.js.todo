  function testcase() 
  {
    var arrObj = [0, 1, ];
    Object.defineProperty(arrObj, "1", {
      value : 1,
      configurable : false
    });
    try
{      Object.defineProperty(arrObj, "length", {
        value : 1
      });
      return false;}
    catch (e)
{      var desc = Object.getOwnPropertyDescriptor(arrObj, "length");
      return Object.hasOwnProperty.call(arrObj, "length") && desc.value === 2 && desc.writable === true && desc.configurable === false && desc.enumerable === false;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  