  {
    var __result1 = String.fromCharCode(- 32767).charCodeAt(0) !== 32769;
    var __expect1 = false;
  }
  {
    var __result2 = String.fromCharCode(- 32768).charCodeAt(0) !== 32768;
    var __expect2 = false;
  }
  {
    var __result3 = String.fromCharCode(- 32769).charCodeAt(0) !== 32767;
    var __expect3 = false;
  }
  {
    var __result4 = String.fromCharCode(- 65535).charCodeAt(0) !== 1;
    var __expect4 = false;
  }
  {
    var __result5 = String.fromCharCode(- 65536).charCodeAt(0) !== 0;
    var __expect5 = false;
  }
  {
    var __result6 = String.fromCharCode(- 65537).charCodeAt(0) !== 65535;
    var __expect6 = false;
  }
  {
    var __result7 = String.fromCharCode(65535).charCodeAt(0) !== 65535;
    var __expect7 = false;
  }
  {
    var __result8 = String.fromCharCode(65536).charCodeAt(0) !== 0;
    var __expect8 = false;
  }
  {
    var __result9 = String.fromCharCode(65537).charCodeAt(0) !== 1;
    var __expect9 = false;
  }
  {
    var __result10 = String.fromCharCode(131071).charCodeAt(0) !== 65535;
    var __expect10 = false;
  }
  {
    var __result11 = String.fromCharCode(131072).charCodeAt(0) !== 0;
    var __expect11 = false;
  }
  {
    var __result12 = String.fromCharCode(131073).charCodeAt(0) !== 1;
    var __expect12 = false;
  }
  