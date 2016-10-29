  var CharacterCode = [0x61, 0x63, 0x64, 0x65, 0x67, 0x68, 0x69, 0x6a, 0x6b, 0x6c, 0x6d, 0x6f, 0x70, 0x71, 0x73, 0x77, 0x79, 0x7a, ];
  var NonEscapeCharacter = ["\a", "\c", "\d", "\e", "\g", "\h", "\i", "\j", "\k", "\l", "\m", "\o", "\p", "\q", "\s", "\w", "\y", "\z", ];
  for(var index = 0;index <= 17;index++)
  {
    {
      var __result1 = String.fromCharCode(CharacterCode[index]) !== NonEscapeCharacter[index];
      var __expect1 = false;
    }
  }
  