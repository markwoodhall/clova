# clova

A minimal validation library for Clojure and ClojureScript.

## Status

[![Build Status](https://api.travis-ci.org/markwoodhall/clova.svg?branch=master)](https://api.travis-ci.org/repositories/markwoodhall/clova)

## Installation


`clova` is available from [Clojars](https://clojars.org/clova)

#

Add the following to `project.clj` `:dependencies`:

[![Clojars Project](http://clojars.org/clova/latest-version.svg)](http://clojars.org/clova)

## Usage

[API Docs](http://markwoodhall.github.io/clova)

Define a validation set. Validation sets are pairs of keys to validate
and the functions used to validate them.

```clojure
(let [validation-set (core/validation-set
                       [:email core/email?
                        :post-code core/post-code?
                        :zip-code core/zip-code?
                        :matches [core/matches? #"amatch"]
                        :url core/url?
                        :age [core/between? 18 40]
                        [:nested :value] [core/between? 0 10]])]

```

If you want to compose multiple validators you can. Currently you have to do so by specifying the validated key multiple times.

```clojure
(let [validation-set (core/validation-set
                       [:age [core/greater? 18]
                        :age [core/lesser? 30]])]

```

Most of the time it is useful to only apply and fail validation if a given key is present in the map getting validated, this is
the default behaviour. However if this is not the case and you wish to make a validator fail if the key is not present you can do.
Just use a `required?` validator as well.


```clojure
(let [validation-set (core/validation-set
                       [:email core/required?
                        :email core/email?])]

(let [validation-set (core/validation-set
                       [:age core/required?
                        :age [core/between? 18 30]])]
```

Use the validation set:

```clojure
(let [result (core/validate validation-set
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

You can also specify a custom function for providing validation error messages. This function will
be called with the validator type specified as an argument, if the custom function returns nil then
the default validation message will be used.

```clojure
(core/validate v-set {:email ""} {:default-message-fn (fn [v-type]
                                                        (case v-type
                                                          :email (str "custom email error")
                                                           nil))})
```

Get the validation status:

```clojure
(:valid? result)
```

Get the validation results (messages):

```clojure
(:results result)
```

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
9. [present?](http://markwoodhall.github.io/clova/clova.core.html#var-present.3F)
10. [url?](http://markwoodhall.github.io/clova/clova.core.html#var-url.3F)
11. [zip-code?](http://markwoodhall.github.io/clova/clova.core.html#var-zip-code.3F)
12. [length?](http://markwoodhall.github.io/clova/clova.core.html#var-length.3F)
13. [longer?](http://markwoodhall.github.io/clova/clova.core.html#var-longer.3F)
14. [shorter?](http://markwoodhall.github.io/clova/clova.core.html#var-shorter.3F)

## License

Copyright © 2015 Mark Woodhall

Released under the MIT License: http://www.opensource.org/licenses/mit-license.php
