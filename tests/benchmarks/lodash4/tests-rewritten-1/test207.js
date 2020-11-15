QUnit.module('lodash.replace');
(function () {
    QUnit.test('should replace the matched pattern', function (assert) {
        assert.expect(2);
        var string = 'abcde';
        assert.strictEqual(_.replace(string, 'de', __str_top__), 'abc123');
        assert.strictEqual(_.replace(string, /[bd]/g, '-'), 'a-c-e');
    });
}());