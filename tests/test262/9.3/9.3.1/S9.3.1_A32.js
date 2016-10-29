  {
    var __result1 = Number("1234567890.1234567890") !== 1234567890.1234567890;
    var __expect1 = false;
  }
  {
    var __result2 = Number("1234567890.1234567890") !== 1234567890.1234567000;
    var __expect2 = false;
  }
  {
    var __result3 = + ("1234567890.1234567890") === 1234567890.123456;
    var __expect3 = false;
  }
  {
    var __result4 = Number("0.12345678901234567890") !== 0.123456789012345678;
    var __expect4 = false;
  }
  {
    var __result5 = Number("00.12345678901234567890") !== 0.123456789012345678;
    var __expect5 = false;
  }
  