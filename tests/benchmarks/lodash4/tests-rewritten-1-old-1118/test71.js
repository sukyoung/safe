QUnit.module('lodash.forEach');
(function () {
    QUnit.test('should be aliased', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.each, _.forEach);
    });
}());