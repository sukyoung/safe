QUnit.module('lodash(...).sort');
(function () {
    QUnit.test('should return the wrapped sorted `array`', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var array = [
                    __num_top__,
                    __num_top__,
                    __num_top__
                ], wrapped = _(array).sort(), actual = wrapped.value();
            assert.strictEqual(actual, array);
            assert.deepEqual(actual, [
                1,
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
                    var result = index ? _(value).sort().value() : _().sort().value();
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