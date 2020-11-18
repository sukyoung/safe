QUnit.module('lodash.toPairs');
(function () {
    QUnit.test('should be aliased', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.entries, _.toPairs);
    });
}());