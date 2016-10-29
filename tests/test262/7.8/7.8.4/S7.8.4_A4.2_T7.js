  var CharacterCode = [0x430, 0x431, 0x432, 0x433, 0x434, 0x435, 0x436, 0x437, 0x438, 0x439, 0x43a, 0x43b, 0x43c, 0x43d, 0x43e, 0x43f, 0x440, 0x441, 0x442, 0x443, 0x444, 0x445, 0x446, 0x447, 0x448, 0x449, 0x44a, 0x44b, 0x44c, 0x44d, 0x44e, 0x44f, 0x451, ];
  var NonEscapeCharacter = ["\а", "\б", "\в", "\г", "\д", "\е", "\ж", "\з", "\и", "\й", "\к", "\л", "\м", "\н", "\о", "\п", "\р", "\с", "\т", "\у", "\ф", "\х", "\ц", "\ч", "\ш", "\щ", "\ъ", "\ы", "\ь", "\э", "\ю", "\я", "\ё", ];
  for(var index = 0;index <= 32;index++)
  {
    {
      var __result1 = String.fromCharCode(CharacterCode[index]) !== NonEscapeCharacter[index];
      var __expect1 = false;
    }
  }
  