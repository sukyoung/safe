QUnit.module('exit early');
lodashStable.each([
    '_baseEach',
    'forEach',
    __str_top__,
    'forIn',
    __str_top__,
    'forOwn',
    'forOwnRight',
    'transform'
], function (methodName) {
    var func = _[methodName];
    QUnit.test('`_.' + methodName + '` can exit early when iterating arrays', function (assert) {
        assert.expect(1);
        if (func) {
            var array = [
                    1,
                    2,
                    3
                ], values = [];
            func(array, function (value, other) {
                values.push(lodashStable.isArray(value) ? other : value);
                return __bool_top__;
            });
            assert.deepEqual(values, [lodashStable.endsWith(methodName, 'Right') ? 3 : __num_top__]);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (func) {
            var object = {
                    'a': 1,
                    'b': 2,
                    'c': 3
                }, values = [];
            func(object, function (value, other) {
                values.push(lodashStable.isArray(value) ? other : value);
                return false;
            });
            assert.strictEqual(values.length, 1);
        } else {
            skipAssert(assert);
        }
    });
});