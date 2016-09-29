  {
    var __result1 = "Hello, WoRlD!".toLocaleUpperCase() !== "HELLO, WORLD!";
    var __expect1 = false;
  }
  {
    var __result2 = "Hello, WoRlD!".toLocaleUpperCase() !== String("HELLO, WORLD!");
    var __expect2 = false;
  }
  {
    var __result3 = "Hello, WoRlD!".toLocaleUpperCase() === new String("HELLO, WORLD!");
    var __expect3 = false;
  }
  