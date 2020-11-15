QUnit.module('lodash.lowerCase');
(function () {
    QUnit.test('should lowercase as space-separated words', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.lowerCase(__str_top__), 'foo bar');
        assert.strictEqual(_.lowerCase('fooBar'), 'foo bar');
        assert.strictEqual(_.lowerCase('__FOO_BAR__'), 'foo bar');
    });
}());