QUnit.module('lodash.isNaN');
(function () {
    QUnit.test('should return `true` for NaNs', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.isNaN(NaN), true);
        assert.strictEqual(_.isNaN(Object(NaN)), true);
    });
    QUnit.test('should return `false` for non-NaNs', function (assert) {
        assert.expect(14);
        var expected = lodashStable.map(falsey, function (value) {
            return value !== value;
        });
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isNaN(value) : _.isNaN();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isNaN(args), false);
        assert.strictEqual(_.isNaN([
            __num_top__,
            __num_top__,
            __num_top__
        ]), false);
        assert.strictEqual(_.isNaN(__bool_top__), false);
        assert.strictEqual(_.isNaN(new Date()), __bool_top__);
        assert.strictEqual(_.isNaN(new Error()), false);
        assert.strictEqual(_.isNaN(_), __bool_top__);
        assert.strictEqual(_.isNaN(slice), false);
        assert.strictEqual(_.isNaN({ 'a': 1 }), false);
        assert.strictEqual(_.isNaN(1), false);
        assert.strictEqual(_.isNaN(Object(__num_top__)), false);
        assert.strictEqual(_.isNaN(/x/), false);
        assert.strictEqual(_.isNaN('a'), false);
        assert.strictEqual(_.isNaN(symbol), __bool_top__);
    });
    QUnit.test('should work with `NaN` from another realm', function (assert) {
        assert.expect(1);
        if (realm.object) {
            assert.strictEqual(_.isNaN(realm.nan), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
}());