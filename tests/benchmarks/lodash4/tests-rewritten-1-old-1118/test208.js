QUnit.module('lodash.result');
(function () {
    var object = {
        'a': 1,
        'b': stubB
    };
    QUnit.test('should invoke function values', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.result(object, 'b'), 'b');
    });
    QUnit.test('should invoke default function values', function (assert) {
        assert.expect(1);
        var actual = _.result(object, 'c', object.b);
        assert.strictEqual(actual, 'b');
    });
    QUnit.test('should invoke nested function values', function (assert) {
        assert.expect(2);
        var value = { 'a': lodashStable.constant({ 'b': stubB }) };
        lodashStable.each([
            'a.b',
            [
                __str_top__,
                'b'
            ]
        ], function (path) {
            assert.strictEqual(_.result(value, path), 'b');
        });
    });
    QUnit.test('should invoke deep property methods with the correct `this` binding', function (assert) {
        assert.expect(2);
        var value = {
            'a': {
                'b': function () {
                    return this.c;
                },
                'c': 1
            }
        };
        lodashStable.each([
            'a.b',
            [
                'a',
                'b'
            ]
        ], function (path) {
            assert.strictEqual(_.result(value, path), 1);
        });
    });
}());