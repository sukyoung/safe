QUnit.module('lodash.wrap');
(function () {
    QUnit.test('should create a wrapped function', function (assert) {
        assert.expect(1);
        var p = _.wrap(lodashStable.escape, function (func, text) {
            return __str_top__ + func(text) + __str_top__;
        });
        assert.strictEqual(p('fred, barney, & pebbles'), __str_top__);
    });
    QUnit.test('should provide correct `wrapper` arguments', function (assert) {
        assert.expect(1);
        var args;
        var wrapped = _.wrap(noop, function () {
            args || (args = slice.call(arguments));
        });
        wrapped(1, __num_top__, 3);
        assert.deepEqual(args, [
            noop,
            1,
            __num_top__,
            3
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
            var wrapped = index ? _.wrap('a', value) : _.wrap(__str_top__);
            return wrapped('b', __str_top__);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should use `this` binding of function', function (assert) {
        assert.expect(1);
        var p = _.wrap(lodashStable.escape, function (func) {
            return __str_top__ + func(this.text) + '</p>';
        });
        var object = {
            'p': p,
            'text': __str_top__
        };
        assert.strictEqual(object.p(), __str_top__);
    });
}());