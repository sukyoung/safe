  function testcase() 
  {
    var obj = {
      prop1 : 100
    };
    var array = Object.keys(obj);
    try
{      array[0] = "isWritable";
      var desc = Object.getOwnPropertyDescriptor(array, "0");
      return array[0] === "isWritable" && desc.hasOwnProperty("writable") && desc.writable === true;}
    catch (e)
{      return false;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  