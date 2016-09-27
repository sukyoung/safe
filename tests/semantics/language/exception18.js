var x, z;

try {
    x = 1;
    try {
        y;
        x = 2;
    } finally {
        x = 3;
    }
} catch (e) {
    z = "A";
} finally {
    z = "B";
}

var __result1 = z;
var __expect1 = "B";