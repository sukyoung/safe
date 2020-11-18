QUnit.module('lodash.isNull');
(function () {
    QUnit.test('should return `true` for `null` values', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.isNull(null), true);
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
        assert.strictEqual(_.isNull(args), false);
        assert.strictEqual(_.isNull([
            1,
            2,
            3
        ]), false);
        assert.strictEqual(_.isNull(true), false);
        assert.strictEqual(_.isNull(new Date()), false);
        assert.strictEqual(_.isNull(new Error()), false);
        assert.strictEqual(_.isNull(_), false);
        assert.strictEqual(_.isNull(slice), false);
        assert.strictEqual(_.isNull({ 'a': 1 }), false);
        assert.strictEqual(_.isNull(1), false);
        assert.strictEqual(_.isNull(/x/), false);
        assert.strictEqual(_.isNull('a'), false);
        assert.strictEqual(_.isNull(symbol), false);
    });
    QUnit.test('should work with nulls from another realm', function (assert) {
        assert.expect(1);
        if (realm.object) {
            assert.strictEqual(_.isNull(realm.null), true);
        } else {
            skipAssert(assert);
        }
    });
}());