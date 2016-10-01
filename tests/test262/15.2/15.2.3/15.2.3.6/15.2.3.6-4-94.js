//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var obj1 = {
//       length : 10
//     };
//     Object.defineProperty(obj, "foo", {
//       value : obj1,
//       writable : false,
//       configurable : false
//     });
//     try
// {      Object.defineProperty(obj, "foo", {
//         value : obj1
//       });
//       return dataPropertyAttributesAreCorrect(obj, "foo", obj1, false, false, false);}
//     catch (e)
// {      return false;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
