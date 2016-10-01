//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arr = [];
//     var obj1 = {
//       value : 12
//     };
//     var obj2 = {
//       value : 36
//     };
//     Object.defineProperty(arr, "1", {
//       value : obj1
//     });
//     try
// {      Object.defineProperties(arr, {
//         "1" : {
//           value : obj2
//         }
//       });
//       return false;}
//     catch (ex)
// {      return (ex instanceof TypeError) && dataPropertyAttributesAreCorrect(arr, "1", obj1, false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
