QUnit.module('lodash.isFunction');
(function () {
    QUnit.test('should return `true` for functions', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.isFunction(_), true);
        assert.strictEqual(_.isFunction(slice), __bool_top__);
    });
    QUnit.test('should return `true` for async functions', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.isFunction(asyncFunc), typeof asyncFunc == 'function');
    });
    QUnit.test('should return `true` for generator functions', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.isFunction(genFunc), typeof genFunc == 'function');
    });
    QUnit.test('should return `true` for the `Proxy` constructor', function (assert) {
        assert.expect(1);
        if (Proxy) {
            assert.strictEqual(_.isFunction(Proxy), true);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return `true` for array view constructors', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(arrayViews, function (type) {
            return objToString.call(root[type]) == funcTag;
        });
        var actual = lodashStable.map(arrayViews, function (type) {
            return _.isFunction(root[type]);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return `false` for non-functions', function (assert) {
        assert.expect(12);
        var expected = lodashStable.map(falsey, stubFalse);
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isFunction(value) : _.isFunction();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isFunction(args), false);
        assert.strictEqual(_.isFunction([
            1,
            __num_top__,
            3
        ]), __bool_top__);
        assert.strictEqual(_.isFunction(__bool_top__), __bool_top__);
        assert.strictEqual(_.isFunction(new Date()), false);
        assert.strictEqual(_.isFunction(new Error()), __bool_top__);
        assert.strictEqual(_.isFunction({ 'a': 1 }), __bool_top__);
        assert.strictEqual(_.isFunction(__num_top__), __bool_top__);
        assert.strictEqual(_.isFunction(/x/), false);
        assert.strictEqual(_.isFunction('a'), false);
        assert.strictEqual(_.isFunction(symbol), false);
        if (document) {
            assert.strictEqual(_.isFunction(document.getElementsByTagName('body')), false);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should work with a function from another realm', function (assert) {
        assert.expect(1);
        if (realm.function) {
            assert.strictEqual(_.isFunction(realm.function), true);
        } else {
            skipAssert(assert);
        }
    });
}());