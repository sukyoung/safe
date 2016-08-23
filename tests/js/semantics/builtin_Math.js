// Math.abs
var __result1 = Math.abs(NaN);
var __expect1 = NaN;
var __result2 = Math.abs(-3);
var __expect2 = 3;
var __result3 = Math.abs(3);
var __expect3 = 3;
var __result4 = Math.abs(2.1);
var __expect4 = 2.1;
var __result5 = Math.abs(-2.1);
var __expect5 = 2.1;
var __result6 = Math.abs(0);
var __expect6 = 0;
var __result7 = Math.abs(__NumTop);
var __expect7 = __NumTop;
var __result8 = Math.abs(__NumTop);
var __expect8 = __NumTop;

// Math.acos
var __result9 = Math.acos(NaN);
var __expect9 = NaN;
var __result10 = Math.acos(__NumTop);
var __expect10 = __NumTop;
var __result11 = Math.acos(1);
var __expect11 = 0;
var __result12 = Math.acos(-2);
var __expect12 = NaN;
var __result13 = Math.acos(-1);
var __expect13 = 3.141592653589793;
var __result14 = Math.acos(3);
var __expect14 = NaN;
var __result15 = Math.acos(0);
var __expect15 = 1.5707963267948966;

// Math.asin
var __result16 = Math.asin(NaN);
var __expect16 = NaN;
var __result17 = Math.asin(__NumTop);
var __expect17 = __NumTop;
var __result18 = Math.asin(-2);
var __expect18 = NaN;
var __result19 = Math.asin(3);
var __expect19 = NaN;
var __result20 = Math.asin(0);
var __expect20 = 0;
var __result21 = Math.asin(1);
var __expect21 = 1.5707963267948966;
var __result22 = Math.asin(-1);
var __expect22 = -1.5707963267948966;

// Math.atan
var __result23 = Math.atan(NaN);
var __expect23 = NaN;
var __result24 = Math.atan(__NumTop);
var __expect24 = __NumTop;
var __result25 = Math.atan2(__NumTop, 1);
var __expect25 = __NumTop;
var __result26 = Math.atan2(34, __NumTop);
var __expect26 = __NumTop;
var __result27 = Math.atan2(NaN, 3.2);
var __expect27 = NaN;
var __result28 = Math.atan2(2, NaN);
var __expect28 = NaN;
var __result29 = Math.atan2(0, 0);
var __expect29 = 0;
var __result30 = Math.atan2(0, 1);
var __expect30 = 0;
var __result31 = Math.atan2(0, -1);
var __expect31 = 3.141592653589793;
var __result32 = Math.atan(0);
var __expect32 = 0;
var __result33 = Math.atan(1);
var __expect33 = 0.7853981633974483;
var __result34 = Math.atan(-1);
var __expect34 = -0.7853981633974483;

// Math.ceil
var __result35 = Math.ceil(NaN);
var __expect35 = NaN;
var __result36 = Math.ceil(__NumTop);
var __expect36 = __NumTop;
var __result37 = Math.ceil(3);
var __expect37 = 3;
var __result38 = Math.ceil(3.1);
var __expect38 = 4;
var __result39 = Math.ceil(-3.2);
var __expect39 = -3;
var __result40 = Math.ceil(0);
var __expect40 = 0;

// Math.cos
var __result41 = Math.cos(NaN);
var __expect41 = NaN;
var __result42 = Math.cos(__NumTop);
var __expect42 = __NumTop;
var __result43 = Math.cos(3);
var __expect43 = -0.9899924966004454;
var __result44 = Math.cos(0);
var __expect44 = 1;
var __result45 = Math.cos(3.141592653589793);
var __expect45 = -1;
var __result46 = Math.cos(-3.141592653589793);
var __expect46 = -1;

// Math.exp
var __result47 = Math.exp(NaN);
var __expect47 = NaN;
var __result48 = Math.exp(__NumTop);
var __expect48 = __NumTop;
var __result49 = Math.exp(0);
var __expect49 = 1;
var __result50 = Math.exp(-1);
var __expect50 = 0.36787944117144233;
var __result51 = Math.exp(3.5);
var __expect51 = 33.11545195869231;

// Math.floor
var __result52 = Math.floor(__NumTop);
var __expect52 = __NumTop;
var __result53 = Math.floor(3);
var __expect53 = 3;
var __result54 = Math.floor(-1.3);
var __expect54 = -2;
var __result55 = Math.floor(3.2);
var __expect55 = 3;
var __result56 = Math.floor(3.7);
var __expect56 = 3;
var __result57 = Math.floor(0);
var __expect57 = 0;

// Math.log
var __result58 = Math.log(NaN);
var __expect58 = NaN;
var __result59 = Math.log(__NumTop);
var __expect59 = __NumTop;
var __result60 = Math.log(-2);
var __expect60 = NaN;
var __result61 = Math.log(1);
var __expect61 = 0;
var __result62 = Math.log(3);
var __expect62 = 1.0986122886681098;

// Math.pow
var __result63 = Math.pow(1, NaN);
var __expect63 = NaN;
var __result64 = Math.pow(NaN, 0);
var __expect64 = 1;
var __result65 = Math.pow(NaN, 3);
var __expect65 = NaN;
var __result66 = Math.pow(0, 4);
var __expect66 = 0;
var __result67 = Math.pow(-2, 4.3);
var __expect67 = NaN;
var __result68 = Math.pow(3, 4);
var __expect68 = 81;
var __result69 = Math.pow(3, 2.1);
var __expect69 = 10.04510856630514;
var __result70 = Math.pow(__NumTop, 3);
var __expect70 = __NumTop;

// Math.round
var __result71 = Math.round(__NumTop);
var __expect71 = __NumTop;
var __result72 = Math.round(3);
var __expect72 = 3;
var __result73 = Math.round(0.1);
var __expect73 = 0;
var __result74 = Math.round(-0.1);
var __expect74 = 0;
var __result75 = Math.round(2.7);
var __expect75 = 3;
var __result76 = Math.round(7.2);
var __expect76 = 7;

// Math.sin
var __result77 = Math.sin(NaN);
var __expect77 = NaN;
var __result78 = Math.sin(0);
var __expect78 = 0;
var __result79 = Math.sin(-2);
var __expect79 = -0.9092974268256817;
var __result80 = Math.sin(__NumTop);
var __expect80 = __NumTop;

// Math.sqrt
var __result81 = Math.sqrt(NaN);
var __expect81 = NaN;
var __result82 = Math.sqrt(-1);
var __expect82 = NaN;
var __result83 = Math.sqrt(1);
var __expect83 = 1;
var __result84 = Math.sqrt(0);
var __expect84 = 0;
var __result85 = Math.sqrt(9);
var __expect85 = 3;
var __result86 = Math.sqrt(__NumTop);
var __expect86 = __NumTop;

// Math.tan
var __result87 = Math.tan(NaN);
var __expect87 = NaN;
var __result88 = Math.tan(__NumTop);
var __expect88 = __NumTop;
var __result89 = Math.tan(0);
var __expect89 = 0;
var __result90 = Math.tan(1);
var __expect90 = 1.5574077246549023;
var __result91 = Math.tan(-1);
var __expect91 = -1.5574077246549023;
