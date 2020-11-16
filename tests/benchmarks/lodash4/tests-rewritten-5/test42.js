QUnit.module('lodash.defer');
(function () {
    QUnit.test('should defer `func` execution', function (assert) {
        assert.expect(1);
        var done = assert.async();
        var pass = __bool_top__;
        _.defer(function () {
            pass = __bool_top__;
        });
        setTimeout(function () {
            assert.ok(pass);
            done();
        }, __num_top__);
    });
    QUnit.test('should provide additional arguments to `func`', function (assert) {
        assert.expect(1);
        var done = assert.async();
        var args;
        _.defer(function () {
            args = slice.call(arguments);
        }, 1, 2);
        setTimeout(function () {
            assert.deepEqual(args, [
                __num_top__,
                2
            ]);
            done();
        }, 32);
    });
    QUnit.test('should be cancelable', function (assert) {
        assert.expect(1);
        var done = assert.async();
        var pass = true, timerId = _.defer(function () {
                pass = false;
            });
        clearTimeout(timerId);
        setTimeout(function () {
            assert.ok(pass);
            done();
        }, __num_top__);
    });
}());