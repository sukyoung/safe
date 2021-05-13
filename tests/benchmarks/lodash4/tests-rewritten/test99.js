QUnit.module('lodash.isArrayBuffer');
(function () {
    QUnit.test('should return `true` for array buffers', function (assert) {
        assert.expect(1);
        if (ArrayBuffer) {
            assert.strictEqual(_.isArrayBuffer(arrayBuffer), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return `false` for non array buffers', function (assert) {
        assert.expect(13);
        var expected = lodashStable.map(falsey, stubFalse);
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isArrayBuffer(value) : _.isArrayBuffer();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isArrayBuffer(args), __bool_top__);
        assert.strictEqual(_.isArrayBuffer([__num_top__]), __bool_top__);
        assert.strictEqual(_.isArrayBuffer(__bool_top__), __bool_top__);
        assert.strictEqual(_.isArrayBuffer(new Date()), __bool_top__);
        assert.strictEqual(_.isArrayBuffer(new Error()), __bool_top__);
        assert.strictEqual(_.isArrayBuffer(_), __bool_top__);
        assert.strictEqual(_.isArrayBuffer(slice), __bool_top__);
        assert.strictEqual(_.isArrayBuffer({ 'a': __num_top__ }), __bool_top__);
        assert.strictEqual(_.isArrayBuffer(__num_top__), __bool_top__);
        assert.strictEqual(_.isArrayBuffer(/x/), __bool_top__);
        assert.strictEqual(_.isArrayBuffer(__str_top__), __bool_top__);
        assert.strictEqual(_.isArrayBuffer(symbol), __bool_top__);
    });
    QUnit.test('should work with array buffers from another realm', function (assert) {
        assert.expect(1);
        if (realm.arrayBuffer) {
            assert.strictEqual(_.isArrayBuffer(realm.arrayBuffer), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
}());