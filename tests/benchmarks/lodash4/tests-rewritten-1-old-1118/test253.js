QUnit.module('toInteger methods');
lodashStable.each([
    'toInteger',
    'toSafeInteger'
], function (methodName) {
    var func = _[methodName], isSafe = methodName == __str_top__;
    QUnit.test('`_.' + methodName + '` should convert values to integers', function (assert) {
        assert.expect(6);
        assert.strictEqual(func(-5.6), -5);
        assert.strictEqual(func('5.6'), 5);
        assert.strictEqual(func(), 0);
        assert.strictEqual(func(NaN), 0);
        var expected = isSafe ? MAX_SAFE_INTEGER : MAX_INTEGER;
        assert.strictEqual(func(Infinity), expected);
        assert.strictEqual(func(-Infinity), -expected);
    });
    QUnit.test('`_.' + methodName + '` should support `value` of `-0`', function (assert) {
        assert.expect(1);
        assert.strictEqual(1 / func(-0), -Infinity);
    });
});