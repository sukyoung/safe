_<>_print(parseInt("123"));
_<>_print(parseInt("1123", 2));
_<>_print(parseInt("12a1", 16));
_<>_print(parseInt("0xff"));
_<>_print(parseInt(" \r\n543"));
_<>_print(parseInt(" \r\n543 abc"));

_<>_print(parseInt(10.33));          // 10
_<>_print(parseInt("10.33"));        // 10
_<>_print(parseInt("40 years", 10)); // 40
_<>_print(parseInt("He was 40"));    // NaN
