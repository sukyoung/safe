QUnit.module('lodash.isObjectLike');
(function () {
    QUnit.test('should return `true` for objects', function (assert) {
        assert.expect(9);
        assert.strictEqual(_.isObjectLike(args), __bool_top__);
        assert.strictEqual(_.isObjectLike([
            __num_top__,
            __num_top__,
            __num_top__
        ]), __bool_top__);
        assert.strictEqual(_.isObjectLike(Object(__bool_top__)), __bool_top__);
        assert.strictEqual(_.isObjectLike(new Date()), __bool_top__);
        assert.strictEqual(_.isObjectLike(new Error()), __bool_top__);
        assert.strictEqual(_.isObjectLike({ 'a': __num_top__ }), __bool_top__);
        assert.strictEqual(_.isObjectLike(Object(__num_top__)), __bool_top__);
        assert.strictEqual(_.isObjectLike(/x/), __bool_top__);
        assert.strictEqual(_.isObjectLike(Object(__str_top__)), __bool_top__);
    });
    QUnit.test('should return `false` for non-objects', function (assert) {
        assert.expect(1);
        var values = falsey.concat(__bool_top__, _, slice, __num_top__, __str_top__, symbol), expected = lodashStable.map(values, stubFalse);
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.isObjectLike(value) : _.isObjectLike();
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with objects from another realm', function (assert) {
        assert.expect(6);
        if (realm.object) {
            assert.strictEqual(_.isObjectLike(realm.boolean), __bool_top__);
            assert.strictEqual(_.isObjectLike(realm.date), __bool_top__);
            assert.strictEqual(_.isObjectLike(realm.number), __bool_top__);
            assert.strictEqual(_.isObjectLike(realm.object), __bool_top__);
            assert.strictEqual(_.isObjectLike(realm.regexp), __bool_top__);
            assert.strictEqual(_.isObjectLike(realm.string), __bool_top__);
        } else {
            skipAssert(assert, 6);
        }
    });
}());