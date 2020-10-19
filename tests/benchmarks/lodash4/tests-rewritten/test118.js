QUnit.module('lodash.isNull');
(function () {
    QUnit.test('should return `true` for `null` values', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.isNull(null), __bool_top__);
    });
    QUnit.test('should return `false` for non `null` values', function (assert) {
        assert.expect(13);
        var expected = lodashStable.map(falsey, function (value) {
            return value === null;
        });
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isNull(value) : _.isNull();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isNull(args), __bool_top__);
        assert.strictEqual(_.isNull([
            __num_top__,
            __num_top__,
            __num_top__
        ]), __bool_top__);
        assert.strictEqual(_.isNull(__bool_top__), __bool_top__);
        assert.strictEqual(_.isNull(new Date()), __bool_top__);
        assert.strictEqual(_.isNull(new Error()), __bool_top__);
        assert.strictEqual(_.isNull(_), __bool_top__);
        assert.strictEqual(_.isNull(slice), __bool_top__);
        assert.strictEqual(_.isNull({ 'a': __num_top__ }), __bool_top__);
        assert.strictEqual(_.isNull(__num_top__), __bool_top__);
        assert.strictEqual(_.isNull(/x/), __bool_top__);
        assert.strictEqual(_.isNull(__str_top__), __bool_top__);
        assert.strictEqual(_.isNull(symbol), __bool_top__);
    });
    QUnit.test('should work with nulls from another realm', function (assert) {
        assert.expect(1);
        if (realm.object) {
            assert.strictEqual(_.isNull(realm.null), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
}());