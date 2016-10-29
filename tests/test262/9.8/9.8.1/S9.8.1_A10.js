  {
    var __result1 = String(1.2345) !== "1.2345";
    var __expect1 = false;
  }
  {
    var __result2 = String(1.234567890) !== "1.23456789";
    var __expect2 = false;
  }
  {
    var __result3 = String(0.12345) !== "0.12345";
    var __expect3 = false;
  }
  {
    var __result4 = String(.012345) !== "0.012345";
    var __expect4 = false;
  }
  {
    var __result5 = String(.0012345) !== "0.0012345";
    var __expect5 = false;
  }
  {
    var __result6 = String(.00012345) !== "0.00012345";
    var __expect6 = false;
  }
  {
    var __result7 = String(.000012345) !== "0.000012345";
    var __expect7 = false;
  }
  {
    var __result8 = String(.0000012345) !== "0.0000012345";
    var __expect8 = false;
  }
  {
    var __result9 = String(.00000012345) !== "1.2345e-7";
    var __expect9 = false;
  }
  