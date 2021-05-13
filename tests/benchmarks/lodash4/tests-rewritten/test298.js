QUnit.module('lodash(...).splice');
(function () {
    QUnit.test('should support removing and inserting elements', function (assert) {
        assert.expect(5);
        if (!isNpm) {
            var array = [
                    __num_top__,
                    __num_top__
                ], wrapped = _(array);
            assert.deepEqual(wrapped.splice(__num_top__, __num_top__, __num_top__).value(), [__num_top__]);
            assert.deepEqual(wrapped.value(), [
                __num_top__,
                __num_top__
            ]);
            assert.deepEqual(wrapped.splice(__num_top__, __num_top__).value(), [
                __num_top__,
                __num_top__
            ]);
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
                    var result = index ? _(value).splice(__num_top__, __num_top__).value() : _().splice(__num_top__, __num_top__).value();
                    return lodashStable.isEqual(result, []);
                } catch (e) {
                }
            });
            assert.deepEqual(actual, expected);
        } else {
            skipAssert(assert);
        }
    });
}());