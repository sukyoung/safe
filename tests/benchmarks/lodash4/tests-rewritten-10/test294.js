QUnit.module('lodash(...).pop');
(function () {
    QUnit.test('should remove elements from the end of `array`', function (assert) {
        assert.expect(5);
        if (!isNpm) {
            var array = [
                    __num_top__,
                    __num_top__
                ], wrapped = _(array);
            assert.strictEqual(wrapped.pop(), __num_top__);
            assert.deepEqual(wrapped.value(), [__num_top__]);
            assert.strictEqual(wrapped.pop(), __num_top__);
            var actual = wrapped.value();
            assert.strictEqual(actual, array);
            assert.deepEqual(actual, []);
        } else {
            skipAssert(assert, 5);
        }
    });
    QUnit.test('should accept falsey arguments', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var expected = lodashStable.map(falsey, stubTrue);
            var actual = lodashStable.map(falsey, function (value, index) {
                try {
                    var result = index ? _(value).pop() : _().pop();
                    return result === undefined;
                } catch (e) {
                }
            });
            assert.deepEqual(actual, expected);
        } else {
            skipAssert(assert);
        }
    });
}());