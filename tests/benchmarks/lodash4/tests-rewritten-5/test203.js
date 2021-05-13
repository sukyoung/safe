QUnit.module('lodash.reject');
(function () {
    var array = [
        __num_top__,
        __num_top__,
        __num_top__
    ];
    QUnit.test('should return elements the `predicate` returns falsey for', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.reject(array, isEven), [
            __num_top__,
            __num_top__
        ]);
    });
}());