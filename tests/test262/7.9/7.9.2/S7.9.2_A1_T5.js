  var a = 1, b = 2, c = 3;
  a = b;
  ++ c;
  if (a !== b)
    $ERROR('#1: Automatic semicolon insertion not work with ++');
  