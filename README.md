# clova

A minimal validation library for Clojure and ClojureScript.

## Status

[![Build Status](https://api.travis-ci.org/markwoodhall/clova.svg?branch=master)](https://api.travis-ci.org/repositories/markwoodhall/clova)

## Usage

Define a validation set. Validation sets are pairs of keys to validate
and the functions used to validate them.

```clojure
(let [validation-set (core/validation-set
                       [:email core/email?
                        :post-code core/post-code?
                        :zip-code core/zip-code?
                        :matches [core/matches? #"amatch"]
                        :url core/url?
                        :age [core/between? 18 40]])
```

Use the validation set:

```clojure
(let [result (core/validate validation-set
                            {:email "test.email@googlemail.com"
                             :post-code "B11 2SB"
                             :matches "amatch"
                             :zip-code 96801
                             :url "http://google.com"
                             :age 21})
```

Get the validation status:

```clojure
(:valid? result)
```

Get the validation results (messages):

```clojure
(:results result)
```


## License

Copyright © 2015 Mark Woodhall

Released under the MIT License: http://www.opensource.org/licenses/mit-license.php
