QUnit.module('toInteger methods');
lodashStable.each([
    'toInteger',
    __str_top__
], function (methodName) {
    var func = _[methodName], isSafe = methodName == 'toSafeInteger';
    QUnit.test('`_.' + methodName + '` should convert values to integers', function (assert) {
        assert.expect(6);
        assert.strictEqual(func(-5.6), -__num_top__);
        assert.strictEqual(func('5.6'), 5);
        assert.strictEqual(func(), 0);
        assert.strictEqual(func(NaN), __num_top__);
        var expected = isSafe ? MAX_SAFE_INTEGER : MAX_INTEGER;
        assert.strictEqual(func(Infinity), expected);
        assert.strictEqual(func(-Infinity), -expected);
    });
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.strictEqual(1 / func(-__num_top__), -Infinity);
    });
});