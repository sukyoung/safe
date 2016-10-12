  var CharacterCode = [0x410, 0x411, 0x412, 0x413, 0x414, 0x415, 0x416, 0x417, 0x418, 0x419, 0x41a, 0x41b, 0x41c, 0x41d, 0x41e, 0x41f, 0x420, 0x421, 0x422, 0x423, 0x424, 0x425, 0x426, 0x427, 0x428, 0x429, 0x42a, 0x42b, 0x42c, 0x42d, 0x42e, 0x42f, 0x401, ];
  var NonEscapeCharacter = ["\А", "\Б", "\В", "\Г", "\Д", "\Е", "\Ж", "\З", "\И", "\Й", "\К", "\Л", "\М", "\Н", "\О", "\П", "\Р", "\С", "\Т", "\У", "\Ф", "\Х", "\Ц", "\Ч", "\Ш", "\Щ", "\Ъ", "\Ы", "\Ь", "\Э", "\Ю", "\Я", "\Ё", ];
  for(var index = 0;index <= 32;index++)
  {
    {
      var __result1 = String.fromCharCode(CharacterCode[index]) !== NonEscapeCharacter[index];
      var __expect1 = false;
    }
  }
  