QUnit.module('lodash.upperCase');
(function () {
    QUnit.test('should uppercase as space-separated words', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.upperCase('--foo-bar--'), 'FOO BAR');
        assert.strictEqual(_.upperCase(__str_top__), 'FOO BAR');
        assert.strictEqual(_.upperCase('__foo_bar__'), 'FOO BAR');
    });
}());