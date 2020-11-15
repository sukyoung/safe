QUnit.module('lodash.padStart');
(function () {
    var string = __str_top__;
    QUnit.test('should pad a string to a given length', function (assert) {
        assert.expect(1);
        var values = [
                ,
                undefined
            ], expected = lodashStable.map(values, lodashStable.constant(__str_top__));
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.padStart(string, 6, value) : _.padStart(string, __num_top__);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should truncate pad characters to fit the pad length', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.padStart(string, 6, __str_top__), '_-_abc');
    });
    QUnit.test('should coerce `string` to a string', function (assert) {
        assert.expect(1);
        var values = [
                Object(string),
                { 'toString': lodashStable.constant(string) }
            ], expected = lodashStable.map(values, stubTrue);
        var actual = lodashStable.map(values, function (value) {
            return _.padStart(value, __num_top__) === '   abc';
        });
        assert.deepEqual(actual, expected);
    });
}());