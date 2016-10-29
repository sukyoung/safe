function testcase() {
        for (var i in Error.prototype) {
            if (i==="name") {
                return false;
            }
        }
        return true;
}
var __result1 = testcase()
var __expect1 = true
