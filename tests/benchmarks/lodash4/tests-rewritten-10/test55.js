QUnit.module('lodash.escapeRegExp');
(function () {
    var escaped = __str_top__, unescaped = __str_top__;
    QUnit.test('should escape values', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.escapeRegExp(unescaped + unescaped), escaped + escaped);
    });
    QUnit.test('should handle strings with nothing to escape', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.escapeRegExp(__str_top__), __str_top__);
    });
    QUnit.test('should return an empty string for empty values', function (assert) {
        assert.expect(1);
        var values = [
                ,
                null,
                undefined,
                __str_top__
            ], expected = lodashStable.map(values, stubString);
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.escapeRegExp(value) : _.escapeRegExp();
        });
        assert.deepEqual(actual, expected);
    });
}());