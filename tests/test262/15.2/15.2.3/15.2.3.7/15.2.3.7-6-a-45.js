//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var desc = {
//       value : - 0
//     };
//     Object.defineProperty(obj, "foo", desc);
//     try
// {      Object.defineProperties(obj, {
//         foo : {
//           value : + 0
//         }
//       });
//       return false;}
//     catch (e)
// {      return (e instanceof TypeError) && dataPropertyAttributesAreCorrect(obj, "foo", - 0, false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
