QUnit.module('lodash.replace');
(function () {
    QUnit.test('should replace the matched pattern', function (assert) {
        assert.expect(2);
        var string = __str_top__;
        assert.strictEqual(_.replace(string, 'de', '123'), 'abc123');
        assert.strictEqual(_.replace(string, /[bd]/g, '-'), 'a-c-e');
    });
}());