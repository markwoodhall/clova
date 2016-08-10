# clova

A minimal validation library for Clojure and ClojureScript.

- [API Docs](http://markwoodhall.github.io/clova)
- [Change Log](https://github.com/markwoodhall/clova/blob/master/doc/CHANGES.md)

## Status

[![Build Status](https://api.travis-ci.org/markwoodhall/clova.svg?branch=master)](https://api.travis-ci.org/repositories/markwoodhall/clova)
[![Dependency Status](https://www.versioneye.com/user/projects/566b4d044e049b003b000704/badge.svg?style=flat)](https://www.versioneye.com/user/projects/566b4d044e049b003b000704)
[![Clojars Project](https://img.shields.io/clojars/v/clova.svg)](http://clojars.org/clova)


## Installation

`clova` is available from [Clojars](https://clojars.org/clova)

#

Add the following to `project.clj` `:dependencies`:

[![Clojars Project](http://clojars.org/clova/latest-version.svg)](http://clojars.org/clova)

## Usage

Define a validation set. Validation sets are pairs of keys to validate
and the functions used to validate them.

```clojure
(let [validation-set (validation-set
                       [:email email?
                        :post-code post-code?
                        :zip-code zip-code?
                        :matches [matches? #"amatch"]
                        :url url?
                        :age [between? 18 40]
                        [:nested :value] [between? 0 10]])]

```

If you want to compose multiple validators you can.

```clojure
(let [validation-set (validation-set [:age required? [greater? 18] [lesser? 30]])])
```

You can also use an `all?` validator to achieve the above.

```clojure
(let [validation-set (validation-set [:age [all? [[greater? 18] [lesser? 30]]]])]

```

Most of the time it is useful to only apply and fail validation if a given key is present in the map getting validated, this is
the default behaviour. However if this is not the case and you wish to make a validator fail if the key is not present you can do.
Just use a `required?` validator as well.


```clojure
(let [validation-set (validation-set
                       [:email required? email?])]

(let [validation-set (validation-set
                       [:age required? [between? 18 30]])]
```

Use the validation set:

```clojure
(let [result (validate validation-set
                            {:email "test.email@googlemail.com"
                             :post-code "B11 2SB"
                             :matches "amatch"
                             :zip-code 96801
                             :url "http://google.com"
                             :age 21
                             :nested {:value 1}})]
```

Notice how we can use a seqeunce of keys to define a validation function for a value in a
nested map.

Get the validation status:

```clojure
(:valid? result)
```

Get the validation results (error messages):

```clojure
(:results result)
```

You can also specify a custom function for providing validation error messages. This function will
be called with the validator type, the target value and any arguments passed to the validator specified as arguments,
if the custom function returns nil then the default validation message will be used.

For example, we can use the `between?` validator with a custom error message, like so:

```clojure
(let [message-func (fn [v-type value args]
                    (case v-type
                      :between (str "Age is " value " but it must be between " (first args) " and " (second args))
                       nil))]
    (validate v-set {:age 9} {:default-message-fn message-func}))
```

By default clova will execute all validators and provide validation messages for all failures. You
can override this behaviour using the `:short-circuit?` option. This will stop execution of subsequent
validators after the first validation failure and will therefore only return one validation failure
message.

```clojure
(validate v-set {:email ""} {:short-circuit? true })
```

[See more usage examples.](https://github.com/markwoodhall/clova/blob/master/doc/EXAMPLES.md)

## Validators

clova has the following built in validators

1. [between?](http://markwoodhall.github.io/clova/clova.core.html#var-between.3F)
2. [email?](http://markwoodhall.github.io/clova/clova.core.html#var-email.3F)
3. [greater?](http://markwoodhall.github.io/clova/clova.core.html#var-greater.3F)
4. [lesser?](http://markwoodhall.github.io/clova/clova.core.html#var-lesser.3F)
5. [matches?](http://markwoodhall.github.io/clova/clova.core.html#var-matches.3F)
6. [negative?](http://markwoodhall.github.io/clova/clova.core.html#var-negative.3F)
7. [positive?](http://markwoodhall.github.io/clova/clova.core.html#var-positive.3F)
8. [post-code?](http://markwoodhall.github.io/clova/clova.core.html#var-post-code.3F)
9. [not-nil?](http://markwoodhall.github.io/clova/clova.core.html#var-not-nil.3F)
10. [required?](http://markwoodhall.github.io/clova/clova.core.html#var-required.3F)
11. [url?](http://markwoodhall.github.io/clova/clova.core.html#var-url.3F)
12. [zip-code?](http://markwoodhall.github.io/clova/clova.core.html#var-zip-code.3F)
13. [length?](http://markwoodhall.github.io/clova/clova.core.html#var-length.3F)
14. [longer?](http://markwoodhall.github.io/clova/clova.core.html#var-longer.3F)
15. [shorter?](http://markwoodhall.github.io/clova/clova.core.html#var-shorter.3F)
16. [one-of?](http://markwoodhall.github.io/clova/clova.core.html#var-one-of.3F)
17. [all?](http://markwoodhall.github.io/clova/clova.core.html#var-all.3F)
18. [credit-card?](http://markwoodhall.github.io/clova/clova.core.html#var-credit-card.3F)
19. [numeric?](http://markwoodhall.github.io/clova/clova.core.html#var-numeric.3F)
20. [stringy?](http://markwoodhall.github.io/clova/clova.core.html#var-stringy.3F)
21. [date?](http://markwoodhall.github.io/clova/clova.core.html#var-date.3F)
22. [before?](http://markwoodhall.github.io/clova/clova.core.html#var-before.3F)
23. [after?](http://markwoodhall.github.io/clova/clova.core.html#var-after.3F)
24. [=date?](http://markwoodhall.github.io/clova/clova.core.html#var-.3Ddate.3F)
25. [=?](http://markwoodhall.github.io/clova/clova.core.html#var-.3D.3F)
26. [alphanumeric?](http://markwoodhall.github.io/clova/clova.core.html#var-alphanumeric.3F)

## License

Copyright © 2016 Mark Woodhall

Released under the MIT License: http://www.opensource.org/licenses/mit-license.php
