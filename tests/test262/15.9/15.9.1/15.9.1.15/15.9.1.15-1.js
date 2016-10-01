  function testcase() 
  {
    var result = false;
    var expectedDateTimeStr = "1970-01-01T00:00:00.000Z";
    var dateObj = new Date("1970");
    var dateStr = dateObj.toISOString();
    result = dateStr === expectedDateTimeStr;
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  