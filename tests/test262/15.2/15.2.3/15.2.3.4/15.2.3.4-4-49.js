  function testcase() 
  {
    var arr = [0, 1, 2, ];
    var expResult = ["0", "1", "2", "length", ];
    var result = Object.getOwnPropertyNames(arr);
    return (
        0 <= result.indexOf('0') &&
        0 <= result.indexOf('1') &&
        0 <= result.indexOf('2') &&
        0 <= result.indexOf('length')
        );
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
