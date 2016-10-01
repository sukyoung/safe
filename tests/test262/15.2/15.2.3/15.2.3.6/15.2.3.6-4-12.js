//   TODO getter/setter
//   function testcase() 
//   {
//     var o = {
//       
//     };
//     var d1 = {
//       value : 101,
//       configurable : false
//     };
//     Object.defineProperty(o, "foo", d1);
//     var getter = (function () 
//     {
//       return 1;
//     });
//     var desc = {
//       get : getter
//     };
//     try
// {      Object.defineProperty(o, "foo", desc);}
//     catch (e)
// {      if (e instanceof TypeError)
//       {
//         var d2 = Object.getOwnPropertyDescriptor(o, "foo");
//         if (d2.value === 101 && d2.writable === false && d2.enumerable === false && d2.configurable === false)
//         {
//           return true;
//         }
//       }}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
