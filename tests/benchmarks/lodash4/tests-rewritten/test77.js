QUnit.module('exit early');
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName];
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (func) {
            var array = [
                    __num_top__,
                    __num_top__,
                    __num_top__
                ], values = [];
            func(array, function (value, other) {
                values.push(lodashStable.isArray(value) ? other : value);
                return __bool_top__;
            });
            assert.deepEqual(values, [lodashStable.endsWith(methodName, __str_top__) ? __num_top__ : __num_top__]);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (func) {
            var object = {
                    'a': __num_top__,
                    'b': __num_top__,
                    'c': __num_top__
                }, values = [];
            func(object, function (value, other) {
                values.push(lodashStable.isArray(value) ? other : value);
                return __bool_top__;
            });
            assert.strictEqual(values.length, __num_top__);
        } else {
            skipAssert(assert);
        }
    });
});