QUnit.module('lodash.startCase');
(function () {
    QUnit.test('should uppercase only the first character of each word', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.startCase(__str_top__), __str_top__);
        assert.strictEqual(_.startCase(__str_top__), __str_top__);
        assert.strictEqual(_.startCase(__str_top__), __str_top__);
    });
}());