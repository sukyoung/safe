QUnit.module('lodash.pullAllBy');
(function () {
    QUnit.test('should accept an `iteratee`', function (assert) {
        assert.expect(1);
        var array = [
            { 'x': __num_top__ },
            { 'x': __num_top__ },
            { 'x': __num_top__ },
            { 'x': __num_top__ }
        ];
        var actual = _.pullAllBy(array, [
            { 'x': __num_top__ },
            { 'x': __num_top__ }
        ], function (object) {
            return object.x;
        });
        assert.deepEqual(actual, [{ 'x': __num_top__ }]);
    });
    QUnit.test('should provide correct `iteratee` arguments', function (assert) {
        assert.expect(1);
        var args, array = [
                { 'x': __num_top__ },
                { 'x': __num_top__ },
                { 'x': __num_top__ },
                { 'x': __num_top__ }
            ];
        _.pullAllBy(array, [
            { 'x': __num_top__ },
            { 'x': __num_top__ }
        ], function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [{ 'x': __num_top__ }]);
    });
}());