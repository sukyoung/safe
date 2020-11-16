QUnit.module('sum methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var array = [
            __num_top__,
            __num_top__,
            2
        ], func = _[methodName];
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.strictEqual(func(array), 12);
    });
    QUnit.test(__str_top__ + methodName + '` should return `0` when passing empty `array` values', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(empties, stubZero);
        var actual = lodashStable.map(empties, function (value) {
            return func(value);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + '` should skip `undefined` values', function (assert) {
        assert.expect(1);
        assert.strictEqual(func([
            1,
            undefined
        ]), __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.deepEqual(func([
            1,
            NaN
        ]), NaN);
    });
    QUnit.test('`_.' + methodName + '` should not coerce values to numbers', function (assert) {
        assert.expect(1);
        assert.strictEqual(func([
            '1',
            '2'
        ]), '12');
    });
});