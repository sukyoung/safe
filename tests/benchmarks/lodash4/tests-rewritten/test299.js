QUnit.module('lodash(...).unshift');
(function () {
    QUnit.test('should prepend elements to `array`', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var array = [__num_top__], wrapped = _(array).unshift(__num_top__, __num_top__), actual = wrapped.value();
            assert.strictEqual(actual, array);
            assert.deepEqual(actual, [
                __num_top__,
                __num_top__,
                __num_top__
            ]);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should accept falsey arguments', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var expected = lodashStable.map(falsey, stubTrue);
            var actual = lodashStable.map(falsey, function (value, index) {
                try {
                    var result = index ? _(value).unshift(__num_top__).value() : _().unshift(__num_top__).value();
                    return lodashStable.eq(result, value);
                } catch (e) {
                }
            });
            assert.deepEqual(actual, expected);
        } else {
            skipAssert(assert);
        }
    });
}());