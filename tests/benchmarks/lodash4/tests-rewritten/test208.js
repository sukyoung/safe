QUnit.module('lodash.result');
(function () {
    var object = {
        'a': __num_top__,
        'b': stubB
    };
    QUnit.test('should invoke function values', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.result(object, __str_top__), __str_top__);
    });
    QUnit.test('should invoke default function values', function (assert) {
        assert.expect(1);
        var actual = _.result(object, __str_top__, object.b);
        assert.strictEqual(actual, __str_top__);
    });
    QUnit.test('should invoke nested function values', function (assert) {
        assert.expect(2);
        var value = { 'a': lodashStable.constant({ 'b': stubB }) };
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            assert.strictEqual(_.result(value, path), __str_top__);
        });
    });
    QUnit.test('should invoke deep property methods with the correct `this` binding', function (assert) {
        assert.expect(2);
        var value = {
            'a': {
                'b': function () {
                    return this.c;
                },
                'c': __num_top__
            }
        };
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            assert.strictEqual(_.result(value, path), __num_top__);
        });
    });
}());