  function testcase() 
  {
    var str = new String("abc");
    str[5] = "de";
    var expResult = ["0", "1", "2", "length", "5", ];
    var result = Object.getOwnPropertyNames(str);
    return (
        0 <= result.indexOf('0') &&
        0 <= result.indexOf('1') &&
        0 <= result.indexOf('2') &&
        0 <= result.indexOf('length') &&
        0 <= result.indexOf('5')
        );
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
