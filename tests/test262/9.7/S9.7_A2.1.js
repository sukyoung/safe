  {
    var __result1 = String.fromCharCode(0).charCodeAt(0) !== 0;
    var __expect1 = false;
  }
  {
    var __result2 = String.fromCharCode(1).charCodeAt(0) !== 1;
    var __expect2 = false;
  }
  {
    var __result3 = String.fromCharCode(- 1).charCodeAt(0) !== 65535;
    var __expect3 = false;
  }
  {
    var __result4 = String.fromCharCode(65535).charCodeAt(0) !== 65535;
    var __expect4 = false;
  }
  {
    var __result5 = String.fromCharCode(65534).charCodeAt(0) !== 65534;
    var __expect5 = false;
  }
  {
    var __result6 = String.fromCharCode(65536).charCodeAt(0) !== 0;
    var __expect6 = false;
  }
  {
    var __result7 = String.fromCharCode(4294967295).charCodeAt(0) !== 65535;
    var __expect7 = false;
  }
  {
    var __result8 = String.fromCharCode(4294967294).charCodeAt(0) !== 65534;
    var __expect8 = false;
  }
  {
    var __result9 = String.fromCharCode(4294967296).charCodeAt(0) !== 0;
    var __expect9 = false;
  }
  