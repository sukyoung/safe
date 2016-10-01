  {
    var __result1 = "Hello, WoRlD!".toUpperCase() !== "HELLO, WORLD!";
    var __expect1 = false;
  }
  {
    var __result2 = "Hello, WoRlD!".toUpperCase() !== String("HELLO, WORLD!");
    var __expect2 = false;
  }
  {
    var __result3 = "Hello, WoRlD!".toUpperCase() === new String("HELLO, WORLD!");
    var __expect3 = false;
  }
  