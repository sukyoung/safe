function f() {
    function f() {
        f(); // inner f win (bug)
    }
    f(); // inner f win (bug)
}
f(); // outer f win (not bug)
