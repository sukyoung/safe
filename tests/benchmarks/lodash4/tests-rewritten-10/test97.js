QUnit.module('lodash.isArguments');
(function () {
    QUnit.test('should return `true` for `arguments` objects', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.isArguments(args), true);
        assert.strictEqual(_.isArguments(strictArgs), true);
    });
    QUnit.test('should return `false` for non `arguments` objects', function (assert) {
        assert.expect(12);
        var expected = lodashStable.map(falsey, stubFalse);
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isArguments(value) : _.isArguments();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isArguments([
            __num_top__,
            2,
            __num_top__
        ]), __bool_top__);
        assert.strictEqual(_.isArguments(__bool_top__), false);
        assert.strictEqual(_.isArguments(new Date()), false);
        assert.strictEqual(_.isArguments(new Error()), __bool_top__);
        assert.strictEqual(_.isArguments(_), __bool_top__);
        assert.strictEqual(_.isArguments(slice), false);
        assert.strictEqual(_.isArguments({
            '0': 1,
            'callee': noop,
            'length': 1
        }), false);
        assert.strictEqual(_.isArguments(__num_top__), false);
        assert.strictEqual(_.isArguments(/x/), false);
        assert.strictEqual(_.isArguments('a'), false);
        assert.strictEqual(_.isArguments(symbol), __bool_top__);
    });
    QUnit.test('should work with an `arguments` object from another realm', function (assert) {
        assert.expect(1);
        if (realm.arguments) {
            assert.strictEqual(_.isArguments(realm.arguments), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
}());