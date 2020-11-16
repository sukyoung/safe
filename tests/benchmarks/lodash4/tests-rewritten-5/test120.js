QUnit.module('lodash.isObject');
(function () {
    QUnit.test('should return `true` for objects', function (assert) {
        assert.expect(13);
        assert.strictEqual(_.isObject(args), true);
        assert.strictEqual(_.isObject([
            1,
            2,
            3
        ]), true);
        assert.strictEqual(_.isObject(Object(__bool_top__)), true);
        assert.strictEqual(_.isObject(new Date()), true);
        assert.strictEqual(_.isObject(new Error()), true);
        assert.strictEqual(_.isObject(_), true);
        assert.strictEqual(_.isObject(slice), __bool_top__);
        assert.strictEqual(_.isObject({ 'a': __num_top__ }), true);
        assert.strictEqual(_.isObject(Object(__num_top__)), true);
        assert.strictEqual(_.isObject(/x/), true);
        assert.strictEqual(_.isObject(Object('a')), true);
        if (document) {
            assert.strictEqual(_.isObject(body), true);
        } else {
            skipAssert(assert);
        }
        if (Symbol) {
            assert.strictEqual(_.isObject(Object(symbol)), true);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return `false` for non-objects', function (assert) {
        assert.expect(1);
        var values = falsey.concat(true, 1, 'a', symbol), expected = lodashStable.map(values, stubFalse);
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.isObject(value) : _.isObject();
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with objects from another realm', function (assert) {
        assert.expect(8);
        if (realm.element) {
            assert.strictEqual(_.isObject(realm.element), true);
        } else {
            skipAssert(assert);
        }
        if (realm.object) {
            assert.strictEqual(_.isObject(realm.boolean), true);
            assert.strictEqual(_.isObject(realm.date), true);
            assert.strictEqual(_.isObject(realm.function), true);
            assert.strictEqual(_.isObject(realm.number), true);
            assert.strictEqual(_.isObject(realm.object), true);
            assert.strictEqual(_.isObject(realm.regexp), true);
            assert.strictEqual(_.isObject(realm.string), true);
        } else {
            skipAssert(assert, 7);
        }
    });
}());