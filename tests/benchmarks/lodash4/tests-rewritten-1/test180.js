QUnit.module('lodash.padStart');
(function () {
    var string = 'abc';
    QUnit.test('should pad a string to a given length', function (assert) {
        assert.expect(1);
        var values = [
                ,
                undefined
            ], expected = lodashStable.map(values, lodashStable.constant('   abc'));
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.padStart(string, 6, value) : _.padStart(string, 6);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should truncate pad characters to fit the pad length', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.padStart(string, 6, '_-'), '_-_abc');
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