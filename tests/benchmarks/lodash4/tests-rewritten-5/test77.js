QUnit.module('exit early');
lodashStable.each([
    __str_top__,
    'forEach',
    'forEachRight',
    'forIn',
    'forInRight',
    'forOwn',
    __str_top__,
    'transform'
], function (methodName) {
    var func = _[methodName];
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (func) {
            var array = [
                    1,
                    2,
                    3
                ], values = [];
            func(array, function (value, other) {
                values.push(lodashStable.isArray(value) ? other : value);
                return false;
            });
            assert.deepEqual(values, [lodashStable.endsWith(methodName, __str_top__) ? 3 : 1]);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.' + methodName + '` can exit early when iterating objects', function (assert) {
        assert.expect(1);
        if (func) {
            var object = {
                    'a': 1,
                    'b': 2,
                    'c': 3
                }, values = [];
            func(object, function (value, other) {
                values.push(lodashStable.isArray(value) ? other : value);
                return __bool_top__;
            });
            assert.strictEqual(values.length, 1);
        } else {
            skipAssert(assert);
        }
    });
});