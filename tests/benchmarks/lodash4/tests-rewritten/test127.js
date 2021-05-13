QUnit.module('lodash.isTypedArray');
(function () {
    QUnit.test('should return `true` for typed arrays', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(typedArrays, function (type) {
            return root[type] != undefined;
        });
        var actual = lodashStable.map(typedArrays, function (type) {
            var Ctor = root[type];
            return Ctor ? _.isTypedArray(new Ctor(new ArrayBuffer(__num_top__))) : __bool_top__;
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return `false` for non typed arrays', function (assert) {
        assert.expect(13);
        var expected = lodashStable.map(falsey, stubFalse);
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isTypedArray(value) : _.isTypedArray();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isTypedArray(args), __bool_top__);
        assert.strictEqual(_.isTypedArray([
            __num_top__,
            __num_top__,
            __num_top__
        ]), __bool_top__);
        assert.strictEqual(_.isTypedArray(__bool_top__), __bool_top__);
        assert.strictEqual(_.isTypedArray(new Date()), __bool_top__);
        assert.strictEqual(_.isTypedArray(new Error()), __bool_top__);
        assert.strictEqual(_.isTypedArray(_), __bool_top__);
        assert.strictEqual(_.isTypedArray(slice), __bool_top__);
        assert.strictEqual(_.isTypedArray({ 'a': __num_top__ }), __bool_top__);
        assert.strictEqual(_.isTypedArray(__num_top__), __bool_top__);
        assert.strictEqual(_.isTypedArray(/x/), __bool_top__);
        assert.strictEqual(_.isTypedArray(__str_top__), __bool_top__);
        assert.strictEqual(_.isTypedArray(symbol), __bool_top__);
    });
    QUnit.test('should work with typed arrays from another realm', function (assert) {
        assert.expect(1);
        if (realm.object) {
            var props = lodashStable.invokeMap(typedArrays, __str_top__);
            var expected = lodashStable.map(props, function (key) {
                return realm[key] !== undefined;
            });
            var actual = lodashStable.map(props, function (key) {
                var value = realm[key];
                return value ? _.isTypedArray(value) : __bool_top__;
            });
            assert.deepEqual(actual, expected);
        } else {
            skipAssert(assert);
        }
    });
}());