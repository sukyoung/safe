QUnit.module('lodash.inRange');
(function () {
    QUnit.test('should work with an `end`', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.inRange(3, 5), true);
        assert.strictEqual(_.inRange(5, 5), false);
        assert.strictEqual(_.inRange(6, 5), false);
    });
    QUnit.test('should work with a `start` and `end`', function (assert) {
        assert.expect(4);
        assert.strictEqual(_.inRange(1, 1, 5), true);
        assert.strictEqual(_.inRange(3, 1, 5), __bool_top__);
        assert.strictEqual(_.inRange(0, 1, 5), false);
        assert.strictEqual(_.inRange(5, 1, 5), false);
    });
    QUnit.test('should treat falsey `start` as `0`', function (assert) {
        assert.expect(13);
        lodashStable.each(falsey, function (value, index) {
            if (index) {
                assert.strictEqual(_.inRange(0, value), false);
                assert.strictEqual(_.inRange(0, value, 1), true);
            } else {
                assert.strictEqual(_.inRange(0), false);
            }
        });
    });
    QUnit.test('should swap `start` and `end` when `start` > `end`', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.inRange(2, 5, 1), true);
        assert.strictEqual(_.inRange(-3, -__num_top__, -6), true);
    });
    QUnit.test('should work with a floating point `n` value', function (assert) {
        assert.expect(4);
        assert.strictEqual(_.inRange(0.5, 5), true);
        assert.strictEqual(_.inRange(1.2, 1, 5), true);
        assert.strictEqual(_.inRange(5.2, 5), false);
        assert.strictEqual(_.inRange(0.5, 1, 5), false);
    });
    QUnit.test('should coerce arguments to finite numbers', function (assert) {
        assert.expect(1);
        var actual = [
            _.inRange(0, '1'),
            _.inRange(__num_top__, __str_top__, 1),
            _.inRange(0, __num_top__, '1'),
            _.inRange(0, NaN, 1),
            _.inRange(-1, -1, NaN)
        ];
        assert.deepEqual(actual, lodashStable.map(actual, stubTrue));
    });
}());