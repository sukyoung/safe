// Syntax error
//   function testcase() 
//   {
//     var obj = {
      
//     };
//     obj.tt = {
//       len : 10
//     };
//     try
// {      eval("function obj.tt.ss() {};");
//       return false;}
//     catch (e)
// {      return e instanceof SyntaxError;}

//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
  
