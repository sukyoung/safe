QUnit.module('lodash.isObject');
(function () {
    QUnit.test('should return `true` for objects', function (assert) {
        assert.expect(13);
        assert.strictEqual(_.isObject(args), __bool_top__);
        assert.strictEqual(_.isObject([
            __num_top__,
            __num_top__,
            __num_top__
        ]), __bool_top__);
        assert.strictEqual(_.isObject(Object(__bool_top__)), __bool_top__);
        assert.strictEqual(_.isObject(new Date()), __bool_top__);
        assert.strictEqual(_.isObject(new Error()), __bool_top__);
        assert.strictEqual(_.isObject(_), __bool_top__);
        assert.strictEqual(_.isObject(slice), __bool_top__);
        assert.strictEqual(_.isObject({ 'a': __num_top__ }), __bool_top__);
        assert.strictEqual(_.isObject(Object(__num_top__)), __bool_top__);
        assert.strictEqual(_.isObject(/x/), __bool_top__);
        assert.strictEqual(_.isObject(Object(__str_top__)), __bool_top__);
        if (document) {
            assert.strictEqual(_.isObject(body), __bool_top__);
        } else {
            skipAssert(assert);
        }
        if (Symbol) {
            assert.strictEqual(_.isObject(Object(symbol)), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return `false` for non-objects', function (assert) {
        assert.expect(1);
        var values = falsey.concat(__bool_top__, __num_top__, __str_top__, symbol), expected = lodashStable.map(values, stubFalse);
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.isObject(value) : _.isObject();
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with objects from another realm', function (assert) {
        assert.expect(8);
        if (realm.element) {
            assert.strictEqual(_.isObject(realm.element), __bool_top__);
        } else {
            skipAssert(assert);
        }
        if (realm.object) {
            assert.strictEqual(_.isObject(realm.boolean), __bool_top__);
            assert.strictEqual(_.isObject(realm.date), __bool_top__);
            assert.strictEqual(_.isObject(realm.function), __bool_top__);
            assert.strictEqual(_.isObject(realm.number), __bool_top__);
            assert.strictEqual(_.isObject(realm.object), __bool_top__);
            assert.strictEqual(_.isObject(realm.regexp), __bool_top__);
            assert.strictEqual(_.isObject(realm.string), __bool_top__);
        } else {
            skipAssert(assert, 7);
        }
    });
}());