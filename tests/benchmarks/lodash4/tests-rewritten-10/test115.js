QUnit.module('lodash.isNaN');
(function () {
    QUnit.test('should return `true` for NaNs', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.isNaN(NaN), __bool_top__);
        assert.strictEqual(_.isNaN(Object(NaN)), __bool_top__);
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
            1,
            2,
            __num_top__
        ]), false);
        assert.strictEqual(_.isNaN(true), __bool_top__);
        assert.strictEqual(_.isNaN(new Date()), false);
        assert.strictEqual(_.isNaN(new Error()), false);
        assert.strictEqual(_.isNaN(_), __bool_top__);
        assert.strictEqual(_.isNaN(slice), false);
        assert.strictEqual(_.isNaN({ 'a': __num_top__ }), __bool_top__);
        assert.strictEqual(_.isNaN(1), __bool_top__);
        assert.strictEqual(_.isNaN(Object(1)), false);
        assert.strictEqual(_.isNaN(/x/), false);
        assert.strictEqual(_.isNaN(__str_top__), __bool_top__);
        assert.strictEqual(_.isNaN(symbol), false);
    });
    QUnit.test('should work with `NaN` from another realm', function (assert) {
        assert.expect(1);
        if (realm.object) {
            assert.strictEqual(_.isNaN(realm.nan), true);
        } else {
            skipAssert(assert);
        }
    });
}());