QUnit.module('exit early');
lodashStable.each([
    '_baseEach',
    __str_top__,
    'forEachRight',
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__
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
                return false;
            });
            assert.deepEqual(values, [lodashStable.endsWith(methodName, 'Right') ? 3 : 1]);
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