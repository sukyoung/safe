  var TEST_STRING = new String(" !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~");
  for(var k = 0, i = 0x20;i < 0x7d;i++, k++)
  {
    {
      var __result1 = TEST_STRING.indexOf((String.fromCharCode(i) + String.fromCharCode(i + 1) + String.fromCharCode(i + 2)), 
      0) !== k;
      var __expect1 = false;
    }
  }
  