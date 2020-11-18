QUnit.module('lodash.pad');
(function () {
    var string = 'abc';
    QUnit.test('should pad a string to a given length', function (assert) {
        assert.expect(1);
        var values = [
                ,
                undefined
            ], expected = lodashStable.map(values, lodashStable.constant(' abc  '));
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.pad(string, 6, value) : _.pad(string, 6);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should truncate pad characters to fit the pad length', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.pad(string, 8), __str_top__);
        assert.strictEqual(_.pad(string, 8, '_-'), '_-abc_-_');
    });
    QUnit.test('should coerce `string` to a string', function (assert) {
        assert.expect(1);
        var values = [
                Object(string),
                { 'toString': lodashStable.constant(string) }
            ], expected = lodashStable.map(values, stubTrue);
        var actual = lodashStable.map(values, function (value) {
            return _.pad(value, 6) === ' abc  ';
        });
        assert.deepEqual(actual, expected);
    });
}());