QUnit.module('lodash.lowerFirst');
(function () {
    QUnit.test('should lowercase only the first character', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.lowerFirst(__str_top__), 'fred');
        assert.strictEqual(_.lowerFirst(__str_top__), __str_top__);
        assert.strictEqual(_.lowerFirst(__str_top__), __str_top__);
    });
}());