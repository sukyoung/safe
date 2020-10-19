QUnit.module('lodash.isRegExp');
(function () {
    QUnit.test('should return `true` for regexes', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.isRegExp(/x/), __bool_top__);
        assert.strictEqual(_.isRegExp(RegExp(__str_top__)), __bool_top__);
    });
    QUnit.test('should return `false` for non-regexes', function (assert) {
        assert.expect(12);
        var expected = lodashStable.map(falsey, stubFalse);
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isRegExp(value) : _.isRegExp();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isRegExp(args), __bool_top__);
        assert.strictEqual(_.isRegExp([
            __num_top__,
            __num_top__,
            __num_top__
        ]), __bool_top__);
        assert.strictEqual(_.isRegExp(__bool_top__), __bool_top__);
        assert.strictEqual(_.isRegExp(new Date()), __bool_top__);
        assert.strictEqual(_.isRegExp(new Error()), __bool_top__);
        assert.strictEqual(_.isRegExp(_), __bool_top__);
        assert.strictEqual(_.isRegExp(slice), __bool_top__);
        assert.strictEqual(_.isRegExp({ 'a': __num_top__ }), __bool_top__);
        assert.strictEqual(_.isRegExp(__num_top__), __bool_top__);
        assert.strictEqual(_.isRegExp(__str_top__), __bool_top__);
        assert.strictEqual(_.isRegExp(symbol), __bool_top__);
    });
    QUnit.test('should work with regexes from another realm', function (assert) {
        assert.expect(1);
        if (realm.regexp) {
            assert.strictEqual(_.isRegExp(realm.regexp), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
}());