//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     Object.defineProperty(obj, "foo", {
//       value : + 0,
//       writable : false,
//       configurable : false
//     });
//     try
// {      Object.defineProperty(obj, "foo", {
//         value : - 0
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && dataPropertyAttributesAreCorrect(obj, "foo", + 0, false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
