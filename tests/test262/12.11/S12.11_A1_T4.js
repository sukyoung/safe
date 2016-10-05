  function SwitchTest(value) 
  {
    var result = 0;
    switch (value){
      case 0:
        result += 2;

      case 1:
        result += 4;
        break;

      case 2:
        result += 8;

      case isNaN(value):
        result += 16;

      default:
        result += 32;
        break;
      case null:
        result += 64;

      case isNaN:
        result += 128;
        break;

      case Infinity:
        result += 256;

      case 2 + 3:
        result += 512;
        break;

      case undefined:
        result += 1024;

    }
    return result;
  }
  {
    var __result1 = ! (SwitchTest(Number(false)) === 6);
    var __expect1 = false;
  }
  {
    var __result2 = ! (SwitchTest(parseInt) === 32);
    var __expect2 = false;
  }
  {
    var __result3 = ! (SwitchTest(isNaN) === 128);
    var __expect3 = false;
  }
  {
    var __result4 = ! (SwitchTest(true) === 32);
    var __expect4 = false;
  }
  {
    var __result5 = ! (SwitchTest(false) === 48);
    var __expect5 = false;
  }
  {
    var __result6 = ! (SwitchTest(null) === 192);
    var __expect6 = false;
  }
  {
    var __result7 = ! (SwitchTest(void 0) === 1024);
    var __expect7 = false;
  }
  {
    var __result8 = ! (SwitchTest(NaN) === 32);
    var __expect8 = false;
  }
  {
    var __result9 = ! (SwitchTest(Infinity) === 768);
    var __expect9 = false;
  }
  