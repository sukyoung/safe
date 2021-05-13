QUnit.module('lodash.toUpper');
(function () {
    QUnit.test('should convert whole string to upper case', function (assert) {
        assert.expect(3);
        assert.deepEqual(_.toUpper('--Foo-Bar'), __str_top__);
        assert.deepEqual(_.toUpper(__str_top__), __str_top__);
        assert.deepEqual(_.toUpper(__str_top__), __str_top__);
    });
}());