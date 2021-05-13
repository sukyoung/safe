QUnit.module('lodash.inRange');
(function () {
    QUnit.test('should work with an `end`', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.inRange(__num_top__, __num_top__), __bool_top__);
        assert.strictEqual(_.inRange(__num_top__, __num_top__), __bool_top__);
        assert.strictEqual(_.inRange(__num_top__, __num_top__), __bool_top__);
    });
    QUnit.test('should work with a `start` and `end`', function (assert) {
        assert.expect(4);
        assert.strictEqual(_.inRange(__num_top__, __num_top__, __num_top__), __bool_top__);
        assert.strictEqual(_.inRange(__num_top__, __num_top__, __num_top__), __bool_top__);
        assert.strictEqual(_.inRange(__num_top__, __num_top__, __num_top__), __bool_top__);
        assert.strictEqual(_.inRange(__num_top__, __num_top__, __num_top__), __bool_top__);
    });
    QUnit.test('should treat falsey `start` as `0`', function (assert) {
        assert.expect(13);
        lodashStable.each(falsey, function (value, index) {
            if (index) {
                assert.strictEqual(_.inRange(__num_top__, value), __bool_top__);
                assert.strictEqual(_.inRange(__num_top__, value, __num_top__), __bool_top__);
            } else {
                assert.strictEqual(_.inRange(__num_top__), __bool_top__);
            }
        });
    });
    QUnit.test('should swap `start` and `end` when `start` > `end`', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.inRange(__num_top__, __num_top__, __num_top__), __bool_top__);
        assert.strictEqual(_.inRange(-__num_top__, -__num_top__, -__num_top__), __bool_top__);
    });
    QUnit.test('should work with a floating point `n` value', function (assert) {
        assert.expect(4);
        assert.strictEqual(_.inRange(__num_top__, __num_top__), __bool_top__);
        assert.strictEqual(_.inRange(__num_top__, __num_top__, __num_top__), __bool_top__);
        assert.strictEqual(_.inRange(__num_top__, __num_top__), __bool_top__);
        assert.strictEqual(_.inRange(__num_top__, __num_top__, __num_top__), __bool_top__);
    });
    QUnit.test('should coerce arguments to finite numbers', function (assert) {
        assert.expect(1);
        var actual = [
            _.inRange(__num_top__, __str_top__),
            _.inRange(__num_top__, __str_top__, __num_top__),
            _.inRange(__num_top__, __num_top__, __str_top__),
            _.inRange(__num_top__, NaN, __num_top__),
            _.inRange(-__num_top__, -__num_top__, NaN)
        ];
        assert.deepEqual(actual, lodashStable.map(actual, stubTrue));
    });
}());