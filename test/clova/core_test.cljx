(ns clova.core-test
  (:require [clova.core :as core]
            #+clj [clojure.test :refer [is deftest testing are]]
            #+cljs [cemerick.cljs.test :as t])
  #+cljs (:require-macros [cemerick.cljs.test :refer [is deftest testing are]]))

(def only-clova-meta #(select-keys % [:type :default-message]))
(def only-clova-set-meta #(select-keys % [:type :target :default-message :args]))
(def exp-email-meta {:type :email :target :email :default-message "%s should be a valid email address."})
(def exp-post-meta {:type :post-code :target :post-code :default-message "%s should be a valid post code."})
(def exp-url-meta {:type :url :target :url :default-message "%s should be a valid url."})
(def exp-greater-meta {:type :greater :args [1] :target :count :default-message "%s is %s but it must be greater than %s."})
(def exp-between-meta {:type :between :args [1 9] :target :age :default-message "%s is %s but it must be between %s and %s."})
(def exp-matches-meta {:type :matches :args [#"\w*"] :target :matches :default-message "%s is invalid value %s."})
(def exp-zip-meta {:type :zip-code :target :zip-code :default-message "%s should be a valid zip code."})
(def exp-one-of-meta {:type :one-of :target :one-of :default-message "%s is %s but should be one of %s."})
(def exp-present-meta {:type :present :target :present :default-message "%s is required."})

(deftest present-validator
  (testing "present validator exposes correct meta data"
    (is (= (dissoc exp-present-meta :target)
           (only-clova-meta (meta core/present?)))))

  (testing "validating a valid value"
    (doseq [value [1 2 true false "" "hello" {} [] {:a 1}]]
      (is (core/present? value))))

  (testing "validating an invalid value"
    (doseq [value [nil]]
      (is (not (core/present? value))))))

(deftest email-validator
  (testing "email validator exposes correct meta data"
    (is (= (dissoc exp-email-meta :target)
           (only-clova-meta (meta core/email?)))))

  (testing "validating a valid email address"
    (doseq [email ["test@googlemail.com" "test+test@googlemail.com"]]
      (is (core/email? email))))

  (testing "validating an invalid email address"
    (doseq [email [100 {:a 1} [1 2] "testing" "test@.googlemail.com" "@googlemail.com"]]
      (is (not (core/email? email))))))

(deftest zip-code-validator
  (testing "zip code validator exposes correct meta data"
    (is (= (dissoc exp-zip-meta :target)
           (only-clova-meta (meta core/zip-code?)))))

  (testing "validating a valid zip code"
    (doseq [zip (concat (range 96801 96830) (map str (range 96801 96830)))]
      (is (core/zip-code? zip))))

  (testing "validating an invalid zip code"
    (doseq [zip ["abc" 100 {:a 1} [1 2] "1-1-0"]]
      (is (not (core/zip-code? zip))))))

(deftest post-code-validator
  (testing "post code validator exposes correct meta data"
    (is (= (dissoc exp-post-meta :target)
           (only-clova-meta (meta core/post-code?)))))

  (testing "validating a valid uk post code"
    (doseq [post-code ["B11 2SB" "b11 2sb"]]
      (is (core/post-code? post-code))))

  (testing "validating an invalid uk post code"
    (doseq [post-code ["abc" 100 {:a 1} [1 2] "1-1-0" "B112SB" "b112sb"]]
      (is (not (core/post-code? post-code))))))

(deftest url-validator
  (testing "url validator exposes correct meta data"
    (is (= (dissoc exp-url-meta :target)
           (only-clova-meta (meta core/url?)))))

  (testing "validating a valid url "
    (doseq [url ["http://google.com" "https://www.google.com"]]
      (is (core/url? url))))

  (testing "validating an invalid url"
    (doseq [url ["aaaaasnnnnxnxx.c" "httpp://www.google.com"]]
      (is (not (core/url? url))))))

(deftest between-validator
  (testing "between validator exposes correct meta data"
    (is (= (dissoc exp-between-meta :target :args)
           (only-clova-meta (meta core/between?)))))

  (testing "validating a valid between value"
    (doseq [between [1 2 3 4 5 6 7 8 9]]
      (is (core/between? between 1 9))))

  (testing "validating an invalid between"
    (doseq [between [0 10 11 12 20 30 40]]
      (is (not (core/between? between 1 9))))))

(deftest greater-validator
  (testing "greater validator exposes correct meta data"
    (is (= (dissoc exp-greater-meta :target :args)
           (only-clova-meta (meta core/greater?)))))

  (testing "validating a valid greater value"
    (doseq [greater [1 2 3 4 5 6 7 8 9]]
      (is (core/greater? greater 0))))

  (testing "validating an invalid greater value"
    (doseq [greater [1 2 3 4 5 6 7 8 9]]
      (is (not (core/greater? greater 10))))))

(deftest matches-validator
  (testing "matches validator exposes correct meta data"
    (is (= (dissoc exp-matches-meta :target :args)
           (only-clova-meta (meta core/matches?)))))

  (testing "validating a value that matches"
    (is (core/matches? "amatch" #"amatch")))

  (testing "validating a value that does not match"
    (is (not (core/matches? "nonmatch" #"amatch")))))

(deftest one-of-validator
  (testing "one-of validator exposes correct meta data"
    (is (= (dissoc exp-one-of-meta :target :args)
           (only-clova-meta (meta core/one-of?)))))

  (testing "validating a value that is one of a collection"
    (is (core/one-of? "one" ["one" "two" "three"])))

  (testing "validating a value that is not one of a collection"
    (is (not (core/one-of? "nonmatch" ["one" "two" "three"])))))

(deftest validation-set
  (testing "validation set returns a sequence of the correct
           validation functions"
    (let [v-set (core/validation-set [:email core/email?
                                      :zip-code core/zip-code?
                                      :post-code core/post-code?])
          email-meta (meta (first v-set))
          zip-meta (meta (second v-set))
          post-code-meta (meta (nth v-set 2))]
      (is (= exp-email-meta (only-clova-set-meta email-meta)))
      (is (= exp-zip-meta (only-clova-set-meta zip-meta)))
      (is (= exp-post-meta (only-clova-set-meta post-code-meta)))))

  (testing "testing a validation set with multi arity returns a sequence of the correct
           validation functions"
    (let [v-set (core/validation-set [:age [core/between? 1 9]])
          between-meta (meta (first v-set))]
      (is (= exp-between-meta (only-clova-set-meta between-meta))))))

(deftest validation
  (let [v-set (core/validation-set [:email core/email?
                                    :post-code core/post-code?
                                    :zip-code core/zip-code?
                                    :matches [core/matches? #"amatch"]
                                    :url core/url?
                                    :age [core/between? 18 40]
                                    :one-of [core/one-of? [1 2 3]]
                                    :present core/present?
                                    :count [core/greater? 2]
                                    [:nested :value] [core/between? 1 10]])]
    (testing "valid? returns correct result for a failure"
      (let [valid (core/valid? v-set {:email "abc"
                                      :post-code 12
                                      :zip-code "abc"
                                      :matches "nomatch"
                                      :url "abc"
                                      :age 10
                                      :one-of 4
                                      :present nil
                                      :count 1
                                      :nested {:value 0}})]
        (is (not valid))))

    (testing "valid? returns correct result for a success"
      (let [valid (core/valid? v-set {:email "test.email@googlemail.com"
                                      :post-code "B11 2SB"
                                      :matches "amatch"
                                      :zip-code 96801
                                      :url "http://google.com"
                                      :age 21
                                      :one-of 1
                                      :present true
                                      :count 3
                                      :nested {:value 5}})]
        (is valid)))

    (testing "validate using a validation set returns
             a valid? = false result and a sequence of the validation results"
      (let [result (core/validate v-set {:email "abc"
                                         :post-code 12
                                         :zip-code "abc"
                                         :matches "nomatch"
                                         :url "abc"
                                         :age 10
                                         :one-of 4
                                         :present nil
                                         :count 1
                                         :nested {:value 0}})]
        (is (not (:valid? result)))
        (is (= "email should be a valid email address." (first (:results result))))
        (is (= "post-code should be a valid post code." (second (:results result))))
        (is (= "zip-code should be a valid zip code." (nth (:results result) 2)))
        (is (= "matches is invalid value nomatch." (nth (:results result) 3)))
        (is (= "url should be a valid url." (nth (:results result) 4)))
        (is (= "age is 10 but it must be between 18 and 40." (nth (:results result) 5)))
        (is (= "one-of is 4 but should be one of [1 2 3]." (nth (:results result) 6)))
        (is (= "present is required." (nth (:results result) 7)))
        (is (= "count is 1 but it must be greater than 2." (nth (:results result) 8)))
        (is (= "nested value is 0 but it must be between 1 and 10." (nth (:results result) 9)))))

    (testing "validate using a validation set returns
             a valid? = true result and no validation results"
      (let [result (core/validate v-set {:email "test.email@googlemail.com"
                                         :post-code "B11 2SB"
                                         :matches "amatch"
                                         :zip-code 96801
                                         :url "http://google.com"
                                         :age 21
                                         :one-of 1
                                         :present true
                                         :count 3
                                         :nested {:value 5}})]
        (is (:valid? result))
        (is (empty? (:results result)))))))
