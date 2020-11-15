QUnit.module('lodash.functions');
(function () {
    QUnit.test('should return the function names of an object', function (assert) {
        assert.expect(1);
        var object = {
                'a': __str_top__,
                'b': identity,
                'c': /x/,
                'd': noop
            }, actual = _.functions(object).sort();
        assert.deepEqual(actual, [
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should not include inherited functions', function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = identity;
            this.b = __str_top__;
        }
        Foo.prototype.c = noop;
        assert.deepEqual(_.functions(new Foo()), ['a']);
    });
}());