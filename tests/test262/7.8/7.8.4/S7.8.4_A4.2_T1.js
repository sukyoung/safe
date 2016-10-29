  var CharacterCode = [0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4a, 0x4b, 0x4c, 0x4d, 0x4e, 0x4f, 0x50, 0x51, 0x52, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5a, ];
  var NonEscapeCharacter = ["\A", "\B", "\C", "\D", "\E", "\F", "\G", "\H", "\I", "\J", "\K", "\L", "\M", "\N", "\O", "\P", "\Q", "\R", "\S", "\T", "\U", "\V", "\W", "\X", "\Y", "\Z", ];
  for(var index = 0;index <= 25;index++)
  {
    {
      var __result1 = String.fromCharCode(CharacterCode[index]) !== NonEscapeCharacter[index];
      var __expect1 = false;
    }
  }
  