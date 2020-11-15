QUnit.module('lodash.toPlainObject');
(function () {
    QUnit.test('should flatten inherited string keyed properties', function (assert) {
        assert.expect(1);
        function Foo() {
            this.b = __num_top__;
        }
        Foo.prototype.c = 3;
        var actual = lodashStable.assign({ 'a': __num_top__ }, _.toPlainObject(new Foo()));
        assert.deepEqual(actual, {
            'a': 1,
            'b': 2,
            'c': 3
        });
    });
    QUnit.test('should convert `arguments` objects to plain objects', function (assert) {
        assert.expect(1);
        var actual = _.toPlainObject(args), expected = {
                '0': __num_top__,
                '1': __num_top__,
                '2': 3
            };
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should convert arrays to plain objects', function (assert) {
        assert.expect(1);
        var actual = _.toPlainObject([
                'a',
                __str_top__,
                'c'
            ]), expected = {
                '0': 'a',
                '1': 'b',
                '2': 'c'
            };
        assert.deepEqual(actual, expected);
    });
}());