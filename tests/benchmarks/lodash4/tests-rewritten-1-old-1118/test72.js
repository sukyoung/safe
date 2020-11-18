QUnit.module('lodash.forEachRight');
(function () {
    QUnit.test('should be aliased', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.eachRight, _.forEachRight);
    });
}());