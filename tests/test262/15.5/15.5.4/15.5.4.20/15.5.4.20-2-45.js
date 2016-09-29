  function testcase() 
  {
    var str = "abc" + "   " + 123 + "   " + {
      
    } + "    " + "\u0000";
    var str1 = "    " + str + "    ";
    return str1.trim() === str;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  