QUnit.module('lodash(...).shift');
(function () {
    QUnit.test('should remove elements from the front of `array`', function (assert) {
        assert.expect(5);
        if (!isNpm) {
            var array = [
                    __num_top__,
                    2
                ], wrapped = _(array);
            assert.strictEqual(wrapped.shift(), __num_top__);
            assert.deepEqual(wrapped.value(), [__num_top__]);
            assert.strictEqual(wrapped.shift(), __num_top__);
            var actual = wrapped.value();
            assert.strictEqual(actual, array);
            assert.deepEqual(actual, []);
        } else {
            skipAssert(assert, __num_top__);
        }
    });
    QUnit.test('should accept falsey arguments', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var expected = lodashStable.map(falsey, stubTrue);
            var actual = lodashStable.map(falsey, function (value, index) {
                try {
                    var result = index ? _(value).shift() : _().shift();
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