  if (true)
    if (false)
      $ERROR('#1.1: At embedded "if/else" constructions engine must select right branches');
  var c = 0;
  if (true)
    if (true)
      c = 2;
  if (c !== 2)
    $ERROR('#2: At embedded "if/else" constructions engine must select right branches');
  if (false)
    if (true)
      $ERROR('#3.1: At embedded "if/else" constructions engine must select right branches');
  if (false)
    if (true)
      $ERROR('#4.1: At embedded "if/else" constructions engine must select right branches');
  