QUnit.module('exit early');
lodashStable.each([
    '_baseEach',
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
                    __num_top__,
                    __num_top__,
                    3
                ], values = [];
            func(array, function (value, other) {
                values.push(lodashStable.isArray(value) ? other : value);
                return false;
            });
            assert.deepEqual(values, [lodashStable.endsWith(methodName, 'Right') ? __num_top__ : 1]);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test(__str_top__ + methodName + '` can exit early when iterating objects', function (assert) {
        assert.expect(1);
        if (func) {
            var object = {
                    'a': __num_top__,
                    'b': 2,
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