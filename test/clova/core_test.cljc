(ns clova.core-test
  (:require #?(:cljs [cljs.test :as t])
            #?(:clj  [clojure.test :as t])
            #?(:clj  [clj-time.format :as f])
            #?(:cljs [cljs-time.format :as f])
            [clova.core :as core]))

(def only-clova-meta #(select-keys % [::core/type ::core/default-message]))
(def only-clova-set-meta #(select-keys % [::core/type ::core/target ::core/default-message ::core/args]))
(def exp-email-meta {::core/type :email ::core/target :email ::core/default-message "%s should be a valid email address."})
(def exp-post-meta {::core/type :post-code ::core/target :post-code ::core/default-message "%s should be a valid post code."})
(def exp-url-meta {::core/type :url ::core/target :url ::core/default-message "%s should be a valid url."})
(def exp-greater-meta {::core/type :greater ::core/target :count ::core/default-message "%s is %s but it must be greater than %s."})
(def exp-lesser-meta {::core/type :lesser  ::core/target :count2 ::core/default-message "%s is %s but it must be less than %s."})
(def exp-between-meta {::core/type :between ::core/args [1 9] ::core/target :age ::core/default-message "%s is %s but it must be between %s and %s."})
(def exp-matches-meta {::core/type :matches ::core/target :matches ::core/default-message "%s is invalid value %s."})
(def exp-zip-meta {::core/type :zip-code ::core/target :zip-code ::core/default-message "%s should be a valid zip code."})
(def exp-one-of-meta {::core/type :one-of ::core/target :one-of ::core/default-message "%s is %s but should be one of %s."})
(def exp-not-nil-meta {::core/type :not-nil ::core/target :not-nil ::core/default-message "%s is required."})
(def exp-required-meta {::core/type :required ::core/target :required ::core/default-message "%s is required."})
(def exp-positive-meta {::core/type :positive ::core/target :positive ::core/default-message "%s is %s but it should be a positive number."})
(def exp-negative-meta {::core/type :negative ::core/target :negative ::core/default-message "%s is %s but it should be a negative number."})
(def exp-length-meta {::core/type :length ::core/target :length ::core/default-message "%s is %s but it should have a length of %s."})
(def exp-longer-meta {::core/type :longer ::core/target :longer ::core/default-message "%s is %s but it should have a length longer than %s."})
(def exp-shorter-meta {::core/type :shorter ::core/target :shorter ::core/default-message "%s is %s but it should have a length shorter than %s."})
(def exp-all-meta {::core/type :all ::core/target :all ::core/default-message "%s is %s but it does not meet all of the requirements."})
(def exp-cc-meta {::core/type :credit-card ::core/target :credit-card ::core/default-message "%s is %s but it should be a valid credit card number."})
(def exp-numeric-meta {::core/type :numeric ::core/target :numeric ::core/default-message "%s is %s but it should be a number."})
(def exp-stringy-meta {::core/type :stringy ::core/target :stringy ::core/default-message "%s is %s but it should be a string."})
(def exp-alphanumeric-meta {::core/type :alphanumeric ::core/target :alphanumeric ::core/default-message "%s is %s but it should be an alphanumeric value."})
(def exp-default-as-validator-meta {::core/type :as-validator ::core/target :as-validator ::core/default-message "%s is %s but this is not a valid value."})
(def exp-as-validator-meta {::core/type :as-validator ::core/target :as-validator ::core/default-message "%s is %s but it should be XXX."})
(def exp-date-meta {::core/type :date ::core/target :date ::core/default-message "%s is %s but it should be a date."})
(def exp-before-meta {::core/type :before ::core/target :before ::core/default-message "%s is %s but it should be before %s."})
(def exp-after-meta {::core/type :after ::core/target :after ::core/default-message "%s is %s but it should be after %s."})
(def exp-=date-meta {::core/type :=date ::core/target :=date ::core/default-message "%s is %s but it should be %s."})
(def exp-=-meta {::core/type := ::core/target := ::core/default-message "%s is %s but it should be %s."})
(def exp-not-exists {::core/type :not-exists ::core/default-message "%s already exists."})

(t/deftest not-exists-validator
  (t/testing "not-exists validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-not-exists)
             (only-clova-meta (meta core/not-exists?)))))

  (t/testing "validating a valid value"
    (doseq [d ["1" "2" "99"]]
      (t/is (core/not-exists? d ["3" "5" "98"]))))

  (t/testing "validating an invalid value"
    (doseq [d ["1" "2" "99"]]
      (t/is (not (core/not-exists? d ["1" "2" "99"]))))))

(t/deftest =date-validator
  (t/testing "=date validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-=date-meta)
             (only-clova-meta (meta core/=date?)))))

  (t/testing "validating a valid value"
    (doseq [d [(f/parse "2015-01-01") "2015-01-01"]]
      (t/is (core/=date? d "2015-01-01"))
      (t/is (core/=date? d (f/parse "2015-01-01")))))

  (t/testing "validating an invalid value"
    (doseq [d ["2015-01-01" (f/parse "2014-01-01")]]
      (t/is (not (core/=date? d "2001-01-01")))
      (t/is (not (core/=date? d (f/parse "2001-01-01")))))))

(t/deftest =-validator
  (t/testing "= validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-=-meta)
             (only-clova-meta (meta core/=?)))))

  (t/testing "validating a valid value"
    (doseq [d [[1 1] ["1" "1"] [{:a 1} {:a 1}]]]
      (t/is (core/=? (first d) (last d)))))

  (t/testing "validating an invalid value"
    (doseq [d [[1 2] ["1" "2"] [{:a 1} {:a 2}]]]
      (t/is (not (core/=? (first d) (last d)))))))

(t/deftest before-validator
  (t/testing "before validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-before-meta)
             (only-clova-meta (meta core/before?)))))

  (t/testing "validating a valid value"
    (doseq [d [(f/parse "2015-01-01") "2011-01-01" "2014-12-12" "2001-01-24" #?(:clj (java.util.Date.)
                                                                                :cljs (js/Date.))]]
      (t/is (core/before? d "2020-01-01"))
      (t/is (core/before? d (f/parse "2020-01-01")))
      (t/is (core/before? d #?(:clj (java.util.Date.)
                               :cljs (js/Date.))))))

  (t/testing "validating an invalid value"
    (doseq [d ["2015-01-01" (f/parse "2014-01-01")]]
      (t/is (not (core/before? d "2001-01-01")))
      (t/is (not (core/before? d (f/parse "2001-01-01")))))))

(t/deftest after-validator
  (t/testing "after validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-after-meta)
             (only-clova-meta (meta core/after?)))))

  (t/testing "validating a valid value"
    (doseq [d [(f/parse "2015-01-01") "2011-01-01" "2014-12-12" "2001-01-24" #?(:clj (java.util.Date.)
                                                                                :cljs (js/Date.))]]
      (t/is (core/after? d "1900-01-01"))
      (t/is (core/after? d (f/parse "1901-01-01")))
      (t/is (core/after? #?(:clj (java.util.Date.)
                            :cljs (js/Date.)) d))))

  (t/testing "validating an invalid value"
    (doseq [d ["2015-01-01" (f/parse "2014-01-01")]]
      (t/is (not (core/after? d "2101-01-01")))
      (t/is (not (core/after? d (f/parse "2101-01-01")))))))

(t/deftest date-validator
  (t/testing "date validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-date-meta)
             (only-clova-meta (meta core/date?)))))

  (t/testing "validating a valid value"
    (doseq [d [(f/parse "2015-01-01") "2011-01-01" "2015-12-12" "2001-01-24" #?(:clj (java.util.Date.)
                                                                                :cljs (js/Date.))]]
      (t/is (core/date? d))))

  (t/testing "validating an invalid value"
    (doseq [d [nil {} [] {:a 1} [1 2 3]]]
      (t/is (not (core/date? d)))))

  (t/testing "validating an invalid value using custom date formatter"
    (doseq [d [nil {} [] {:a 1} [1 2 3] "211000000000"]]
      (t/is (not (core/date? d {:formatter (f/formatters :year-month-day)})))))

  (t/testing "validating a valid value using custom date formatter"
    (doseq [d ["2015-12-01" "2014-01-12" "2015-12-24"]]
      (t/is (core/date? d {:formatter (f/formatters :year-month-day)}))))

  (t/testing "validating a valid value using custom string date formatter"
    (doseq [d ["2015-12-01" "2014-01-12" "2015-12-24"]]
      (t/is (core/date? d {:formatter "yyyy-MM-dd"})))))

(t/deftest as-validator-validator
  (t/testing "as-validator validator exposes default meta data"
    (t/is (= (only-clova-meta exp-default-as-validator-meta)
             (only-clova-meta (meta (core/as-validator #(true)))))))
  (t/testing "as-validator validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-as-validator-meta)
             (only-clova-meta (meta (core/as-validator #(true) {:target :as-validator :default-message "%s is %s but it should be XXX."})))))))

(t/deftest stringy-validator
  (t/testing "stringy validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-stringy-meta)
             (only-clova-meta (meta core/stringy?)))))

  (t/testing "validating a valid value"
    (doseq [s ["a string" "a" "19298" "{:a 1}" "[a 1 4 3 b]"]]
      (t/is (core/stringy? s))))

  (t/testing "validating an invalid value"
    (doseq [s [nil {} [] {:a 1} [1 2 3]]]
      (t/is (not (core/stringy? s))))))

(t/deftest alphanumeric-validator
  (t/testing "alphanumeric validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-alphanumeric-meta)
             (only-clova-meta (meta core/alphanumeric?)))))

  (t/testing "validating a valid value"
    (doseq [s ["abc123" "a1b2c3" "123abc" "zesdsds"]]
      (t/is (core/alphanumeric? s))))

  (t/testing "validating an invalid value"
    (doseq [s ["£222dcds" "&*@2ncjkbvfd"]]
      (t/is (not (core/alphanumeric? s))))))

(t/deftest numeric-validator
  (t/testing "numeric validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-numeric-meta)
             (only-clova-meta (meta core/numeric?)))))

  (t/testing "validating a valid value"
    (doseq [n [1 2 5 9 0 4 -1 100 -50 3]]
      (t/is (core/numeric? n))))

  (t/testing "validating an invalid value"
    (doseq [n [nil {} [] "500" "1"]]
      (t/is (not (core/numeric? n))))))

(t/deftest credit-card-validator
  (t/testing "credit card validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-cc-meta)
             (only-clova-meta (meta core/credit-card?)))))

  (t/testing "validating a valid value"
    (doseq [cc ["5105 1051 0510 5100" "5105105105105100" "5105-1051-0510-5100"]]
      (t/is (core/credit-card? cc))))

  (t/testing "validating an invalid value"
    (doseq [cc [nil 1 "500 500 111 111"]]
      (t/is (not (core/credit-card? cc))))))

(t/deftest all-validator
  (t/testing "all validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-all-meta)
             (only-clova-meta (meta core/all?)))))

  (t/testing "validating a valid value"
    (doseq [col [true (fn [v] true) [true true] [(fn [v] true) (fn [v] true)]]]
      (t/is (core/all? true col))))

  (t/testing "validating an invalid value"
    (doseq [col [false (fn [v] false) [false false] [(fn [v] false) (fn [v] false)] [(fn [v] true) (fn [v] false)]]]
      (t/is (not (core/all? false col)))))

  (t/testing "validating an invalid value with other validators"
    (doseq [col [[[core/greater? 3] [core/lesser? 10]]]]
      (t/is (not (core/all? 2 col)))))

  (t/testing "validating an valid value with other validators"
    (doseq [col [[[core/greater? 3] [core/lesser? 10]]]]
      (t/is (core/all? 7 col)))))

(t/deftest required-validator
  (t/testing "required validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-required-meta)
             (only-clova-meta (meta core/required?)))))

  (t/testing "validating a valid value"
    (doseq [value [1 2 true false "" "hello" {} [] {:a 1}]]
      (t/is (core/required? value))))

  (t/testing "validating an invalid value"
    (doseq [value [::core/key-not-found?]]
      (t/is (not (core/required? value))))))

(t/deftest not-nil-validator
  (t/testing "not-nil validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-not-nil-meta)
             (only-clova-meta (meta core/not-nil?)))))

  (t/testing "validating a valid value"
    (doseq [value [1 2 true false "" "hello" {} [] {:a 1}]]
      (t/is (core/not-nil? value))))

  (t/testing "validating an invalid value"
    (doseq [value [nil]]
      (t/is (not (core/not-nil? value))))))

(t/deftest email-validator
  (t/testing "email validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-email-meta)
             (only-clova-meta (meta core/email?)))))

  (t/testing "validating a valid email address"
    (doseq [email ["test@googlemail.com" "test+test@googlemail.com"]]
      (t/is (core/email? email))))

  (t/testing "validating an invalid email address"
    (doseq [email [nil 100 {:a 1} [1 2] "testing" "test@.googlemail.com" "@googlemail.com"]]
      (t/is (not (core/email? email))))))

(t/deftest zip-code-validator
  (t/testing "zip code validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-zip-meta)
             (only-clova-meta (meta core/zip-code?)))))

  (t/testing "validating a valid zip code"
    (doseq [zip (concat (range 96801 96830) (map str (range 96801 96830)))]
      (t/is (core/zip-code? zip))))

  (t/testing "validating an invalid zip code"
    (doseq [zip [nil "abc" 100 {:a 1} [1 2] "1-1-0"]]
      (t/is (not (core/zip-code? zip))))))

(t/deftest post-code-validator
  (t/testing "post code validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-post-meta)
             (only-clova-meta (meta core/post-code?)))))

  (t/testing "validating a valid uk post code"
    (doseq [post-code ["B11 2SB" "b11 2sb"]]
      (t/is (core/post-code? post-code))))

  (t/testing "validating an invalid uk post code"
    (doseq [post-code [nil "abc" 100 {:a 1} [1 2] "1-1-0" "B112SB" "b112sb"]]
      (t/is (not (core/post-code? post-code))))))

(t/deftest url-validator
  (t/testing "url validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-url-meta)
             (only-clova-meta (meta core/url?)))))

  (t/testing "validating a valid url "
    (doseq [url ["http://google.com" "https://www.google.com"]]
      (t/is (core/url? url))))

  (t/testing "validating an invalid url"
    (doseq [url [nil "aaaaasnnnnxnxx.c" "httpp://www.google.com"]]
      (t/is (not (core/url? url))))))

(t/deftest between-validator
  (t/testing "between validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-between-meta)
             (only-clova-meta (meta core/between?)))))

  (t/testing "validating a valid between value"
    (doseq [value [1 2 3 4 5 6 7 8 9]]
      (t/is (core/between? value 1 9))))

  (t/testing "validating an invalid between"
    (doseq [value [0 10 11 12 20 30 40 nil]]
      (t/is (not (core/between? value 1 9)))))

  (t/testing "validating with nil lower and upper bounds"
    (doseq [value [0 10 11 12 20 30 40 nil]]
      (t/is (not (core/between? value nil nil))))))

(t/deftest greater-validator
  (t/testing "greater validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-greater-meta)
             (only-clova-meta (meta core/greater?)))))

  (t/testing "validating a valid greater value"
    (doseq [value [1 2 3 4 5 6 7 8 9]]
      (t/is (core/greater? value 0))))

  (t/testing "validating an invalid greater value"
    (doseq [value [nil 1 2 3 4 5 6 7 8 9]]
      (t/is (not (core/greater? value 10)))))

  (t/testing "validating with nil greater"
    (doseq [value [nil 1 2 3 4 5 6 7 8 9]]
      (t/is (not (core/greater? value nil))))))

(t/deftest lesser-validator
  (t/testing "lesser validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-lesser-meta)
             (only-clova-meta (meta core/lesser?)))))

  (t/testing "validating a valid lesser value"
    (doseq [value [1 2 3 4 5 6 7 8 9]]
      (t/is (core/lesser? value 10))))

  (t/testing "validating an invalid lesser value"
    (doseq [value [nil 1 2 3 4 5 6 7 8 9]]
      (t/is (not (core/lesser? value 0)))))

  (t/testing "validating with nil lesser"
    (doseq [value [nil 1 2 3 4 5 6 7 8 9]]
      (t/is (not (core/lesser? value nil))))))

(t/deftest positive-validator
  (t/testing "positive validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-positive-meta)
             (only-clova-meta (meta core/positive?)))))

  (t/testing "validating a valid positive value"
    (doseq [positive [1 2 3 4 5 6 7 8 9]]
      (t/is (core/positive? positive))))

  (t/testing "validating an invalid positive value"
    (doseq [not-positive [nil 0 -1 -2 -10 -20 -100 -200]]
      (t/is (not (core/positive? not-positive))))))


(t/deftest negative-validator
  (t/testing "negative validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-negative-meta)
             (only-clova-meta (meta core/negative?)))))

  (t/testing "validating a valid negative value"
    (doseq [negative [-1 -2 -3 -4 -5 -6 -7 -8 -9]]
      (t/is (core/negative? negative))))

  (t/testing "validating an invalid negative value"
    (doseq [not-negative [nil 0 1 2 3 4 5 6 7 8 9]]
      (t/is (not (core/negative? not-negative))))))

(t/deftest matches-validator
  (t/testing "matches validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-matches-meta)
             (only-clova-meta (meta core/matches?)))))

  (t/testing "validating a value that matches"
    (t/is (core/matches? "amatch" #"amatch")))

  (t/testing "validating a value that does not match"
    (doseq [v ["nonmatch" nil]]
      (t/is (not (core/matches? "nonmatch" #"amatch"))))))

(t/deftest one-of-validator
  (t/testing "one-of validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-one-of-meta)
             (only-clova-meta (meta core/one-of?)))))

  (t/testing "validating a value that is one of a collection"
    (t/is (core/one-of? "one" ["one" "two" "three"])))

  (t/testing "validating a value that is not one of a collection"
    (doseq [v ["nonmatch" nil]]
      (t/is (not (core/one-of? "nonmatch" ["one" "two" "three"]))))))

(t/deftest length-validator
  (t/testing "length validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-length-meta)
             (only-clova-meta (meta core/length?)))))

  (t/testing "validating a value that is shorter or longer"
    (doseq [v [nil "aaaa" "aa" [1 2] [1 2 3 4]]]
            (t/is (not (core/length? v 3)))))

  (t/testing "validating a value that is the correct length"
    (doseq [v ["aaa" "bbb" [1 2 3] ["one" "two" "three"]]]
            (t/is (core/length? v 3))))

  (t/testing "validating nil length"
    (doseq [v ["aaa" "bbb" [1 2 3] ["one" "two" "three"]]]
            (t/is (not (core/length? v nil))))))

(t/deftest longer-validator
  (t/testing "longer validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-longer-meta)
             (only-clova-meta (meta core/longer?)))))

  (t/testing "validating a value that is shorter or of equal length"
    (doseq [v [nil "aaaa" "aa" [1 2] [1 2 3 4]]]
            (t/is (not (core/longer? v 4)))))

  (t/testing "validating a value that is longer"
    (doseq [v ["aaa" "bbb" [1 2 3] ["one" "two" "three"]]]
            (t/is (core/longer? v 2))))

  (t/testing "validating nil longer"
    (doseq [v ["aaa" "bbb" [1 2 3] ["one" "two" "three"]]]
            (t/is (not (core/longer? v nil))))))

(t/deftest shorter-validator
  (t/testing "shorter validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-shorter-meta)
             (only-clova-meta (meta core/shorter?)))))

  (t/testing "validating a value that is longer or of equal length"
    (doseq [v [nil "aaaa" "aa" [1 2] [1 2 3 4]]]
            (t/is (not (core/shorter? v 2)))))

  (t/testing "validating a value that is shorter"
    (doseq [v ["aaa" "bbb" [1 2 3] ["one" "two" "three"]]]
            (t/is (core/shorter? v 4))))

  (t/testing "validating nil shorter"
    (doseq [v ["aaa" "bbb" [1 2 3] ["one" "two" "three"]]]
            (t/is (not (core/shorter? v nil))))))

(t/deftest validation-set
  (t/testing "validation set returns a sequence of the correct
             validation functions"
    (let [v-set (core/validation-set [:email core/email?
                                      :zip-code core/zip-code?
                                      :post-code core/post-code?])
          email-meta (meta (first v-set))
          zip-meta (meta (second v-set))
          post-code-meta (meta (nth v-set 2))]
      (t/is (= exp-email-meta (only-clova-set-meta email-meta)))
      (t/is (= exp-zip-meta (only-clova-set-meta zip-meta)))
      (t/is (= exp-post-meta (only-clova-set-meta post-code-meta)))))

  (t/testing "testing a validation set with multi arity returns a sequence of the correct
             validation functions"
    (let [v-set (core/validation-set [:age [core/between? 1 9]])
          between-meta (meta (first v-set))]
      (t/is (= exp-between-meta (only-clova-set-meta between-meta))))))

(t/deftest validation
  (let [v-set (core/validation-set [:email core/email?
                                    :post-code core/post-code?
                                    :zip-code core/zip-code?
                                    :matches [core/matches? #"amatch"]
                                    :url core/url?
                                    :age [core/between? 18 40]
                                    :one-of [core/one-of? [1 2 3]]
                                    :not-nil core/not-nil?
                                    :count [core/greater? 2]
                                    :count2 [core/lesser? 0]
                                    :positive core/positive?
                                    :negative core/negative?
                                    :length [core/length? 3]
                                    :longer [core/longer? 2]
                                    :shorter [core/shorter? 2]
                                    :required [core/required?]
                                    :all [core/all? [(fn[v] (= v 5))]]
                                    :credit-card [core/credit-card?]
                                    :numeric core/numeric?
                                    :stringy core/stringy?
                                    :alphanumeric core/alphanumeric?
                                    :as-validator (core/as-validator #(= % 1))
                                    :after [core/after? "2015-01-01"]
                                    :before [core/before? "2015-01-01"]
                                    :=date [core/=date? "2015-01-01"]
                                    := [core/=? 1]
                                    [:nested :value] [core/between? 1 10]
                                    :function [> 5]])
        invalid-map {:email "abc"
                     :post-code 12
                     :zip-code "abc"
                     :matches "nomatch"
                     :url "abc"
                     :age 10
                     :one-of 4
                     :not-nil nil
                     :count 1
                     :count2 1
                     :positive -1
                     :negative 1
                     :length  "aaaaa"
                     :longer [1 2]
                     :shorter "aaa"
                     :all 4
                     :credit-card 1
                     :numeric ""
                     :stringy 1
                     :alphanumeric "!abc"
                     :as-validator 2
                     :after "2014-01-01"
                     :before "2016-01-01"
                     :=date "2016-01-01"
                     := 2
                     :nested {:value 0}
                     :function 4}
        valid-map {:email "test.email@googlemail.com"
                   :post-code "B11 2SB"
                   :matches "amatch"
                   :zip-code 96801
                   :url "http://google.com"
                   :age 21
                   :one-of 1
                   :not-nil true
                   :count 3
                   :count2 -1
                   :positive 1
                   :negative -1
                   :length  "aaa"
                   :longer [1 2 3]
                   :shorter "a"
                   :required nil
                   :all 5
                   :credt-card "5105 1051 0510 5100"
                   :numeric 1
                   :stringy "abc"
                   :alphanumeric "abc123"
                   :as-validator 1
                   :after "2015-01-02"
                   :before "2014-01-02"
                   :=date "2015-01-01"
                   := 1
                   :nested {:value 5}
                   :function 6}]
    (t/testing "valid? returns correct result for a failure"
      (let [valid (core/valid? v-set invalid-map)]
        (t/is (not valid))))

    (t/testing "valid? returns correct result for a success"
      (let [valid (core/valid? v-set valid-map)]
        (t/is valid)))

    (t/testing "validate using a validation set returns
               a valid? = false result and a sequence of the validation results"
      (let [result (core/validate v-set invalid-map)
            results (:results result)]
        (t/is (not (:valid? result)))
        (t/is (= "email should be a valid email address." (first results)))
        (t/is (= "post-code should be a valid post code." (second results)))
        (t/is (= "zip-code should be a valid zip code." (nth results 2)))
        (t/is (= "matches is invalid value nomatch." (nth results 3)))
        (t/is (= "url should be a valid url." (nth results 4)))
        (t/is (= "age is 10 but it must be between 18 and 40." (nth results 5)))
        (t/is (= "one-of is 4 but should be one of [1 2 3]." (nth results 6)))
        (t/is (= "not-nil is required." (nth results 7)))
        (t/is (= "count is 1 but it must be greater than 2." (nth results 8)))
        (t/is (= "count2 is 1 but it must be less than 0." (nth results 9)))
        (t/is (= "positive is -1 but it should be a positive number." (nth results 10)))
        (t/is (= "negative is 1 but it should be a negative number." (nth results 11)))
        (t/is (= "length is aaaaa but it should have a length of 3." (nth results 12)))
        (t/is (= "longer is [1 2] but it should have a length longer than 2." (nth results 13)))
        (t/is (= "shorter is aaa but it should have a length shorter than 2." (nth results 14)))
        (t/is (= "required is required." (nth results 15)))
        (t/is (= "all is 4 but it does not meet all of the requirements." (nth results 16)))
        (t/is (= "credit-card is 1 but it should be a valid credit card number." (nth results 17)))
        (t/is (= "numeric is  but it should be a number." (nth results 18)))
        (t/is (= "stringy is 1 but it should be a string." (nth results 19)))
        (t/is (= "alphanumeric is !abc but it should be an alphanumeric value." (nth results 20)))
        (t/is (= "as-validator is 2 but this is not a valid value." (nth results 21)))
        (t/is (= "after is 2014-01-01 but it should be after 2015-01-01." (nth results 22)))
        (t/is (= "before is 2016-01-01 but it should be before 2015-01-01." (nth results 23)))
        (t/is (= "=date is 2016-01-01 but it should be 2015-01-01." (nth results 24)))
        (t/is (= "= is 2 but it should be 1." (nth results 25)))
        (t/is (= "nested value is 0 but it must be between 1 and 10." (nth results 26)))
        (t/is (= "function has value 4, which is invalid." (nth results 27)))))

    (t/testing "validate using a validation set returns
               a valid? = true result and no validation results"
      (let [result (core/validate v-set valid-map)]
        (t/is (:valid? result))
        (t/is (empty? (:results result)))))

    (t/testing "validate uses a custom function for default message lookup"
      (let [v-set (core/validation-set [:email core/email? :not-nil core/not-nil? :age [core/between? 1 9]])
            get-message (fn [v-type value args]
                          (case v-type
                            :email (str value " is not an email address")
                            :between (str value " should be between " (first args) " and " (second args))
                            nil))
            result (core/validate v-set {:email "dave" :age 10 :not-nil nil} {:default-message-fn get-message})]
        (t/is (= "not-nil is required." (second (:results result))))
        (t/is (= "dave is not an email address" (first (:results result))))
        (t/is (= "10 should be between 1 and 9" (nth (:results result) 2)))))

    (t/testing "validate short circuits if configured"
      (let [v-set (core/validation-set [:email core/email? :not-nil core/not-nil?])
            result (core/validate v-set {:email "" :not-nil nil} {:short-circuit? true})]
        (t/is (= "email should be a valid email address." (first (:results result))))
        (t/is (=  1 (count (:results result))))))

    (t/testing "validate respects allow missing keys so the only failure is for a required field"
      (let [result (core/validate v-set {})]
        (t/is (not (:valid? result)))
        (t/is (= (count (:results result)) 1))
        (t/is (= (first (:results result)) "required is required."))))

    (t/testing "validate respects allow missing keys when using a required combination"
      (let [v-set (core/validation-set [:email core/email?
                                        :email core/required?])
            result (core/validate v-set {})]
        (t/is (not (:valid? result)))))

    (t/testing "validate supports a failing validation set with duplicate keys and multiple validators"
      (let [v-set (core/validation-set [:test [core/greater? 2]
                                        :test [core/lesser? 5]])
            result (core/validate v-set {:test 6})]
        (t/is (not (:valid? result)))))

    (t/testing "validate supports a validation set with duplicate keys and multiple validators"
      (let [v-set (core/validation-set [:test [core/greater? 2]
                                        :test [core/lesser? 5]])
            result (core/validate v-set {:test 4})]
        (t/is (:valid? result))))

    (t/testing "validate supports a validation set with regular functions"
      (let [v-set (core/validation-set [:test [> 2]])
            result (core/validate v-set {:test 1})]
        (t/is (not (:valid? result)))))

    (t/testing "validate supports a validation set with functional args"
      (let [db {:users ["test@email.com"]}
            users (fn [value]
                    (filter #{value} (:users db)))
            v-set (core/validation-set [:email [core/not-exists? users]])
            result (core/validate v-set {:email "test2@email.com"})
            result2 (core/validate v-set {:email "test@email.com"})]
        (t/is (:valid? result))
        (t/is (not (:valid? result2)))
        (t/is (first (:results result2)) "test@email.com already exists")))))

#?(:cljs
    (do
      (enable-console-print!)
      (set! *main-cli-fn* #(t/run-tests))))

#?(:cljs
    (defmethod t/report [:cljs.test/default :end-run-tests]
      [m]
      (if (t/successful? m)
        (set! (.-exitCode js/process) 0)
        (set! (.-exitCode js/process) 1))))
