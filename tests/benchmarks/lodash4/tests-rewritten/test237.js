QUnit.module('sum methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var array = [
            __num_top__,
            __num_top__,
            __num_top__
        ], func = _[methodName];
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.strictEqual(func(array), __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(empties, stubZero);
        var actual = lodashStable.map(empties, function (value) {
            return func(value);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.strictEqual(func([
            __num_top__,
            undefined
        ]), __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.deepEqual(func([
            __num_top__,
            NaN
        ]), NaN);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.strictEqual(func([
            __str_top__,
            __str_top__
        ]), __str_top__);
    });
});