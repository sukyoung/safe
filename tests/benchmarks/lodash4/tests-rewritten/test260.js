QUnit.module('lodash.toPlainObject');
(function () {
    QUnit.test('should flatten inherited string keyed properties', function (assert) {
        assert.expect(1);
        function Foo() {
            this.b = __num_top__;
        }
        Foo.prototype.c = __num_top__;
        var actual = lodashStable.assign({ 'a': __num_top__ }, _.toPlainObject(new Foo()));
        assert.deepEqual(actual, {
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
        });
    });
    QUnit.test('should convert `arguments` objects to plain objects', function (assert) {
        assert.expect(1);
        var actual = _.toPlainObject(args), expected = {
                '0': __num_top__,
                '1': __num_top__,
                '2': __num_top__
            };
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should convert arrays to plain objects', function (assert) {
        assert.expect(1);
        var actual = _.toPlainObject([
                __str_top__,
                __str_top__,
                __str_top__
            ]), expected = {
                '0': __str_top__,
                '1': __str_top__,
                '2': __str_top__
            };
        assert.deepEqual(actual, expected);
    });
}());