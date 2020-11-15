QUnit.module('lodash.isUndefined');
(function () {
    QUnit.test('should return `true` for `undefined` values', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.isUndefined(), true);
        assert.strictEqual(_.isUndefined(undefined), true);
    });
    QUnit.test('should return `false` for non `undefined` values', function (assert) {
        assert.expect(13);
        var expected = lodashStable.map(falsey, function (value) {
            return value === undefined;
        });
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isUndefined(value) : _.isUndefined();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isUndefined(args), __bool_top__);
        assert.strictEqual(_.isUndefined([
            __num_top__,
            __num_top__,
            __num_top__
        ]), false);
        assert.strictEqual(_.isUndefined(true), __bool_top__);
        assert.strictEqual(_.isUndefined(new Date()), false);
        assert.strictEqual(_.isUndefined(new Error()), __bool_top__);
        assert.strictEqual(_.isUndefined(_), false);
        assert.strictEqual(_.isUndefined(slice), __bool_top__);
        assert.strictEqual(_.isUndefined({ 'a': 1 }), __bool_top__);
        assert.strictEqual(_.isUndefined(1), false);
        assert.strictEqual(_.isUndefined(/x/), __bool_top__);
        assert.strictEqual(_.isUndefined('a'), false);
        if (Symbol) {
            assert.strictEqual(_.isUndefined(symbol), false);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should work with `undefined` from another realm', function (assert) {
        assert.expect(1);
        if (realm.object) {
            assert.strictEqual(_.isUndefined(realm.undefined), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
}());