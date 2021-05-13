QUnit.module('lodash.isNumber');
(function () {
    QUnit.test('should return `true` for numbers', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.isNumber(__num_top__), __bool_top__);
        assert.strictEqual(_.isNumber(Object(__num_top__)), __bool_top__);
        assert.strictEqual(_.isNumber(NaN), __bool_top__);
    });
    QUnit.test('should return `false` for non-numbers', function (assert) {
        assert.expect(12);
        var expected = lodashStable.map(falsey, function (value) {
            return typeof value == __str_top__;
        });
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isNumber(value) : _.isNumber();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isNumber(args), __bool_top__);
        assert.strictEqual(_.isNumber([
            __num_top__,
            __num_top__,
            __num_top__
        ]), __bool_top__);
        assert.strictEqual(_.isNumber(__bool_top__), __bool_top__);
        assert.strictEqual(_.isNumber(new Date()), __bool_top__);
        assert.strictEqual(_.isNumber(new Error()), __bool_top__);
        assert.strictEqual(_.isNumber(_), __bool_top__);
        assert.strictEqual(_.isNumber(slice), __bool_top__);
        assert.strictEqual(_.isNumber({ 'a': __num_top__ }), __bool_top__);
        assert.strictEqual(_.isNumber(/x/), __bool_top__);
        assert.strictEqual(_.isNumber(__str_top__), __bool_top__);
        assert.strictEqual(_.isNumber(symbol), __bool_top__);
    });
    QUnit.test('should work with numbers from another realm', function (assert) {
        assert.expect(1);
        if (realm.number) {
            assert.strictEqual(_.isNumber(realm.number), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
}());