QUnit.module('lodash.wrap');
(function () {
    QUnit.test('should create a wrapped function', function (assert) {
        assert.expect(1);
        var p = _.wrap(lodashStable.escape, function (func, text) {
            return '<p>' + func(text) + '</p>';
        });
        assert.strictEqual(p('fred, barney, & pebbles'), '<p>fred, barney, &amp; pebbles</p>');
    });
    QUnit.test('should provide correct `wrapper` arguments', function (assert) {
        assert.expect(1);
        var args;
        var wrapped = _.wrap(noop, function () {
            args || (args = slice.call(arguments));
        });
        wrapped(1, 2, 3);
        assert.deepEqual(args, [
            noop,
            1,
            2,
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
            var wrapped = index ? _.wrap('a', value) : _.wrap('a');
            return wrapped('b', __str_top__);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should use `this` binding of function', function (assert) {
        assert.expect(1);
        var p = _.wrap(lodashStable.escape, function (func) {
            return '<p>' + func(this.text) + '</p>';
        });
        var object = {
            'p': p,
            'text': 'fred, barney, & pebbles'
        };
        assert.strictEqual(object.p(), '<p>fred, barney, &amp; pebbles</p>');
    });
}());