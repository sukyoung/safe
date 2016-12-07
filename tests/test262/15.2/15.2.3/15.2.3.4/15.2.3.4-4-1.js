  function testcase() 
  {
    var result = Object.getOwnPropertyNames(@Global);
    var expResult = ["NaN", "Infinity", "undefined", "eval", "parseInt", "parseFloat", "isNaN", "isFinite", "decodeURI", "decodeURIComponent", "encodeURI", "encodeURIComponent", "Object", "Function", "Array", "String", "Boolean", "Number", "Date", "Date", "RegExp", "Error", "EvalError", "RangeError", "ReferenceError", "SyntaxError", "TypeError", "URIError", "Math", "JSON", ];
    var result1 = {
      
    };
    for(var p in result)
    {
      result1[result[p]] = true;
    }
    for(var p1 in expResult)
    {
      if (! result1[expResult[p1]])
      {
        return false;
      }
    }
    return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
