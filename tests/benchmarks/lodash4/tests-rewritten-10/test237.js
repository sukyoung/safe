QUnit.module('sum methods');
lodashStable.each([
    'sum',
    __str_top__
], function (methodName) {
    var array = [
            __num_top__,
            __num_top__,
            2
        ], func = _[methodName];
    QUnit.test('`_.' + methodName + '` should return the sum of an array of numbers', function (assert) {
        assert.expect(1);
        assert.strictEqual(func(array), 12);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(empties, stubZero);
        var actual = lodashStable.map(empties, function (value) {
            return func(value);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.strictEqual(func([
            __num_top__,
            undefined
        ]), __num_top__);
    });
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
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
            __str_top__
        ]), '12');
    });
});