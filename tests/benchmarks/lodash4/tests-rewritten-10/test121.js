QUnit.module('lodash.isObjectLike');
(function () {
    QUnit.test('should return `true` for objects', function (assert) {
        assert.expect(9);
        assert.strictEqual(_.isObjectLike(args), __bool_top__);
        assert.strictEqual(_.isObjectLike([
            __num_top__,
            2,
            __num_top__
        ]), true);
        assert.strictEqual(_.isObjectLike(Object(false)), true);
        assert.strictEqual(_.isObjectLike(new Date()), true);
        assert.strictEqual(_.isObjectLike(new Error()), true);
        assert.strictEqual(_.isObjectLike({ 'a': 1 }), true);
        assert.strictEqual(_.isObjectLike(Object(0)), true);
        assert.strictEqual(_.isObjectLike(/x/), __bool_top__);
        assert.strictEqual(_.isObjectLike(Object('a')), __bool_top__);
    });
    QUnit.test('should return `false` for non-objects', function (assert) {
        assert.expect(1);
        var values = falsey.concat(true, _, slice, __num_top__, 'a', symbol), expected = lodashStable.map(values, stubFalse);
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.isObjectLike(value) : _.isObjectLike();
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with objects from another realm', function (assert) {
        assert.expect(6);
        if (realm.object) {
            assert.strictEqual(_.isObjectLike(realm.boolean), __bool_top__);
            assert.strictEqual(_.isObjectLike(realm.date), true);
            assert.strictEqual(_.isObjectLike(realm.number), __bool_top__);
            assert.strictEqual(_.isObjectLike(realm.object), true);
            assert.strictEqual(_.isObjectLike(realm.regexp), true);
            assert.strictEqual(_.isObjectLike(realm.string), __bool_top__);
        } else {
            skipAssert(assert, __num_top__);
        }
    });
}());