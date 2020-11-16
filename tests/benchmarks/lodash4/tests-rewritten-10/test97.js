QUnit.module('lodash.isArguments');
(function () {
    QUnit.test('should return `true` for `arguments` objects', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.isArguments(args), __bool_top__);
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
            1,
            2,
            __num_top__
        ]), __bool_top__);
        assert.strictEqual(_.isArguments(true), __bool_top__);
        assert.strictEqual(_.isArguments(new Date()), false);
        assert.strictEqual(_.isArguments(new Error()), false);
        assert.strictEqual(_.isArguments(_), __bool_top__);
        assert.strictEqual(_.isArguments(slice), __bool_top__);
        assert.strictEqual(_.isArguments({
            '0': 1,
            'callee': noop,
            'length': 1
        }), __bool_top__);
        assert.strictEqual(_.isArguments(__num_top__), __bool_top__);
        assert.strictEqual(_.isArguments(/x/), false);
        assert.strictEqual(_.isArguments(__str_top__), false);
        assert.strictEqual(_.isArguments(symbol), false);
    });
    QUnit.test('should work with an `arguments` object from another realm', function (assert) {
        assert.expect(1);
        if (realm.arguments) {
            assert.strictEqual(_.isArguments(realm.arguments), true);
        } else {
            skipAssert(assert);
        }
    });
}());