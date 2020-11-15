QUnit.module('lodash.isWeakMap');
(function () {
    QUnit.test('should return `true` for weak maps', function (assert) {
        assert.expect(1);
        if (WeakMap) {
            assert.strictEqual(_.isWeakMap(weakMap), true);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return `false` for non weak maps', function (assert) {
        assert.expect(14);
        var expected = lodashStable.map(falsey, stubFalse);
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isWeakMap(value) : _.isWeakMap();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isWeakMap(args), false);
        assert.strictEqual(_.isWeakMap([
            __num_top__,
            __num_top__,
            3
        ]), false);
        assert.strictEqual(_.isWeakMap(__bool_top__), false);
        assert.strictEqual(_.isWeakMap(new Date()), false);
        assert.strictEqual(_.isWeakMap(new Error()), __bool_top__);
        assert.strictEqual(_.isWeakMap(_), false);
        assert.strictEqual(_.isWeakMap(slice), __bool_top__);
        assert.strictEqual(_.isWeakMap({ 'a': __num_top__ }), false);
        assert.strictEqual(_.isWeakMap(map), __bool_top__);
        assert.strictEqual(_.isWeakMap(1), __bool_top__);
        assert.strictEqual(_.isWeakMap(/x/), __bool_top__);
        assert.strictEqual(_.isWeakMap('a'), false);
        assert.strictEqual(_.isWeakMap(symbol), __bool_top__);
    });
    QUnit.test('should work for objects with a non-function `constructor` (test in IE 11)', function (assert) {
        assert.expect(1);
        var values = [
                false,
                true
            ], expected = lodashStable.map(values, stubFalse);
        var actual = lodashStable.map(values, function (value) {
            return _.isWeakMap({ 'constructor': value });
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with weak maps from another realm', function (assert) {
        assert.expect(1);
        if (realm.weakMap) {
            assert.strictEqual(_.isWeakMap(realm.weakMap), true);
        } else {
            skipAssert(assert);
        }
    });
}());