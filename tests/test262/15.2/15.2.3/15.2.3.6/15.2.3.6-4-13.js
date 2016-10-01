//   TODO getter/setter
//   function testcase() 
//   {
//     var o = {
//       
//     };
//     var getter = (function () 
//     {
//       return 1;
//     });
//     var d1 = {
//       get : getter,
//       configurable : false
//     };
//     Object.defineProperty(o, "foo", d1);
//     var desc = {
//       value : 101
//     };
//     try
// {      Object.defineProperty(o, "foo", desc);}
//     catch (e)
// {      if (e instanceof TypeError)
//       {
//         var d2 = Object.getOwnPropertyDescriptor(o, "foo");
//         if (d2.get === getter && d2.configurable === false)
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
