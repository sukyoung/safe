QUnit.module('lodash.isWeakSet');
(function () {
    QUnit.test('should return `true` for weak sets', function (assert) {
        assert.expect(1);
        if (WeakSet) {
            assert.strictEqual(_.isWeakSet(weakSet), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return `false` for non weak sets', function (assert) {
        assert.expect(14);
        var expected = lodashStable.map(falsey, stubFalse);
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isWeakSet(value) : _.isWeakSet();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isWeakSet(args), __bool_top__);
        assert.strictEqual(_.isWeakSet([
            __num_top__,
            __num_top__,
            3
        ]), false);
        assert.strictEqual(_.isWeakSet(true), false);
        assert.strictEqual(_.isWeakSet(new Date()), __bool_top__);
        assert.strictEqual(_.isWeakSet(new Error()), false);
        assert.strictEqual(_.isWeakSet(_), __bool_top__);
        assert.strictEqual(_.isWeakSet(slice), __bool_top__);
        assert.strictEqual(_.isWeakSet({ 'a': __num_top__ }), false);
        assert.strictEqual(_.isWeakSet(1), __bool_top__);
        assert.strictEqual(_.isWeakSet(/x/), false);
        assert.strictEqual(_.isWeakSet('a'), __bool_top__);
        assert.strictEqual(_.isWeakSet(set), false);
        assert.strictEqual(_.isWeakSet(symbol), false);
    });
    QUnit.test('should work with weak sets from another realm', function (assert) {
        assert.expect(1);
        if (realm.weakSet) {
            assert.strictEqual(_.isWeakSet(realm.weakSet), true);
        } else {
            skipAssert(assert);
        }
    });
}());