  var str = new String();
  if (! (str.valueOf() == str.toString()))
    $ERROR('#1: str = new String(),str.valueOf() == str.toString()');
  str = new String(true);
  if (! (str.valueOf() == str.toString()))
    $ERROR('#2: str = new String(true),str.valueOf() == str.toString()');
  str = new String(false);
  if (! (str.valueOf() == str.toString()))
    $ERROR('#3: str = new String(false),str.valueOf() == str.toString()');
  str = new String(Math.PI);
  if (! (str.valueOf() == str.toString()))
    $ERROR('#4: str = new String(Math.PI),str.valueOf() == str.toString()');
  