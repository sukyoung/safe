//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     Object.defineProperty(obj, "foo", {
//       value : 100,
//       writable : false,
//       configurable : false
//     });
//     try
// {      Object.defineProperty(obj, "foo", {
//         value : 100
//       });
//       return dataPropertyAttributesAreCorrect(obj, "foo", 100, false, false, false);}
//     catch (e)
// {      return false;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
