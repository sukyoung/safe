QUnit.module('lodash.isNative');
(function () {
    QUnit.test('should return `true` for native methods', function (assert) {
        assert.expect(1);
        var values = [
                Array,
                body && body.cloneNode,
                create,
                root.encodeURI,
                Promise,
                slice,
                Uint8Array
            ], expected = lodashStable.map(values, Boolean), actual = lodashStable.map(values, _.isNative);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return `false` for non-native methods', function (assert) {
        assert.expect(12);
        var expected = lodashStable.map(falsey, stubFalse);
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isNative(value) : _.isNative();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isNative(args), false);
        assert.strictEqual(_.isNative([
            __num_top__,
            __num_top__,
            __num_top__
        ]), false);
        assert.strictEqual(_.isNative(true), __bool_top__);
        assert.strictEqual(_.isNative(new Date()), false);
        assert.strictEqual(_.isNative(new Error()), false);
        assert.strictEqual(_.isNative(_), false);
        assert.strictEqual(_.isNative({ 'a': 1 }), __bool_top__);
        assert.strictEqual(_.isNative(1), false);
        assert.strictEqual(_.isNative(/x/), false);
        assert.strictEqual(_.isNative('a'), false);
        assert.strictEqual(_.isNative(symbol), false);
    });
    QUnit.test('should work with native functions from another realm', function (assert) {
        assert.expect(2);
        if (realm.element) {
            assert.strictEqual(_.isNative(realm.element.cloneNode), __bool_top__);
        } else {
            skipAssert(assert);
        }
        if (realm.object) {
            assert.strictEqual(_.isNative(realm.object.valueOf), true);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should throw an error if core-js is detected', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            var lodash = _.runInContext({ '__core-js_shared__': {} });
            assert.raises(function () {
                lodash.isNative(noop);
            });
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should detect methods masquerading as native (test in Node.js)', function (assert) {
        assert.expect(2);
        if (!amd && _._baseEach) {
            var path = require(__str_top__), basePath = path.dirname(filePath), uid = __str_top__, coreKey = '__core-js_shared__', fakeSrcKey = 'Symbol(src)_1.' + uid;
            root[coreKey] = { 'keys': { 'IE_PROTO': __str_top__ + uid } };
            emptyObject(require.cache);
            var baseIsNative = interopRequire(path.join(basePath, '_baseIsNative'));
            assert.strictEqual(baseIsNative(slice), true);
            slice[fakeSrcKey] = slice + '';
            assert.strictEqual(baseIsNative(slice), __bool_top__);
            delete slice[fakeSrcKey];
            delete root[coreKey];
        } else {
            skipAssert(assert, 2);
        }
    });
}());