  {
    var __result1 = Object("some string").valueOf() !== "some string";
    var __expect1 = false;
  }
  {
    var __result2 = typeof Object("some string") !== "object";
    var __expect2 = false;
  }
  {
    var __result3 = Object("some string").constructor.prototype !== String.prototype;
    var __expect3 = false;
  }
  {
    var __result4 = Object("").valueOf() !== "";
    var __expect4 = false;
  }
  {
    var __result5 = typeof Object("") !== "object";
    var __expect5 = false;
  }
  {
    var __result6 = Object("").constructor.prototype !== String.prototype;
    var __expect6 = false;
  }
  {
    var __result7 = Object("\r\t\b\n\v\f").valueOf() !== "\r\t\b\n\v\f";
    var __expect7 = false;
  }
  {
    var __result8 = typeof Object("\r\t\b\n\v\f") !== "object";
    var __expect8 = false;
  }
  {
    var __result9 = Object("\r\t\b\n\v\f").constructor.prototype !== String.prototype;
    var __expect9 = false;
  }
  {
    var __result10 = Object(String(10)).valueOf() !== "10";
    var __expect10 = false;
  }
  {
    var __result11 = typeof Object(String(10)) !== "object";
    var __expect11 = false;
  }
  {
    var __result12 = Object(String(10)).constructor.prototype !== String.prototype;
    var __expect12 = false;
  }
  