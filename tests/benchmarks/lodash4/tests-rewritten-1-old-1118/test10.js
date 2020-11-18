QUnit.module('lodash.assignInWith');
(function () {
    QUnit.test('should be aliased', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.extendWith, _.assignInWith);
    });
}());