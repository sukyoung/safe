  {
    var __result1 = "Hello, WoRlD!".toLowerCase() !== "hello, world!";
    var __expect1 = false;
  }
  {
    var __result2 = "Hello, WoRlD!".toLowerCase() !== String("hello, world!");
    var __expect2 = false;
  }
  {
    var __result3 = "Hello, WoRlD!".toLowerCase() === new String("hello, world!");
    var __expect3 = false;
  }
  