QUnit.module('lodash.toLower');
(function () {
    QUnit.test('should convert whole string to lower case', function (assert) {
        assert.expect(3);
        assert.deepEqual(_.toLower(__str_top__), __str_top__);
        assert.deepEqual(_.toLower(__str_top__), __str_top__);
        assert.deepEqual(_.toLower(__str_top__), __str_top__);
    });
}());