QUnit.module('toInteger methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isSafe = methodName == 'toSafeInteger';
    QUnit.test(__str_top__ + methodName + '` should convert values to integers', function (assert) {
        assert.expect(6);
        assert.strictEqual(func(-5.6), -__num_top__);
        assert.strictEqual(func('5.6'), 5);
        assert.strictEqual(func(), 0);
        assert.strictEqual(func(NaN), 0);
        var expected = isSafe ? MAX_SAFE_INTEGER : MAX_INTEGER;
        assert.strictEqual(func(Infinity), expected);
        assert.strictEqual(func(-Infinity), -expected);
    });
    QUnit.test('`_.' + methodName + '` should support `value` of `-0`', function (assert) {
        assert.expect(1);
        assert.strictEqual(__num_top__ / func(-0), -Infinity);
    });
});