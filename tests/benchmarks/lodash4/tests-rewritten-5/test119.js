QUnit.module('lodash.isNumber');
(function () {
    QUnit.test('should return `true` for numbers', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.isNumber(__num_top__), true);
        assert.strictEqual(_.isNumber(Object(0)), __bool_top__);
        assert.strictEqual(_.isNumber(NaN), __bool_top__);
    });
    QUnit.test('should return `false` for non-numbers', function (assert) {
        assert.expect(12);
        var expected = lodashStable.map(falsey, function (value) {
            return typeof value == 'number';
        });
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isNumber(value) : _.isNumber();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isNumber(args), false);
        assert.strictEqual(_.isNumber([
            1,
            2,
            3
        ]), false);
        assert.strictEqual(_.isNumber(__bool_top__), false);
        assert.strictEqual(_.isNumber(new Date()), false);
        assert.strictEqual(_.isNumber(new Error()), false);
        assert.strictEqual(_.isNumber(_), false);
        assert.strictEqual(_.isNumber(slice), false);
        assert.strictEqual(_.isNumber({ 'a': 1 }), false);
        assert.strictEqual(_.isNumber(/x/), false);
        assert.strictEqual(_.isNumber('a'), false);
        assert.strictEqual(_.isNumber(symbol), false);
    });
    QUnit.test('should work with numbers from another realm', function (assert) {
        assert.expect(1);
        if (realm.number) {
            assert.strictEqual(_.isNumber(realm.number), true);
        } else {
            skipAssert(assert);
        }
    });
}());