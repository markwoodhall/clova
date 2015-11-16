(ns clova.core-test
  (:require #?(:cljs [cljs.test :as t]
                     :clj  [clojure.test :as t])
            [clova.core :as core]))

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

(t/deftest present-validator
  (t/testing "present validator exposes correct meta data"
    (t/is (= (dissoc exp-present-meta :target)
             (only-clova-meta (meta core/present?)))))

  (t/testing "validating a valid value"
    (doseq [value [1 2 true false "" "hello" {} [] {:a 1}]]
      (t/is (core/present? value))))

  (t/testing "validating an invalid value"
    (doseq [value [nil]]
      (t/is (not (core/present? value))))))

(t/deftest email-validator
  (t/testing "email validator exposes correct meta data"
    (t/is (= (dissoc exp-email-meta :target)
             (only-clova-meta (meta core/email?)))))

  (t/testing "validating a valid email address"
    (doseq [email ["test@googlemail.com" "test+test@googlemail.com"]]
      (t/is (core/email? email))))

  (t/testing "validating an invalid email address"
    (doseq [email [100 {:a 1} [1 2] "testing" "test@.googlemail.com" "@googlemail.com"]]
      (t/is (not (core/email? email))))))

(t/deftest zip-code-validator
  (t/testing "zip code validator exposes correct meta data"
    (t/is (= (dissoc exp-zip-meta :target)
             (only-clova-meta (meta core/zip-code?)))))

  (t/testing "validating a valid zip code"
    (doseq [zip (concat (range 96801 96830) (map str (range 96801 96830)))]
      (t/is (core/zip-code? zip))))

  (t/testing "validating an invalid zip code"
    (doseq [zip ["abc" 100 {:a 1} [1 2] "1-1-0"]]
      (t/is (not (core/zip-code? zip))))))

(t/deftest post-code-validator
  (t/testing "post code validator exposes correct meta data"
    (t/is (= (dissoc exp-post-meta :target)
             (only-clova-meta (meta core/post-code?)))))

  (t/testing "validating a valid uk post code"
    (doseq [post-code ["B11 2SB" "b11 2sb"]]
      (t/is (core/post-code? post-code))))

  (t/testing "validating an invalid uk post code"
    (doseq [post-code ["abc" 100 {:a 1} [1 2] "1-1-0" "B112SB" "b112sb"]]
      (t/is (not (core/post-code? post-code))))))

(t/deftest url-validator
  (t/testing "url validator exposes correct meta data"
    (t/is (= (dissoc exp-url-meta :target)
             (only-clova-meta (meta core/url?)))))

  (t/testing "validating a valid url "
    (doseq [url ["http://google.com" "https://www.google.com"]]
      (t/is (core/url? url))))

  (t/testing "validating an invalid url"
    (doseq [url ["aaaaasnnnnxnxx.c" "httpp://www.google.com"]]
      (t/is (not (core/url? url))))))

(t/deftest between-validator
  (t/testing "between validator exposes correct meta data"
    (t/is (= (dissoc exp-between-meta :target :args)
             (only-clova-meta (meta core/between?)))))

  (t/testing "validating a valid between value"
    (doseq [between [1 2 3 4 5 6 7 8 9]]
      (t/is (core/between? between 1 9))))

  (t/testing "validating an invalid between"
    (doseq [between [0 10 11 12 20 30 40]]
      (t/is (not (core/between? between 1 9))))))

(t/deftest greater-validator
  (t/testing "greater validator exposes correct meta data"
    (t/is (= (dissoc exp-greater-meta :target :args)
             (only-clova-meta (meta core/greater?)))))

  (t/testing "validating a valid greater value"
    (doseq [greater [1 2 3 4 5 6 7 8 9]]
      (t/is (core/greater? greater 0))))

  (t/testing "validating an invalid greater value"
    (doseq [greater [1 2 3 4 5 6 7 8 9]]
      (t/is (not (core/greater? greater 10))))))

(t/deftest matches-validator
  (t/testing "matches validator exposes correct meta data"
    (t/is (= (dissoc exp-matches-meta :target :args)
             (only-clova-meta (meta core/matches?)))))

  (t/testing "validating a value that matches"
    (t/is (core/matches? "amatch" #"amatch")))

  (t/testing "validating a value that does not match"
    (t/is (not (core/matches? "nonmatch" #"amatch")))))

(t/deftest one-of-validator
  (t/testing "one-of validator exposes correct meta data"
    (t/is (= (dissoc exp-one-of-meta :target :args)
             (only-clova-meta (meta core/one-of?)))))

  (t/testing "validating a value that is one of a collection"
    (t/is (core/one-of? "one" ["one" "two" "three"])))

  (t/testing "validating a value that is not one of a collection"
    (t/is (not (core/one-of? "nonmatch" ["one" "two" "three"])))))

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
                                    :present core/present?
                                    :count [core/greater? 2]
                                    [:nested :value] [core/between? 1 10]])]
    (t/testing "valid? returns correct result for a failure"
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
        (t/is (not valid))))

    (t/testing "valid? returns correct result for a success"
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
                                         :present nil
                                         :count 1
                                         :nested {:value 0}})]
        (t/is (not (:valid? result)))
        (t/is (= "email should be a valid email address." (first (:results result))))
        (t/is (= "post-code should be a valid post code." (second (:results result))))
        (t/is (= "zip-code should be a valid zip code." (nth (:results result) 2)))
        (t/is (= "matches is invalid value nomatch." (nth (:results result) 3)))
        (t/is (= "url should be a valid url." (nth (:results result) 4)))
        (t/is (= "age is 10 but it must be between 18 and 40." (nth (:results result) 5)))
        (t/is (= "one-of is 4 but should be one of [1 2 3]." (nth (:results result) 6)))
        (t/is (= "present is required." (nth (:results result) 7)))
        (t/is (= "count is 1 but it must be greater than 2." (nth (:results result) 8)))
        (t/is (= "nested value is 0 but it must be between 1 and 10." (nth (:results result) 9)))))

    (t/testing "validate using a validation set returns
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
        (t/is (:valid? result))
        (t/is (empty? (:results result)))))))

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
