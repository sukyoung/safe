QUnit.module('lodash.isMap');
(function () {
    QUnit.test('should return `true` for maps', function (assert) {
        assert.expect(1);
        if (Map) {
            assert.strictEqual(_.isMap(map), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return `false` for non-maps', function (assert) {
        assert.expect(14);
        var expected = lodashStable.map(falsey, stubFalse);
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isMap(value) : _.isMap();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isMap(args), __bool_top__);
        assert.strictEqual(_.isMap([
            __num_top__,
            __num_top__,
            __num_top__
        ]), __bool_top__);
        assert.strictEqual(_.isMap(__bool_top__), __bool_top__);
        assert.strictEqual(_.isMap(new Date()), __bool_top__);
        assert.strictEqual(_.isMap(new Error()), __bool_top__);
        assert.strictEqual(_.isMap(_), __bool_top__);
        assert.strictEqual(_.isMap(slice), __bool_top__);
        assert.strictEqual(_.isMap({ 'a': __num_top__ }), __bool_top__);
        assert.strictEqual(_.isMap(__num_top__), __bool_top__);
        assert.strictEqual(_.isMap(/x/), __bool_top__);
        assert.strictEqual(_.isMap(__str_top__), __bool_top__);
        assert.strictEqual(_.isMap(symbol), __bool_top__);
        assert.strictEqual(_.isMap(weakMap), __bool_top__);
    });
    QUnit.test('should work for objects with a non-function `constructor` (test in IE 11)', function (assert) {
        assert.expect(1);
        var values = [
                __bool_top__,
                __bool_top__
            ], expected = lodashStable.map(values, stubFalse);
        var actual = lodashStable.map(values, function (value) {
            return _.isMap({ 'constructor': value });
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with maps from another realm', function (assert) {
        assert.expect(1);
        if (realm.map) {
            assert.strictEqual(_.isMap(realm.map), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
}());