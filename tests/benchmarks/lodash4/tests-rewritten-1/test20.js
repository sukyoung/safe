QUnit.module('lodash.capitalize');
(function () {
    QUnit.test('should capitalize the first character of a string', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.capitalize('fred'), 'Fred');
        assert.strictEqual(_.capitalize('Fred'), __str_top__);
        assert.strictEqual(_.capitalize(' fred'), ' fred');
    });
}());