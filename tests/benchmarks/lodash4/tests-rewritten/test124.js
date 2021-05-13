QUnit.module('lodash.isSet');
(function () {
    QUnit.test('should return `true` for sets', function (assert) {
        assert.expect(1);
        if (Set) {
            assert.strictEqual(_.isSet(set), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return `false` for non-sets', function (assert) {
        assert.expect(14);
        var expected = lodashStable.map(falsey, stubFalse);
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isSet(value) : _.isSet();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isSet(args), __bool_top__);
        assert.strictEqual(_.isSet([
            __num_top__,
            __num_top__,
            __num_top__
        ]), __bool_top__);
        assert.strictEqual(_.isSet(__bool_top__), __bool_top__);
        assert.strictEqual(_.isSet(new Date()), __bool_top__);
        assert.strictEqual(_.isSet(new Error()), __bool_top__);
        assert.strictEqual(_.isSet(_), __bool_top__);
        assert.strictEqual(_.isSet(slice), __bool_top__);
        assert.strictEqual(_.isSet({ 'a': __num_top__ }), __bool_top__);
        assert.strictEqual(_.isSet(__num_top__), __bool_top__);
        assert.strictEqual(_.isSet(/x/), __bool_top__);
        assert.strictEqual(_.isSet(__str_top__), __bool_top__);
        assert.strictEqual(_.isSet(symbol), __bool_top__);
        assert.strictEqual(_.isSet(weakSet), __bool_top__);
    });
    QUnit.test('should work for objects with a non-function `constructor` (test in IE 11)', function (assert) {
        assert.expect(1);
        var values = [
                __bool_top__,
                __bool_top__
            ], expected = lodashStable.map(values, stubFalse);
        var actual = lodashStable.map(values, function (value) {
            return _.isSet({ 'constructor': value });
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with weak sets from another realm', function (assert) {
        assert.expect(1);
        if (realm.set) {
            assert.strictEqual(_.isSet(realm.set), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
}());