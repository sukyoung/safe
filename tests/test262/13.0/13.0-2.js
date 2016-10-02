// Syntax error
//   function testcase() 
//   {
//     try
// {      eval("function x,y,z(){}");
//       return false;}
//     catch (e)
// {      return e instanceof SyntaxError;}

//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
  
