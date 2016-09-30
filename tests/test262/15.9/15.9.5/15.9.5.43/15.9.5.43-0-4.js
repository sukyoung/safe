  function testcase() 
  {
    var date = new Date(1999, 9, 10, 10, 10, 10, 10);
    var localDate = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
    return localDate.toISOString() === "1999-10-10T10:10:10.010Z";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  