  function testcase() 
  {
    var str = new String("abc");
    str.value = "StrValue";
    var newObj = Object.create({
      
    }, {
      prop : str
    });
    return newObj.prop === "StrValue";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  