  function testcase() 
  {
    var literal = {
      a : 1
    };
    var keysBefore = Object.keys(literal);
    if (keysBefore[0] != 'a')
      return false;
    keysBefore[0] = 'x';
    var keysAfter = Object.keys(literal);
    return (keysBefore[0] == 'x') && (keysAfter[0] == 'a');
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  