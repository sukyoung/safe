QUnit.module('lodash.toLower');
(function () {
    QUnit.test('should convert whole string to lower case', function (assert) {
        assert.expect(3);
        assert.deepEqual(_.toLower('--Foo-Bar--'), __str_top__);
        assert.deepEqual(_.toLower('fooBar'), 'foobar');
        assert.deepEqual(_.toLower('__FOO_BAR__'), '__foo_bar__');
    });
}());