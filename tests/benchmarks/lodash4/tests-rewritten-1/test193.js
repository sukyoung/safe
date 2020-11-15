QUnit.module('lodash.pullAllBy');
(function () {
    QUnit.test('should accept an `iteratee`', function (assert) {
        assert.expect(1);
        var array = [
            { 'x': 1 },
            { 'x': 2 },
            { 'x': 3 },
            { 'x': 1 }
        ];
        var actual = _.pullAllBy(array, [
            { 'x': 1 },
            { 'x': __num_top__ }
        ], function (object) {
            return object.x;
        });
        assert.deepEqual(actual, [{ 'x': 2 }]);
    });
    QUnit.test('should provide correct `iteratee` arguments', function (assert) {
        assert.expect(1);
        var args, array = [
                { 'x': 1 },
                { 'x': 2 },
                { 'x': 3 },
                { 'x': 1 }
            ];
        _.pullAllBy(array, [
            { 'x': 1 },
            { 'x': 3 }
        ], function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [{ 'x': 1 }]);
    });
}());