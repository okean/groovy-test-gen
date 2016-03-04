def x = null

assert x == null
assert x != "abc"
assert x != "foo"

x = "abc"

assert x != "foo"
assert x != null
assert x != "def"
assert x == "abc"

assert x.equals("abc")

assert !x.equals("def")
assert !false
assert !(1 == 2)
assert !(1 > 3)
assert !(1 != 1)

assert true: "Assert after colon"