QUnit.module('lodash.wrap');
(function () {
    QUnit.test('should create a wrapped function', function (assert) {
        assert.expect(1);
        var p = _.wrap(lodashStable.escape, function (func, text) {
            return __str_top__ + func(text) + __str_top__;
        });
        assert.strictEqual(p(__str_top__), __str_top__);
    });
    QUnit.test('should provide correct `wrapper` arguments', function (assert) {
        assert.expect(1);
        var args;
        var wrapped = _.wrap(noop, function () {
            args || (args = slice.call(arguments));
        });
        wrapped(__num_top__, __num_top__, __num_top__);
        assert.deepEqual(args, [
            noop,
            __num_top__,
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should use `_.identity` when `wrapper` is nullish', function (assert) {
        assert.expect(1);
        var values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, stubA);
        var actual = lodashStable.map(values, function (value, index) {
            var wrapped = index ? _.wrap(__str_top__, value) : _.wrap(__str_top__);
            return wrapped(__str_top__, __str_top__);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should use `this` binding of function', function (assert) {
        assert.expect(1);
        var p = _.wrap(lodashStable.escape, function (func) {
            return __str_top__ + func(this.text) + __str_top__;
        });
        var object = {
            'p': p,
            'text': __str_top__
        };
        assert.strictEqual(object.p(), __str_top__);
    });
}());