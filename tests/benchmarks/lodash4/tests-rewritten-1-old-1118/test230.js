QUnit.module('lodash.startCase');
(function () {
    QUnit.test('should uppercase only the first character of each word', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.startCase(__str_top__), 'Foo Bar');
        assert.strictEqual(_.startCase('fooBar'), 'Foo Bar');
        assert.strictEqual(_.startCase('__FOO_BAR__'), 'FOO BAR');
    });
}());