  {
    var __result1 = "Hello, WoRlD!".toLocaleLowerCase() !== "hello, world!";
    var __expect1 = false;
  }
  {
    var __result2 = "Hello, WoRlD!".toLocaleLowerCase() !== String("hello, world!");
    var __expect2 = false;
  }
  {
    var __result3 = "Hello, WoRlD!".toLocaleLowerCase() === new String("hello, world!");
    var __expect3 = false;
  }
  