QUnit.module('lodash.functions');
(function () {
    QUnit.test('should return the function names of an object', function (assert) {
        assert.expect(1);
        var object = {
                'a': 'a',
                'b': identity,
                'c': /x/,
                'd': noop
            }, actual = _.functions(object).sort();
        assert.deepEqual(actual, [
            'b',
            'd'
        ]);
    });
    QUnit.test('should not include inherited functions', function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = identity;
            this.b = 'b';
        }
        Foo.prototype.c = noop;
        assert.deepEqual(_.functions(new Foo()), [__str_top__]);
    });
}());