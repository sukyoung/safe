QUnit.module('lodash.isBoolean');
(function () {
    QUnit.test('should return `true` for booleans', function (assert) {
        assert.expect(4);
        assert.strictEqual(_.isBoolean(__bool_top__), __bool_top__);
        assert.strictEqual(_.isBoolean(__bool_top__), __bool_top__);
        assert.strictEqual(_.isBoolean(Object(__bool_top__)), __bool_top__);
        assert.strictEqual(_.isBoolean(Object(__bool_top__)), __bool_top__);
    });
    QUnit.test('should return `false` for non-booleans', function (assert) {
        assert.expect(12);
        var expected = lodashStable.map(falsey, function (value) {
            return value === __bool_top__;
        });
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isBoolean(value) : _.isBoolean();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isBoolean(args), __bool_top__);
        assert.strictEqual(_.isBoolean([
            __num_top__,
            __num_top__,
            __num_top__
        ]), __bool_top__);
        assert.strictEqual(_.isBoolean(new Date()), __bool_top__);
        assert.strictEqual(_.isBoolean(new Error()), __bool_top__);
        assert.strictEqual(_.isBoolean(_), __bool_top__);
        assert.strictEqual(_.isBoolean(slice), __bool_top__);
        assert.strictEqual(_.isBoolean({ 'a': __num_top__ }), __bool_top__);
        assert.strictEqual(_.isBoolean(__num_top__), __bool_top__);
        assert.strictEqual(_.isBoolean(/x/), __bool_top__);
        assert.strictEqual(_.isBoolean(__str_top__), __bool_top__);
        assert.strictEqual(_.isBoolean(symbol), __bool_top__);
    });
    QUnit.test('should work with a boolean from another realm', function (assert) {
        assert.expect(1);
        if (realm.boolean) {
            assert.strictEqual(_.isBoolean(realm.boolean), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
}());