(ns clova.core-test
  (:require #?(:cljs [cljs.test :as t]
               :clj  [clojure.test :as t])
            [clova.core :as core]))

(def only-clova-meta #(select-keys % [:clova.core/type :clova.core/default-message]))
(def only-clova-set-meta #(select-keys % [:clova.core/type :clova.core/target :clova.core/default-message :clova.core/args]))
(def exp-email-meta {:clova.core/type :email :clova.core/target :email :clova.core/default-message "%s should be a valid email address."})
(def exp-post-meta {:clova.core/type :post-code :clova.core/target :post-code :clova.core/default-message "%s should be a valid post code."})
(def exp-url-meta {:clova.core/type :url :clova.core/target :url :clova.core/default-message "%s should be a valid url."})
(def exp-greater-meta {:clova.core/type :greater :clova.core/target :count :clova.core/default-message "%s is %s but it must be greater than %s."})
(def exp-lesser-meta {:clova.core/type :lesser  :clova.core/target :count2 :clova.core/default-message "%s is %s but it must be less than %s."})
(def exp-between-meta {:clova.core/type :between :clova.core/args [1 9] :clova.core/target :age :clova.core/default-message "%s is %s but it must be between %s and %s."})
(def exp-matches-meta {:clova.core/type :matches :clova.core/target :matches :clova.core/default-message "%s is invalid value %s."})
(def exp-zip-meta {:clova.core/type :zip-code :clova.core/target :zip-code :clova.core/default-message "%s should be a valid zip code."})
(def exp-one-of-meta {:clova.core/type :one-of :clova.core/target :one-of :clova.core/default-message "%s is %s but should be one of %s."})
(def exp-not-nil-meta {:clova.core/type :not-nil :clova.core/target :not-nil :clova.core/default-message "%s is required."})
(def exp-required-meta {:clova.core/type :required :clova.core/target :required :clova.core/default-message "%s is required."})
(def exp-positive-meta {:clova.core/type :positive :clova.core/target :positive :clova.core/default-message "%s is %s but it should be a positive number."})
(def exp-negative-meta {:clova.core/type :negative :clova.core/target :negative :clova.core/default-message "%s is %s but it should be a negative number."})
(def exp-length-meta {:clova.core/type :length :clova.core/target :length :clova.core/default-message "%s is %s but it should have a length of %s."})
(def exp-longer-meta {:clova.core/type :longer :clova.core/target :longer :clova.core/default-message "%s is %s but it should have a length longer than %s."})
(def exp-shorter-meta {:clova.core/type :shorter :clova.core/target :shorter :clova.core/default-message "%s is %s but it should have a length shorter than %s."})
(def exp-all-meta {:clova.core/type :all :clova.core/target :all :clova.core/default-message "%s is %s but it does not meet all of the requirements."})
(def exp-cc-meta {:clova.core/type :credit-card :clova.core/target :credit-card :clova.core/default-message "%s is %s but it should be a valid credit card number."})

(t/deftest credit-card-validator
  (t/testing "credit card validator exposes correct meta data"
    (t/is (= (only-clova-meta exp-cc-meta)
             (only-clova-meta (meta core/credit-card?)))))

  (t/testing "validating a valid value"
    (doseq [cc ["5105 1051 0510 5100" "5105105105105100" "5105-1051-0510-5100"]]
      (t/is (core/credit-card? cc))))

  (t/testing "validating an invalid value"
    (doseq [cc [nil 1 "500 500 111 111"]]
      (t/is (not (core/credit-card? cc)))))

  (t/testing "validating an invalid value with other validators"
    (doseq [col [[[core/greater? 3] [core/lesser? 10]]]]
      (t/is (not (core/all? 2 col)))))

  (t/testing "validating an valid value with other validators"
    (doseq [col [[[core/greater? 3] [core/lesser? 10]]]]
      (t/is (core/all? 7 col)))))

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
    (doseq [value [:clova.core/key-not-found?]]
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
                                    [:nested :value] [core/between? 1 10]])]
    (t/testing "valid? returns correct result for a failure"
      (let [valid (core/valid? v-set {:email "abc"
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
                                      :nested {:value 0}})]
        (t/is (not valid))))

    (t/testing "valid? returns correct result for a success"
      (let [valid (core/valid? v-set {:email "test.email@googlemail.com"
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
                                      :nested {:value 5}})]
        (t/is valid)))

    (t/testing "validate using a validation set returns
               a valid? = false result and a sequence of the validation results"
      (let [result (core/validate v-set {:email "abc"
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
                                         :length "aaaa"
                                         :longer [1 2]
                                         :shorter "aaa"
                                         :all 4
                                         :credit-card 1
                                         :nested {:value 0}})]
        (t/is (not (:valid? result)))
        (t/is (= "email should be a valid email address." (first (:results result))))
        (t/is (= "post-code should be a valid post code." (second (:results result))))
        (t/is (= "zip-code should be a valid zip code." (nth (:results result) 2)))
        (t/is (= "matches is invalid value nomatch." (nth (:results result) 3)))
        (t/is (= "url should be a valid url." (nth (:results result) 4)))
        (t/is (= "age is 10 but it must be between 18 and 40." (nth (:results result) 5)))
        (t/is (= "one-of is 4 but should be one of [1 2 3]." (nth (:results result) 6)))
        (t/is (= "not-nil is required." (nth (:results result) 7)))
        (t/is (= "count is 1 but it must be greater than 2." (nth (:results result) 8)))
        (t/is (= "count2 is 1 but it must be less than 0." (nth (:results result) 9)))
        (t/is (= "positive is -1 but it should be a positive number." (nth (:results result) 10)))
        (t/is (= "negative is 1 but it should be a negative number." (nth (:results result) 11)))
        (t/is (= "length is aaaa but it should have a length of 3." (nth (:results result) 12)))
        (t/is (= "longer is [1 2] but it should have a length longer than 2." (nth (:results result) 13)))
        (t/is (= "shorter is aaa but it should have a length shorter than 2." (nth (:results result) 14)))
        (t/is (= "required is required." (nth (:results result) 15)))
        (t/is (= "all is 4 but it does not meet all of the requirements." (nth (:results result) 16)))
        (t/is (= "credit-card is 1 but it should be a valid credit card number." (nth (:results result) 17)))
        (t/is (= "nested value is 0 but it must be between 1 and 10." (nth (:results result) 18)))))

    (t/testing "validate using a validation set returns
               a valid? = true result and no validation results"
      (let [result (core/validate v-set {:email "test.email@googlemail.com"
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
                                         :length "aaa"
                                         :longer [1 2 3]
                                         :shorter "a"
                                         :required nil
                                         :all 5
                                         :credt-card "5105 1051 0510 5100"
                                         :nested {:value 5}})]
        (t/is (:valid? result))
        (t/is (empty? (:results result)))))

    (t/testing "validate uses a custom function for default message lookup"
      (let [v-set (core/validation-set [:email core/email? :not-nil core/not-nil?])
            result (core/validate v-set {:email "" :not-nil nil} {:default-message-fn (fn [v-type]
                                                                                        (case v-type
                                                                                          :email (str "custom email error")
                                                                                          nil))})]
        (t/is (= "not-nil is required." (second (:results result))))
        (t/is (= "custom email error" (first (:results result))))))

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
        (t/is (:valid? result))))))

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
